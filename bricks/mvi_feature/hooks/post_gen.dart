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
///   aggregates its navigation entries. `AppRoutes` is left untouched.
///
/// * **`delivery: on-demand`** — the module is turned into a
///   `com.android.dynamic-feature` split. The hook rewrites its
///   `build.gradle.kts` + `AndroidManifest.xml`, registers it in `:app`
///   `android.dynamicFeatures`, generates `<Name>FeatureEntry : FeatureEntry`
///   plus a `META-INF/services` registration, and forces a
///   `<Name>Route : NavKey` constant into `:platform` `AppRoutes` (the host
///   cannot see the split at compile time). The `SplitInstallManager` /
///   `:shell` install branch is intentionally left as a guarded no-op — it is
///   owned by Task 14.
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

  _writeDynamicFeatureBuildGradle(modulePath, packageName, logger);
  _writeDynamicFeatureManifest(modulePath, snakeCase, logger);
  _writeDynamicFeatureStrings(modulePath, snakeCase, pascalCase, logger);
  _writeFeatureEntry(
    modulePath: modulePath,
    packageName: packageName,
    pascalCase: pascalCase,
    screenPascal: screenPascal,
    screenCamel: screenCamel,
    logger: logger,
  );
  _writeServiceLoaderRegistration(modulePath, packageName, pascalCase, logger);
  _registerDynamicFeatureInApp(gradlePath, logger);
  _appendRouteToAppRoutes(pascalCase, logger);
  _wireShellInstallBranch(snakeCase, pascalCase, logger);
}

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
import commons.addLibDefaultConfig
import extensions.COMPONENT
import extensions.FRAMEWORK
import extensions.addComposeDependencies
import extensions.addNavigationDependencies
import extensions.implementation

// -----------------------------------------------------------------------------
// On-demand Dynamic Feature Module (generated by `mvi_feature --delivery on-demand`).
//
// The dependency direction is INVERTED versus an install-time feature: `:app`
// lists this module in `android.dynamicFeatures` and this module depends on
// `:app`, so the host never sees the feature (or its Hilt `@Module`s) at compile
// time. Navigation entries are contributed at RUNTIME through a
// `com.danhdue.platform.FeatureEntry` + `ServiceLoader`
// (`src/main/resources/META-INF/services/com.danhdue.platform.FeatureEntry`).
//
// The Hilt Gradle plugin does not support `com.android.dynamic-feature`, so it
// is intentionally NOT applied here: `hilt-android` is on the compile classpath
// only so the generated `@Module` / `@HiltViewModel` code compiles. Wiring the
// split's DI + navigation at runtime (`SplitInstallManager` / `SplitCompat` /
// the `:shell` install branch) is owned by TODO(task_14). Until then the split
// is still bundled by `assembleDebug`, so this module compiles cleanly.
// -----------------------------------------------------------------------------

plugins {
    id("com.android.dynamic-feature")
    id(Deps.KOTLIN_GRADLE_PLUGIN_ID)
    id(Deps.KOTLIN_PARCELIZE)
    id(Deps.ANDROID_COMPOSE_PLUGIN_ID)
}

android {
    namespace = "$packageName"

    addLibDefaultConfig()
    addComposeConfig()

    kotlinOptions {
        languageVersion = AppConfig.kotlinVersion
        jvmTarget = AppConfig.jvmTarget.target
        freeCompilerArgs = EnvConfigs.FreeCoroutineCompilerArgs
    }
}

dependencies {
    // Inverted dependency — a DFM depends on the host, not the other way round.
    implementation(project(":app"))
    implementation(project(Modules.platform))

    FRAMEWORK
    COMPONENT

    addComposeDependencies()
    addNavigationDependencies()

    // Compile-time only — see the plugin note above; no Hilt processing here.
    implementation(Deps.Hilt.core)
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

    <dist:module
        dist:onDemand="true"
        dist:title="@string/title_$snakeCase">
        <dist:fusing dist:include="false" />
    </dist:module>
</manifest>
''');
  logger.info('📝 Wrote dist:onDemand manifest for $modulePath');
}

void _writeDynamicFeatureStrings(
  String modulePath,
  String snakeCase,
  String pascalCase,
  Logger logger,
) {
  final file = File('$modulePath/src/main/res/values/strings.xml');
  file.createSync(recursive: true);
  file.writeAsStringSync('''<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="title_$snakeCase">$pascalCase</string>
</resources>
''');
  logger.info('📝 Wrote split title string for $modulePath');
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
    '$modulePath/src/main/kotlin/$dir/${pascalCase}FeatureEntry.kt',
  );
  file.createSync(recursive: true);
  file.writeAsStringSync('''
/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package $packageName

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.FeatureEntry
import $packageName.presentation.$screenCamel.${screenPascal}Root

/**
 * Runtime navigation entry point for the on-demand $pascalCase dynamic feature module.
 *
 * An install-time feature contributes its entries through Hilt `@IntoSet`
 * multibinding; a downloaded split is invisible to the host Hilt graph, so the
 * host discovers this class via `ServiceLoader`
 * (`src/main/resources/META-INF/services/com.danhdue.platform.FeatureEntry`)
 * after `SplitCompat.install(...)` and folds [installer] into the set that feeds
 * `NavDisplay`.
 *
 * TODO(task_14): the `:shell` branch that triggers `SplitInstallManager` and
 * invokes this once the split has arrived is owned by Task 14.
 */
class ${pascalCase}FeatureEntry : FeatureEntry {
    override fun installer(): EntryProviderInstaller = {
        entry<AppRoutes.${pascalCase}Route> {
            ${screenPascal}Root(onEvent = {})
        }
    }
}
''');
  logger.info('📝 Generated ${pascalCase}FeatureEntry.kt');
}

void _writeServiceLoaderRegistration(
  String modulePath,
  String packageName,
  String pascalCase,
  Logger logger,
) {
  final file = File(
    '$modulePath/src/main/resources/META-INF/services/com.danhdue.platform.FeatureEntry',
  );
  file.createSync(recursive: true);
  file.writeAsStringSync('$packageName.${pascalCase}FeatureEntry\n');
  logger.info('📝 Registered ${pascalCase}FeatureEntry with ServiceLoader');
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
  final file = File(appRoutesPath);
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

/// Guarded `:shell` install branch. `:shell` does not exist yet (Phase 2), and
/// the `SplitInstallManager`-backed runtime is Task 14's — so this only drops a
/// fully-commented, inert `TODO(task_14)` marker when `:shell` is present.
void _wireShellInstallBranch(String snakeCase, String pascalCase, Logger logger) {
  final shellBuildFile = File('shell/build.gradle.kts');
  if (!shellBuildFile.existsSync()) {
    logger.info('✓ :shell absent — skipping on-demand install branch (Task 14)');
    return;
  }

  final file = File(
    'shell/src/main/kotlin/com/danhdue/shell/navigation/OnDemandInstallBranches.kt',
  );
  file.createSync(recursive: true);
  var content = file.existsSync() && file.lengthSync() > 0
      ? file.readAsStringSync()
      : '''
/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell.navigation

/*
 * Generated registry of on-demand dynamic feature modules.
 *
 * TODO(task_14): replace these comments with a real `when` branch that calls
 * `featureInstaller.ensureInstalled("<module>") { navigator.navigateTo(route) }`
 * backed by `SplitInstallManager` + `SplitCompat`.
 */
''';

  final marker = '// on-demand: $snakeCase';
  if (!content.contains(marker)) {
    content =
        '$content\n$marker -> AppRoutes.${pascalCase}Route  // TODO(task_14): ensureInstalled("$snakeCase")\n';
    file.writeAsStringSync(content);
    logger.info('📝 Added guarded :shell install branch for "$snakeCase"');
  } else {
    logger.info('✓ :shell install branch for "$snakeCase" already present');
  }
}

// ---------------------------------------------------------------------------
// shared constants / snippets (kept byte-identical with remove_feature)
// ---------------------------------------------------------------------------

const appRoutesPath =
    'platform/src/main/kotlin/com/danhdue/platform/AppRoutes.kt';

String appRoutesEntry(String pascalCase) =>
    '\n    /** Entry point of the $pascalCase feature (an on-demand dynamic feature module). */\n'
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
    '// (`:app`) must not expose `compileOnly` Android dependencies.\n'
    '// TODO(task_14): fold this into the :app / buildSrc DFM setup.\n'
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
