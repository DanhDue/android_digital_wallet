import 'dart:io';
import 'package:mason/mason.dart';

/// Post-generation wiring for the `mvi_feature` brick.
///
/// Every generated feature is auto-wired into the host the governance way and
/// never touches another feature's files (epic design §7, §4.4):
///
/// * **`delivery: install-time`** (default) — the module is a plain
///   `com.android.library` feature. The hook adds it to `settings.gradle.kts`,
///   `Deps.kt`, `DependencyHandlerExtensions.kt`, `app/build.gradle.kts` and (when
///   present) `shell/build.gradle.kts` as an `implementation(project(...))`
///   dependency; Hilt `@Provides @IntoSet EntryProviderInstaller` multibinding
///   aggregates its navigation entries; Hilt `@Provides @IntoSet DeepLinkResolver`
///   exposes its deep link resolution; its route is appended to `AppRoutes.kt`,
///   and its entry point is added to `AppDeepLinks.kt`.
///
/// * **`delivery: on-demand`** — the module is turned into a
///   `com.android.dynamic-feature` split, structurally identical to the
///   template's worked example `:features:scanner` (Task 14, design §4.4). The
///   hook rewrites its `build.gradle.kts` + `AndroidManifest.xml`, strips every
///   Hilt annotation from the generated code (a downloaded split never joins the
///   host Hilt graph), generates
///   `<package>.presentation.di.<Name>FeatureEntry : FeatureEntry` (providing
///   both installer and deep link resolver) plus a
///   `META-INF/services` registration, registers the module in `:app`
///   `android.dynamicFeatures`, forces a `<Name>Route : NavKey` constant into
///   `:platform` `AppRoutes`, adds an entry point to `AppDeepLinks.kt`, and
///   drops a `:shell` helper that installs the split at runtime through
///   `FeatureInstaller.ensureInstalled(...)`
///   (`SplitInstallManager` + `SplitCompat`) exactly like the scanner tab.
void run(HookContext context) {
  final name = context.vars['name'] as String;
  final packageName = context.vars['package'] as String;
  final screen = context.vars['screen'] as String? ?? 'Main';
  final delivery = _resolveDelivery(context.vars['delivery'], context.logger);

  final pascalCase = _toPascalCase(name);
  final snakeCase = _toSnakeCase(name);
  final upperSnakeCase = snakeCase.toUpperCase();
  final screenPascal = _toPascalCase(screen);
  final screenCamel = _toCamelCase(screen);

  final gradlePath = ':features:$snakeCase';
  final modulePath = 'features/$snakeCase';

  context.logger.info('🔧 Configuring module: $gradlePath  (delivery: $delivery)');

  // 1. Always: register the module in settings.gradle.kts.
  _updateSettingsGradle(gradlePath, context.logger);
  if (delivery != 'on-demand') {
    _updateSettingsGradle('$gradlePath:sample', context.logger);
  }

  // 2. Always: register entry route in AppRoutes.kt and entry point in AppDeepLinks.kt
  _appendRouteToAppRoutes(pascalCase, context.logger);
  _appendDeepLinkEntryPoint(
    snakeCase: snakeCase,
    pascalCase: pascalCase,
    isDfm: delivery == 'on-demand',
    logger: context.logger,
  );

  if (delivery == 'on-demand') {
    _configureOnDemand(
      context: context,
      gradlePath: gradlePath,
      modulePath: modulePath,
      packageName: packageName,
      pascalCase: pascalCase,
      snakeCase: snakeCase,
      screenPascal: screenPascal,
      screenCamel: screenCamel,
    );
  } else {
    _configureInstallTime(
      logger: context.logger,
      gradlePath: gradlePath,
      pascalCase: pascalCase,
      upperSnakeCase: upperSnakeCase,
    );
  }

  context.logger.success('✅ Module configuration complete!');

  // Final: refresh the Gradle build-script model so the IDE picks the module up.
  context.logger.info('');
  context.logger.info('🔄 Running Gradle sync...');
  final result = Process.runSync(
    './gradlew',
    [':prepareKotlinBuildScriptModel', '--console=plain'],
    runInShell: true,
  );

  if (result.exitCode == 0) {
    context.logger.success('✅ Gradle sync complete!');
  } else {
    context.logger.warn(
        '⚠️ Gradle sync failed. Run manually: ./gradlew :prepareKotlinBuildScriptModel --console=plain');
  }
}

// ---------------------------------------------------------------------------
// delivery resolution
// ---------------------------------------------------------------------------

/// Normalises the `delivery` var to exactly `install-time` or `on-demand`.
///
/// An unset or unrecognised value falls back to `install-time` with a warning
/// (equivalence partitioning: {unset}, {install-time}, {on-demand}, {garbage}).
String _resolveDelivery(Object? raw, Logger logger) {
  final value = (raw as String?)?.trim().toLowerCase() ?? '';
  switch (value) {
    case '':
    case 'install-time':
    case 'install_time':
    case 'installtime':
      return 'install-time';
    case 'on-demand':
    case 'on_demand':
    case 'ondemand':
    case 'dfm':
      return 'on-demand';
    default:
      logger.warn(
        '⚠️ Unknown delivery "$raw" — falling back to "install-time". '
        'Valid values: install-time | on-demand.',
      );
      return 'install-time';
  }
}

// ---------------------------------------------------------------------------
// install-time wiring
// ---------------------------------------------------------------------------

void _configureInstallTime({
  required Logger logger,
  required String gradlePath,
  required String pascalCase,
  required String upperSnakeCase,
}) {
  _updateDepsKt(pascalCase, gradlePath, logger);
  _updateDependencyHandler(pascalCase, upperSnakeCase, logger);
  _addFeatureAccessorToBuildFile(
    File('app/build.gradle.kts'),
    upperSnakeCase,
    logger,
    label: 'app/build.gradle.kts',
  );
  // `:shell` does not exist until Phase 2 — guard the edit so it is a no-op
  // today and takes effect automatically once the module lands.
  final shellBuildFile = File('shell/build.gradle.kts');
  if (shellBuildFile.existsSync()) {
    _addFeatureAccessorToBuildFile(
      shellBuildFile,
      upperSnakeCase,
      logger,
      label: 'shell/build.gradle.kts',
    );
  } else {
    logger.info('✓ :shell absent — skipping shell/build.gradle.kts wiring (Phase 2)');
  }
}

void _updateSettingsGradle(String gradlePath, Logger logger) {
  final file = File('settings.gradle.kts');
  if (!file.existsSync()) {
    logger.warn('settings.gradle.kts not found');
    return;
  }

  var content = file.readAsStringSync();
  final include = 'include("$gradlePath")';

  if (!content.contains(include)) {
    content = '$content$include\n';
    file.writeAsStringSync(content);
    logger.info('📝 Added to settings.gradle.kts');
  } else {
    logger.info('✓ Already in settings.gradle.kts');
  }
}

void _updateDepsKt(String pascalCase, String gradlePath, Logger logger) {
  final file = File('buildSrc/src/main/kotlin/Deps.kt');
  if (!file.existsSync()) {
    logger.warn('Deps.kt not found');
    return;
  }

  var content = file.readAsStringSync();
  final moduleConst = 'feature$pascalCase';
  final constLine = '    const val $moduleConst = "$gradlePath"';

  if (!content.contains('const val $moduleConst')) {
    final modulesEnd = content.lastIndexOf('}');
    if (modulesEnd != -1) {
      content =
          '${content.substring(0, modulesEnd)}$constLine\n${content.substring(modulesEnd)}';
      file.writeAsStringSync(content);
      logger.info('📝 Added to Deps.kt');
    }
  } else {
    logger.info('✓ Already in Deps.kt');
  }
}

void _updateDependencyHandler(
  String pascalCase,
  String upperSnakeCase,
  Logger logger,
) {
  final file = File(
    'buildSrc/src/main/kotlin/extensions/DependencyHandlerExtensions.kt',
  );
  if (!file.existsSync()) {
    logger.warn('DependencyHandlerExtensions.kt not found');
    return;
  }

  var content = file.readAsStringSync();
  final accessorName = 'FEATURE_$upperSnakeCase';
  final moduleConst = 'feature$pascalCase';

  if (!content.contains('val DependencyHandler.$accessorName')) {
    content = '''$content
val DependencyHandler.$accessorName
    get() = implementation(project(mapOf(PATH to Modules.$moduleConst)))
''';
    file.writeAsStringSync(content);
    logger.info('📝 Added to DependencyHandlerExtensions.kt');
  } else {
    logger.info('✓ Already in DependencyHandlerExtensions.kt');
  }
}

/// Adds `import extensions.FEATURE_<NAME>` + a bare `FEATURE_<NAME>` accessor to
/// a host build file (`app` today, `shell` from Phase 2). Idempotent; mirrors the
/// existing formatting of the `FEATURE_*` block.
void _addFeatureAccessorToBuildFile(
  File file,
  String upperSnakeCase,
  Logger logger, {
  required String label,
}) {
  if (!file.existsSync()) {
    logger.warn('$label not found');
    return;
  }

  var content = file.readAsStringSync();
  final accessorName = 'FEATURE_$upperSnakeCase';
  final importLine = 'import extensions.$accessorName';
  var updated = false;

  if (!content.contains(importLine)) {
    final importPattern = RegExp(r'import extensions\.FEATURE_\w+');
    final matches = importPattern.allMatches(content).toList();
    if (matches.isNotEmpty) {
      final lastMatch = matches.last;
      content =
          '${content.substring(0, lastMatch.end)}\n$importLine${content.substring(lastMatch.end)}';
      updated = true;
    }
  }

  if (!content.contains('    $accessorName\n')) {
    final featurePattern = RegExp(r'    FEATURE_\w+\n');
    final matches = featurePattern.allMatches(content).toList();
    if (matches.isNotEmpty) {
      final lastMatch = matches.last;
      content =
          '${content.substring(0, lastMatch.end)}    $accessorName\n${content.substring(lastMatch.end)}';
      updated = true;
    }
  }

  if (updated) {
    file.writeAsStringSync(content);
    logger.info('📝 Added $accessorName to $label');
  } else {
    logger.info('✓ $accessorName already in $label');
  }
}

// ---------------------------------------------------------------------------
// on-demand (Dynamic Feature Module) wiring
// ---------------------------------------------------------------------------

void _configureOnDemand({
  required HookContext context,
  required String gradlePath,
  required String modulePath,
  required String packageName,
  required String pascalCase,
  required String snakeCase,
  required String screenPascal,
  required String screenCamel,
}) {
  final logger = context.logger;

  final sampleDir = Directory('$modulePath/sample');
  if (sampleDir.existsSync()) {
    sampleDir.deleteSync(recursive: true);
    logger.info('🗑  Removed sample/ (DFMs are tested via the host :app split runner)');
  }

  _writeDynamicFeatureBuildGradle(modulePath, packageName, logger);
  _writeDynamicFeatureManifest(modulePath, snakeCase, logger);
  _writeBaseModuleSplitTitle(snakeCase, pascalCase, logger);
  // A downloaded split never joins the host Hilt graph — strip every Hilt
  // annotation / module the base brick generated and replace them with a plain
  // AndroidX `viewModel()` + a manual `di/<Name>ViewModelFactory` (mirrors
  // `:features:scanner`, Task 14 / design §4.4).
  _stripHiltForOnDemand(
    modulePath: modulePath,
    packageName: packageName,
    pascalCase: pascalCase,
    screenPascal: screenPascal,
    screenCamel: screenCamel,
    logger: logger,
  );
  _writeFeatureEntry(
    modulePath: modulePath,
    packageName: packageName,
    pascalCase: pascalCase,
    screenPascal: screenPascal,
    screenCamel: screenCamel,
    logger: logger,
  );
  _registerFeatureEntryWithServiceLoader(packageName, pascalCase, logger);
  _registerDynamicFeatureInApp(gradlePath, logger);
  _appendRouteToAppRoutes(pascalCase, logger);
  _wireShellInstallBranch(snakeCase, pascalCase, logger);
}

/// Deletes the Hilt `@Module`s the base brick generated (`presentation/di`,
/// `data/di`, `domain/di`) plus the feature-local `<Screen>Route.kt` (the route
/// now lives in `:platform` `AppRoutes`), and rewrites the ViewModel, Screen,
/// repository impl and use case Hilt-free. Adds `di/<Name>ViewModelFactory.kt`
/// outside the layer packages so it may legitimately wire all three layers
/// (Konsist K2).
void _stripHiltForOnDemand({
  required String modulePath,
  required String packageName,
  required String pascalCase,
  required String screenPascal,
  required String screenCamel,
  required Logger logger,
}) {
  final dir = packageName.replaceAll('.', '/');
  final src = '$modulePath/src/main/kotlin/$dir';

  for (final relative in [
    'presentation/di/${pascalCase}NavigationModule.kt',
    'data/di/${pascalCase}DataModule.kt',
    'domain/di/${pascalCase}DomainModule.kt',
    'presentation/$screenCamel/${screenPascal}Route.kt',
  ]) {
    final f = File('$src/$relative');
    if (f.existsSync()) {
      f.deleteSync();
      logger.info('🗑  Removed $relative (no Hilt / no local route in a DFM)');
    }
    _pruneEmptyDirs(f.parent, stopAt: Directory(src));
  }

  final resolverFile = File(
    '$src/presentation/di/${pascalCase}DeepLinkResolver.kt',
  );
  if (resolverFile.existsSync()) {
    var resolverContent = resolverFile.readAsStringSync();
    resolverContent = resolverContent
        .replaceAll('import javax.inject.Inject\n', '')
        .replaceAll(' @Inject constructor()', '');
    resolverFile.writeAsStringSync(resolverContent);
    logger.info('📝 Stripped @Inject from ${pascalCase}DeepLinkResolver.kt (DFM)');
  }

  File('$src/presentation/$screenCamel/${screenPascal}ViewModel.kt').writeAsStringSync('''
/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package $packageName.presentation.$screenCamel

import com.danhdue.framework.base.mvi.MviViewModel
import timber.log.Timber

/**
 * Manages the business logic and state for the $screenPascal feature.
 *
 * `${_dfmModuleName(packageName)}` is an on-demand Dynamic Feature Module: its `@Module`s never
 * reach the host Hilt graph, so this ViewModel is Hilt-free. It is built by the
 * plain AndroidX `viewModel()` factory (see `${screenPascal}Root`).
 */
class ${screenPascal}ViewModel :
    MviViewModel<${screenPascal}State, ${screenPascal}Action, ${screenPascal}Event>(
        initialState = ${screenPascal}State(),
    ) {
        init {
            Timber.d("${screenPascal}ViewModel init")
        }

        override fun onAction(action: ${screenPascal}Action) {
            when (action) {
                ${screenPascal}Action.OnBackClicked -> {
                    sendEvent(${screenPascal}Event.NavigateBack)
                }
            }
        }
    }
''');

  File('$src/presentation/$screenCamel/${screenPascal}Screen.kt').writeAsStringSync('''
/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package $packageName.presentation.$screenCamel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Composable entry point for the $screenPascal feature.
 *
 * On-demand Dynamic Feature Module — uses the plain AndroidX [viewModel] factory,
 * not `hiltViewModel()`, because a downloaded split never joins the host Hilt
 * graph (Task 14, design §4.4).
 */
@Composable
fun ${screenPascal}Root(
    viewModel: ${screenPascal}ViewModel = viewModel(),
    onEvent: (${screenPascal}Event) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnEvent by rememberUpdatedState(onEvent)

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            currentOnEvent(event)
        }
    }

    ${screenPascal}Screen(
        state = state,
    )
}

/**
 * A stateless composable that draws the UI for the $screenPascal feature.
 */
@Composable
private fun ${screenPascal}Screen(state: ${screenPascal}State) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = "$screenPascal Screen")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview${screenPascal}Screen() {
    ${screenPascal}Screen(
        state = ${screenPascal}State(),
    )
}
''');

  _rewriteWithoutInject(
    File('$src/data/repository/${pascalCase}RepositoryImpl.kt'),
    logger,
  );
  _rewriteWithoutInject(
    File('$src/domain/usecase/Get${pascalCase}DataUseCase.kt'),
    logger,
  );

  logger.info('🧹 Stripped Hilt from $pascalCase for on-demand delivery');
}

/// Removes `@Inject` + its `import javax.inject.Inject` from a generated file so
/// it compiles in a Hilt-plugin-free dynamic-feature module. Also drops the now
/// -redundant empty primary constructor the `@Inject` was attached to — a bare
/// `constructor()` trips detekt `EmptyDefaultConstructor`. Idempotent.
void _rewriteWithoutInject(File file, Logger logger) {
  if (!file.existsSync()) return;
  var content = file.readAsStringSync();
  content = content
      .replaceAll('import javax.inject.Inject\n', '')
      .replaceAll(' @Inject constructor(', ' constructor(')
      .replaceAll('@Inject constructor(', 'constructor(')
      .replaceAll('@Inject\n    constructor(', 'constructor(')
      // strip the leftover empty primary constructor (no params only)
      .replaceAll('\n    constructor() :', ' :')
      .replaceAll(' constructor() :', ' :')
      .replaceAll('\n    constructor() {', ' {')
      .replaceAll(' constructor() {', ' {');
  file.writeAsStringSync(content);
  logger.info('📝 Removed @Inject from ${file.path.split('/').last}');
}

void _pruneEmptyDirs(Directory dir, {required Directory stopAt}) {
  var current = dir;
  while (current.existsSync() &&
      current.path != stopAt.path &&
      current.listSync().isEmpty) {
    current.deleteSync();
    current = current.parent;
  }
}

String _dfmModuleName(String packageName) => packageName.split('.').last;

/// Overwrites the generated feature `build.gradle.kts` with the
/// `com.android.dynamic-feature` variant. No hard-coded versions — every plugin
/// id and dependency comes from `buildSrc` (`Deps` / `extensions` / `commons`).
void _writeDynamicFeatureBuildGradle(
  String modulePath,
  String packageName,
  Logger logger,
) {
  final file = File('$modulePath/build.gradle.kts');
  file.writeAsStringSync('''
/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
import commons.addComposeConfig
import extensions.FRAMEWORK
import extensions.UI_KIT
import extensions.addComposeDependencies
import extensions.addNavigationDependencies
import extensions.implementation
import extensions.testImplementation

// ============================================================================
// On-demand Dynamic Feature Module (generated by `mvi_feature --delivery
// on-demand`) — structurally identical to the template's worked example
// `:features:scanner` (Task 14, design §4.4; see docs/architecture/ARCHITECTURE.md).
//
// The dependency direction is INVERTED versus an install-time feature: `:app`
// lists this module in `android.dynamicFeatures` and this module depends on
// `:app`, so the host never sees the feature at compile time. Consequences:
//
//  * applies `com.android.dynamic-feature`, NOT `com.android.library` /
//    `commons.android-feature` (a dynamic-feature module is its own AGP plugin
//    type — the two cannot coexist);
//  * does NOT apply the Hilt Gradle plugin (unsupported on
//    `com.android.dynamic-feature`) and ships NO Hilt code — its navigation
//    entry is contributed at RUNTIME via `com.danhdue.platform.FeatureEntry`,
//    discovered by `:shell` through `ServiceLoader` once the split is installed.
//    The `META-INF/services/com.danhdue.platform.FeatureEntry` file that names
//    this module's `FeatureEntry` is OWNED BY `:app` (every on-demand FQCN is
//    aggregated there — bundletool forbids two feature splits shipping the same
//    root resource), NOT this module;
//  * `<Screen>Root` uses the plain AndroidX `viewModel()` (no `hiltViewModel()`).
// ============================================================================

plugins {
    id(Deps.ANDROID_DYNAMIC_FEATURE_PLUGIN_ID)
    id(Deps.KOTLIN_GRADLE_PLUGIN_ID)
    id(Deps.ANDROID_COMPOSE_PLUGIN_ID)
    id(Deps.KOTLIN_SERIALIZATION) version Versions.kotlinSerialization
    id(Deps.CODE_ANALYZE_TOOLS_QUALITY)
}

android {
    namespace = "$packageName"

    compileSdk = AppConfig.compileSdk
    defaultConfig {
        minSdk = AppConfig.minSdk
    }

    addComposeConfig()

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget.target
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
    }

    sourceSets {
        getByName("main") {
            kotlin.srcDirs("src/main/java", "src/main/kotlin")
            java.srcDirs("src/main/java", "src/main/kotlin")
        }
        getByName("test") {
            kotlin.srcDirs("src/test/java", "src/test/kotlin")
            java.srcDirs("src/test/java", "src/test/kotlin")
        }
    }
}

dependencies {
    // Inverted DFM dependency — the split depends on the host, not vice-versa.
    // `:app` must never declare this module as a plain `implementation(project(...))`.
    implementation(project(":app"))
    implementation(project(":packages:platform"))

    FRAMEWORK
    UI_KIT

    addComposeDependencies()
    addNavigationDependencies()

    implementation(Deps.Kotlin.coroutineCore)

    testImplementation(project(":libraries:testutils"))
}
''');
  logger.info('📝 Rewrote $modulePath/build.gradle.kts as com.android.dynamic-feature');
}

void _writeDynamicFeatureManifest(
  String modulePath,
  String snakeCase,
  Logger logger,
) {
  final file = File('$modulePath/src/main/AndroidManifest.xml');
  file.writeAsStringSync('''<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:dist="http://schemas.android.com/apk/distribution">

    <!--
      On-demand Dynamic Feature Module (Task 14, design §4.4).
      - not an instant module
      - delivered on demand only (absent from the base APK; installed at runtime
        by FeatureInstallerImpl via SplitInstallManager)
      - not fused into the base APK for pre-Lollipop / multi-APK builds
    -->
    <dist:module
        dist:instant="false"
        dist:title="@string/${snakeCase}_feature_title">
        <dist:delivery>
            <dist:on-demand />
        </dist:delivery>
        <dist:fusing dist:include="false" />
    </dist:module>
</manifest>
''');
  logger.info('📝 Wrote dist:on-demand manifest for $modulePath');
}

/// A `dist:title` string for an on-demand module MUST live in the BASE module's
/// resource table — `bundletool` resolves it from the base APK, not the split
/// (`bundleDebug` fails with "Title for module '<name>' is missing in the base
/// resource table" otherwise). Appends `<name>_feature_title` to
/// `app/src/main/res/values/strings.xml`. Idempotent.
void _writeBaseModuleSplitTitle(
  String snakeCase,
  String pascalCase,
  Logger logger,
) {
  final file = File('app/src/main/res/values/strings.xml');
  if (!file.existsSync()) {
    logger.warn('⚠️ app/src/main/res/values/strings.xml not found — skipping split title');
    return;
  }
  var content = file.readAsStringSync();
  if (content.contains('name="${snakeCase}_feature_title"')) {
    logger.info('✓ ${snakeCase}_feature_title already in app strings.xml');
    return;
  }
  final closing = content.lastIndexOf('</resources>');
  if (closing == -1) {
    logger.warn('⚠️ app strings.xml has no </resources> — skipping split title');
    return;
  }
  content =
      '${content.substring(0, closing)}    <string name="${snakeCase}_feature_title">$pascalCase</string>\n${content.substring(closing)}';
  file.writeAsStringSync(content);
  logger.info('📝 Added ${snakeCase}_feature_title to app/src/main/res/values/strings.xml');
}

void _writeFeatureEntry({
  required String modulePath,
  required String packageName,
  required String pascalCase,
  required String screenPascal,
  required String screenCamel,
  required Logger logger,
}) {
  final dir = packageName.replaceAll('.', '/');
  final file = File(
    '$modulePath/src/main/kotlin/$dir/presentation/di/${pascalCase}FeatureEntry.kt',
  );
  file.createSync(recursive: true);
  file.writeAsStringSync('''
/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package $packageName.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.FeatureEntry
import com.danhdue.platform.deeplink.DeepLinkResolver
import $packageName.presentation.$screenCamel.${screenPascal}Root

/**
 * Runtime navigation entry point for the on-demand $pascalCase Dynamic Feature
 * Module (Task 14, design §4.4).
 *
 * An install-time feature contributes its entries through Hilt `@IntoSet`
 * multibinding; a downloaded split is invisible to the host Hilt graph, so
 * `:shell` discovers this class via `ServiceLoader` after `SplitCompat.install(...)`
 * and folds [installer] into the set that feeds `NavDisplay`. The
 * `META-INF/services/com.danhdue.platform.FeatureEntry` file naming this class
 * is owned by `:app` (one aggregated file for every on-demand feature — design
 * §4.4), not this module.
 *
 * Must have a public no-arg constructor — `ServiceLoader` instantiates it
 * reflectively.
 */
class ${pascalCase}FeatureEntry : FeatureEntry {
    override fun installer(): EntryProviderInstaller =
        {
            entry<AppRoutes.${pascalCase}Route> {
                ${screenPascal}Root(onEvent = {})
            }
        }

    override fun resolver(): DeepLinkResolver = ${pascalCase}DeepLinkResolver()
}
''');
  logger.info('📝 Generated ${pascalCase}FeatureEntry.kt');
}

/// Appends `<pkg>.presentation.di.<Name>FeatureEntry` to the SINGLE
/// `META-INF/services/…platform.FeatureEntry` file owned by **`:app`**.
///
/// bundletool rejects an App Bundle in which two feature splits carry the same
/// root-resource path with differing content, so the on-demand `FeatureEntry`
/// registrations cannot live one-per-module — they are aggregated into the base
/// module's file and `:shell` skips any entry whose split is not installed
/// (design §4.4). Idempotent; the exact inverse of `remove_feature`.
void _registerFeatureEntryWithServiceLoader(
  String packageName,
  String pascalCase,
  Logger logger,
) {
  final fqcn = '$packageName.presentation.di.${pascalCase}FeatureEntry';
  final file = _appFeatureEntryServicesFile();
  var content = file.existsSync() ? file.readAsStringSync() : '';

  final already = content
      .split('\n')
      .map((line) => line.trim())
      .contains(fqcn);
  if (already) {
    logger.info('✓ $fqcn already in ${file.path}');
    return;
  }

  if (content.isNotEmpty && !content.endsWith('\n')) content += '\n';
  content += '$fqcn\n';
  file.createSync(recursive: true);
  file.writeAsStringSync(content);
  logger.info('📝 Registered ${pascalCase}FeatureEntry in ${file.path}');
}

/// The `:app`-owned `META-INF/services/*.platform.FeatureEntry` file — the
/// already-present one (its name follows the project's renamed vendor prefix),
/// else the pristine `com.danhdue.platform.FeatureEntry` default.
File _appFeatureEntryServicesFile() {
  const dir = 'app/src/main/resources/META-INF/services';
  final servicesDir = Directory(dir);
  if (servicesDir.existsSync()) {
    for (final entity in servicesDir.listSync()) {
      if (entity is File &&
          entity.uri.pathSegments.last.endsWith('.platform.FeatureEntry')) {
        return entity;
      }
    }
  }
  return File('$dir/com.danhdue.platform.FeatureEntry');
}

/// Adds `":features:<name>"` to `:app` `android.dynamicFeatures`, creating the
/// block after the `namespace` line if it is not there yet. Idempotent.
void _registerDynamicFeatureInApp(String gradlePath, Logger logger) {
  final file = File('app/build.gradle.kts');
  if (!file.existsSync()) {
    logger.warn('app/build.gradle.kts not found');
    return;
  }

  var content = file.readAsStringSync();
  if (content.contains('dynamicFeatures') && content.contains('"$gradlePath"')) {
    logger.info('✓ $gradlePath already in app dynamicFeatures');
    return;
  }

  if (content.contains('dynamicFeatures')) {
    final marker = RegExp(r'dynamicFeatures\s*\+=\s*setOf\(');
    final match = marker.firstMatch(content);
    if (match == null) {
      logger.warn('⚠️ Could not parse existing dynamicFeatures block — skipping');
      return;
    }
    content =
        '${content.substring(0, match.end)}\n        "$gradlePath",${content.substring(match.end)}';
  } else {
    const anchor = 'namespace = AppConfig.namespace\n';
    final idx = content.indexOf(anchor);
    if (idx == -1) {
      logger.warn('⚠️ Could not find app namespace anchor — skipping dynamicFeatures');
      return;
    }
    final insertAt = idx + anchor.length;
    content =
        '${content.substring(0, insertAt)}${_dynamicFeaturesBlock(gradlePath)}${content.substring(insertAt)}';
  }

  // A dynamic-feature base module (`:app`) must not expose `compileOnly` Android
  // dependencies. `addCommonDependencies()` drags in `compileOnly` Lombok, which
  // `:app` never uses — drop it so the split can build.
  if (!content.contains(_appDfmCompileOnlyGuardMarker)) {
    final guardAnchor = RegExp(
      r'configurations\.forEach \{\n {4}it\.exclude\("ui-text-google-fonts"\)\n\}\n',
    );
    final guardMatch = guardAnchor.firstMatch(content);
    if (guardMatch != null) {
      content =
          '${content.substring(0, guardMatch.end)}${appDfmCompileOnlyGuard()}${content.substring(guardMatch.end)}';
    } else {
      logger.warn('⚠️ Could not place the DFM compileOnly guard in app/build.gradle.kts');
    }
  }

  file.writeAsStringSync(content);
  logger.info('📝 Registered $gradlePath in app android.dynamicFeatures');
}

/// Appends an `@Serializable data object <Name>Route : NavKey` to `:platform`
/// `AppRoutes`. A DFM host cannot import the feature, so its route constant must
/// live in `:platform`. Idempotent; the exact inverse of `remove_feature`.
void _appendRouteToAppRoutes(String pascalCase, Logger logger) {
  var file = File(appRoutesPath);
  if (!file.existsSync()) {
    final dir = Directory('packages/platform/src/main/kotlin');
    if (dir.existsSync()) {
      for (final e in dir.listSync(recursive: true)) {
        if (e is File && e.uri.pathSegments.last == 'AppRoutes.kt') {
          file = e;
          break;
        }
      }
    }
  }
  if (!file.existsSync()) {
    logger.warn('⚠️ ${file.path} not found — skipping :platform route registration');
    return;
  }

  var content = file.readAsStringSync();
  if (content.contains('data object ${pascalCase}Route')) {
    logger.info('✓ ${pascalCase}Route already in AppRoutes.kt');
    return;
  }

  final closingBrace = content.lastIndexOf('}');
  if (closingBrace == -1) {
    logger.warn('⚠️ AppRoutes.kt has no closing brace — skipping');
    return;
  }

  content = content.substring(0, closingBrace) +
      appRoutesEntry(pascalCase) +
      content.substring(closingBrace);
  file.writeAsStringSync(content);
  logger.info('📝 Added ${pascalCase}Route to :platform AppRoutes.kt');
}

/// Appends a [FeatureEntryPoint] for this feature to `:platform` `AppDeepLinks.kt`.
///
/// Idempotent; the exact inverse of `remove_feature`.
void _appendDeepLinkEntryPoint({
  required String snakeCase,
  required String pascalCase,
  required bool isDfm,
  required Logger logger,
}) {
  var file = File(appDeepLinksPath);
  if (!file.existsSync()) {
    final dir = Directory('packages/platform/src/main/kotlin');
    if (dir.existsSync()) {
      for (final e in dir.listSync(recursive: true)) {
        if (e is File && e.uri.pathSegments.last == 'AppDeepLinks.kt') {
          file = e;
          break;
        }
      }
    }
  }
  if (!file.existsSync()) {
    logger.warn('⚠️ ${file.path} not found — skipping :platform deep link registration');
    return;
  }

  var content = file.readAsStringSync();
  if (content.contains('feature = "$snakeCase"')) {
    logger.info('✓ $snakeCase already in AppDeepLinks.kt');
    return;
  }

  final lastParen = content.lastIndexOf('        )');
  if (lastParen == -1) {
    logger.warn('⚠️ AppDeepLinks.kt closing parenthesis missing — skipping');
    return;
  }

  final entry = isDfm
      ? '''            // TODO: if this feature is hosted in a shell tab, set tab = <tabIndex> (e.g. tab = 0)
            FeatureEntryPoint(
                feature = "$snakeCase",
                entryRoute = AppRoutes.${pascalCase}Route,
                tab = null,
                dynamicModule = "$snakeCase",
            ),
'''
      : '''            // TODO: if this feature is hosted in a shell tab, set tab = <tabIndex> (e.g. tab = 0)
            FeatureEntryPoint(
                feature = "$snakeCase",
                entryRoute = AppRoutes.${pascalCase}Route,
                tab = null,
            ),
''';

  content = content.substring(0, lastParen) + entry + content.substring(lastParen);
  file.writeAsStringSync(content);
  logger.info('📝 Added $snakeCase to :platform AppDeepLinks.kt');
}

/// Real `:shell` on-demand install wiring (Task 14, design §4.4). Maintains
/// `shell/.../navigation/OnDemandFeatures.kt` — a compiling registry of
/// module → `:platform` route plus the `FeatureInstaller.ensureInstalled(...)` /
/// `ServiceLoader` helpers.
///
/// This registry is for on-demand features reached from an ARBITRARY call site
/// (not a fixed bottom-nav tab). The template's `:features:scanner` is the
/// hand-wired *bottom-nav-tab* DFM exemplar — `ShellViewModel` / `ShellScreen`
/// wire it directly and it is deliberately NOT listed here. Generated features
/// register here and navigate via [routeOf]. Idempotent; safe to re-run.
void _wireShellInstallBranch(String snakeCase, String pascalCase, Logger logger) {
  final shellBuildFile = File('shell/build.gradle.kts');
  if (!shellBuildFile.existsSync()) {
    logger.info('✓ :shell absent — skipping on-demand install wiring');
    return;
  }

  final file = File(
    'shell/src/main/kotlin/com/danhdue/shell/navigation/OnDemandFeatures.kt',
  );
  file.createSync(recursive: true);

  const appendMarker = '// mvi_feature --delivery on-demand appends here:';
  var content = file.lengthSync() > 0
      ? file.readAsStringSync()
      : _onDemandFeaturesSeed(appendMarker);

  if (content.contains('"$snakeCase" to AppRoutes.${pascalCase}Route')) {
    logger.info('✓ :shell on-demand registry already has "$snakeCase"');
    return;
  }

  final idx = content.indexOf(appendMarker);
  if (idx == -1) {
    logger.warn('⚠️ OnDemandFeatures.kt append marker missing — skipping "$snakeCase"');
    return;
  }
  final insertAt = idx + appendMarker.length;
  content =
      '${content.substring(0, insertAt)}\n            "$snakeCase" to AppRoutes.${pascalCase}Route,${content.substring(insertAt)}';
  file.writeAsStringSync(content);
  logger.info('📝 Registered "$snakeCase" in :shell OnDemandFeatures.kt');
}

String _onDemandFeaturesSeed(String appendMarker) => '''
/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell.navigation

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.FeatureEntry
import com.danhdue.platform.FeatureInstaller
import com.danhdue.shell.installersFrom
import java.util.ServiceLoader

/**
 * Registry + helpers for on-demand Dynamic Feature Module installs in `:shell`
 * (Task 14, design §4.4). Extended by `mvi_feature --delivery on-demand`.
 *
 * `:features:scanner` is the template's hand-wired **bottom-nav-tab** DFM
 * exemplar: `ShellViewModel` calls `FeatureInstaller.ensureInstalled("scanner")`
 * on first Scanner-tab selection, and `ShellScreen` merges the split's
 * `ServiceLoader`-loaded `FeatureEntry` into the `NavDisplay` entry provider. It
 * does NOT use this file.
 *
 * This registry is for on-demand features reached from an **arbitrary call
 * site** (not a fixed tab): [ensureInstalled] the split, then navigate to its
 * [routeOf] entry and rebuild the entry provider with
 * [loadInstalledFeatureInstallers].
 */
object OnDemandFeatures {
    // Gradle-module name -> its cross-feature `:platform` route.
    private val routes: Map<String, Any> =
        mapOf(
            $appendMarker
        )

    /** The `:platform` route for an on-demand [module], or `null` if unknown. */
    fun routeOf(module: String): Any? = routes[module]

    /** Installs [module]'s split if absent, then invokes [onReady]. */
    suspend fun ensureInstalled(
        installer: FeatureInstaller,
        module: String,
        onReady: () -> Unit,
    ) = installer.ensureInstalled(module, onReady)

    /**
     * `EntryProviderInstaller`s contributed by every currently-installed
     * on-demand split. The `META-INF/services/…FeatureEntry` file is owned by
     * `:app` and lists EVERY on-demand `FeatureEntry` FQCN (design §4.4), so
     * [installersFrom] skips the ones whose split is not installed instead of
     * letting `ServiceLoader` throw. Merge the result into the set feeding
     * `NavDisplay` once an install completes.
     */
    fun loadInstalledFeatureInstallers() =
        installersFrom(
            ServiceLoader.load(FeatureEntry::class.java, FeatureEntry::class.java.classLoader),
        )
}
''';

// ---------------------------------------------------------------------------
// shared constants / snippets (kept byte-identical with remove_feature)
// ---------------------------------------------------------------------------

const appRoutesPath =
    'packages/platform/src/main/kotlin/com/danhdue/platform/AppRoutes.kt';

const appDeepLinksPath =
    'packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/AppDeepLinks.kt';

String appRoutesEntry(String pascalCase) =>
    '\n    /** Entry point of the $pascalCase feature. */\n'
    '    @Serializable\n'
    '    data object ${pascalCase}Route : NavKey\n';

String _dynamicFeaturesBlock(String gradlePath) =>
    '\n    dynamicFeatures += setOf(\n        "$gradlePath",\n    )\n';

/// Marker line used to detect (and, in `remove_feature`, strip) the guard.
const _appDfmCompileOnlyGuardMarker =
    'exclude(group = "org.projectlombok", module = "lombok")';

/// Guard appended to `app/build.gradle.kts` for on-demand delivery — the exact
/// inverse of `remove_feature`'s removal.
String appDfmCompileOnlyGuard() =>
    '\n// Added by `mvi_feature --delivery on-demand`: a dynamic-feature base module\n'
    '// (`:app`) must not expose `compileOnly` Android dependencies, and\n'
    '// `addCommonDependencies()` pulls in `compileOnly` Lombok that `:app` never uses.\n'
    'configurations.configureEach {\n'
    '    exclude(group = "org.projectlombok", module = "lombok")\n'
    '}\n';

// ---------------------------------------------------------------------------
// case helpers
// ---------------------------------------------------------------------------

String _toPascalCase(String input) {
  if (input.isEmpty) return input;
  if (input[0] == input[0].toUpperCase() && !input.contains('_')) {
    return input;
  }
  return input
      .split('_')
      .map(
        (word) => word.isEmpty
            ? ''
            : '${word[0].toUpperCase()}${word.substring(1).toLowerCase()}',
      )
      .join('');
}

String _toCamelCase(String input) {
  final pascal = _toPascalCase(input);
  if (pascal.isEmpty) return pascal;
  return '${pascal[0].toLowerCase()}${pascal.substring(1)}';
}

String _toSnakeCase(String input) {
  if (input.isEmpty) return input;
  final buffer = StringBuffer();
  for (var i = 0; i < input.length; i++) {
    final char = input[i];
    if (char == char.toUpperCase() && char != char.toLowerCase()) {
      if (i > 0) buffer.write('_');
      buffer.write(char.toLowerCase());
    } else {
      buffer.write(char);
    }
  }
  return buffer.toString();
}
