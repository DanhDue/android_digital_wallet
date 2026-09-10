import 'dart:io';
import 'package:mason/mason.dart';

/// Reverses every wire point that `mvi_feature` adds — for BOTH delivery modes.
///
/// install-time: `settings.gradle.kts`, `Deps.kt`,
/// `DependencyHandlerExtensions.kt`, `app/build.gradle.kts` (+ `shell` when
/// present).
///
/// on-demand: the above are no-ops (never added), plus the feature directory
/// (which carries the `META-INF/services` registration and `FeatureEntry`), the
/// `:app` `android.dynamicFeatures` entry, the `:platform` `AppRoutes` constant
/// and the guarded `:shell` install branch.
void run(HookContext context) {
  final name = context.vars['name'] as String;

  final pascalCase = _toPascalCase(name);
  final snakeCase = _toSnakeCase(name);
  final upperSnakeCase = snakeCase.toUpperCase();

  final gradlePath = ':features:$snakeCase';
  final modulePath = 'features/$snakeCase';

  context.logger.info('🗑️  Removing module: $gradlePath');

  _removeFeatureDirectory(modulePath, context.logger);
  _removeFromSettingsGradle(gradlePath, context.logger);
  _removeFromSettingsGradle('$gradlePath:sample', context.logger);
  _removeFromDepsKt(pascalCase, context.logger);
  _removeFromDependencyHandler(upperSnakeCase, context.logger);
  _removeFeatureAccessorFromBuildFile(
    File('app/build.gradle.kts'),
    upperSnakeCase,
    context.logger,
    label: 'app/build.gradle.kts',
  );
  _removeFeatureAccessorFromBuildFile(
    File('shell/build.gradle.kts'),
    upperSnakeCase,
    context.logger,
    label: 'shell/build.gradle.kts',
  );

  // Common wire points across all features (install-time and on-demand).
  _removeFromAppDeepLinks(snakeCase, context.logger);
  _removeRouteFromAppRoutes(pascalCase, context.logger);

  // on-demand-only wire points (no-ops when the feature was install-time).
  _removeFromAppDynamicFeatures(gradlePath, context.logger);
  _removeShellInstallBranch(snakeCase, pascalCase, context.logger);
  _removeSplitTitleFromAppStrings(snakeCase, context.logger);
  _removeFeatureEntryFromServiceLoader(pascalCase, context.logger);

  context.logger.success('✅ Module removal complete!');

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

void _removeFeatureDirectory(String modulePath, Logger logger) {
  final dir = Directory(modulePath);
  if (dir.existsSync()) {
    dir.deleteSync(recursive: true);
    logger.info('🗑️  Removed $modulePath/');
  } else {
    logger.warn('⚠️ Directory $modulePath not found');
  }
}

void _removeFromSettingsGradle(String gradlePath, Logger logger) {
  final file = File('settings.gradle.kts');
  if (!file.existsSync()) {
    logger.warn('settings.gradle.kts not found');
    return;
  }

  var content = file.readAsStringSync();
  final include = 'include("$gradlePath")\n';

  if (content.contains(include)) {
    content = content.replaceAll(include, '');
    file.writeAsStringSync(content);
    logger.info('📝 Removed from settings.gradle.kts');
  } else {
    logger.info('✓ Not in settings.gradle.kts');
  }
}

void _removeFromDepsKt(String pascalCase, Logger logger) {
  final file = File('buildSrc/src/main/kotlin/Deps.kt');
  if (!file.existsSync()) {
    logger.warn('Deps.kt not found');
    return;
  }

  var content = file.readAsStringSync();
  final moduleConst = 'feature$pascalCase';
  final pattern = RegExp('    const val $moduleConst = "[^"]+"\n');

  if (pattern.hasMatch(content)) {
    content = content.replaceAll(pattern, '');
    file.writeAsStringSync(content);
    logger.info('📝 Removed from Deps.kt');
  } else {
    logger.info('✓ Not in Deps.kt');
  }
}

void _removeFromDependencyHandler(String upperSnakeCase, Logger logger) {
  final file = File(
    'buildSrc/src/main/kotlin/extensions/DependencyHandlerExtensions.kt',
  );
  if (!file.existsSync()) {
    logger.warn('DependencyHandlerExtensions.kt not found');
    return;
  }

  var content = file.readAsStringSync();
  final accessorName = 'FEATURE_$upperSnakeCase';
  final pattern = RegExp(
    '\nval DependencyHandler\\.$accessorName\n    get\\(\\) = implementation\\(project\\(mapOf\\(PATH to Modules\\.feature[A-Za-z]+\\)\\)\\)\n',
  );

  if (pattern.hasMatch(content)) {
    content = content.replaceAll(pattern, '');
    file.writeAsStringSync(content);
    logger.info('📝 Removed from DependencyHandlerExtensions.kt');
  } else {
    logger.info('✓ Not in DependencyHandlerExtensions.kt');
  }
}

/// Removes `import extensions.FEATURE_<NAME>` and the bare `FEATURE_<NAME>`
/// accessor line from a host build file. No-op when the file or the lines are
/// absent (on-demand features, or `:shell` before Phase 2).
void _removeFeatureAccessorFromBuildFile(
  File file,
  String upperSnakeCase,
  Logger logger, {
  required String label,
}) {
  if (!file.existsSync()) {
    logger.info('✓ $label absent — nothing to unwire');
    return;
  }

  var content = file.readAsStringSync();
  final accessorName = 'FEATURE_$upperSnakeCase';
  var updated = false;

  final importLine = 'import extensions.$accessorName\n';
  if (content.contains(importLine)) {
    content = content.replaceAll(importLine, '');
    updated = true;
  }

  final depLine = '    $accessorName\n';
  if (content.contains(depLine)) {
    content = content.replaceAll(depLine, '');
    updated = true;
  }

  if (updated) {
    file.writeAsStringSync(content);
    logger.info('📝 Removed $accessorName from $label');
  } else {
    logger.info('✓ $accessorName not in $label');
  }
}

/// Removes `":features:<name>"` from `:app` `android.dynamicFeatures`, deleting
/// the whole block once it is empty so the file returns to its pre-DFM state.
void _removeFromAppDynamicFeatures(String gradlePath, Logger logger) {
  final file = File('app/build.gradle.kts');
  if (!file.existsSync()) {
    logger.warn('app/build.gradle.kts not found');
    return;
  }

  var content = file.readAsStringSync();
  if (!content.contains('dynamicFeatures') || !content.contains('"$gradlePath"')) {
    logger.info('✓ $gradlePath not in app dynamicFeatures');
    return;
  }

  content = content
      .replaceAll('        "$gradlePath",\n', '')
      .replaceAll('\n        "$gradlePath",', '');

  // Collapse an emptied `dynamicFeatures += setOf( )` block.
  content = content.replaceAll('\n    dynamicFeatures += setOf(\n    )\n', '');

  // Once no dynamic feature module remains, drop the DFM compileOnly guard too.
  if (!content.contains('dynamicFeatures')) {
    content = content.replaceAll(_appDfmCompileOnlyGuard(), '');
  }

  file.writeAsStringSync(content);
  logger.info('📝 Removed $gradlePath from app android.dynamicFeatures');
}

/// Exact inverse of `mvi_feature`'s `appDfmCompileOnlyGuard()` — must stay
/// byte-identical to it or the `replaceAll` below will not match.
String _appDfmCompileOnlyGuard() =>
    '\n// Added by `mvi_feature --delivery on-demand`: a dynamic-feature base module\n'
    '// (`:app`) must not expose `compileOnly` Android dependencies, and\n'
    '// `addCommonDependencies()` pulls in `compileOnly` Lombok that `:app` never uses.\n'
    'configurations.configureEach {\n'
    '    exclude(group = "org.projectlombok", module = "lombok")\n'
    '}\n';

/// Locates a uniquely-named source file under [root], regardless of the
/// project's package directory (the template may have been renamed away from
/// `com/danhdue`, so a hard-coded path would silently miss).
File? _findFile(String root, String fileName) {
  final dir = Directory(root);
  if (!dir.existsSync()) return null;
  for (final e in dir.listSync(recursive: true)) {
    if (e is File && e.uri.pathSegments.last == fileName) return e;
  }
  return null;
}

/// Exact inverse of the `mvi_feature` on-demand `AppRoutes` insertion.
void _removeRouteFromAppRoutes(String pascalCase, Logger logger) {
  final file = _findFile('packages/platform/src/main/kotlin', 'AppRoutes.kt');
  if (file == null) {
    logger.info('✓ AppRoutes.kt absent — nothing to unwire');
    return;
  }

  var content = file.readAsStringSync();
  final entry = _appRoutesEntry(pascalCase);
  if (!content.contains(entry)) {
    final fallbackPattern = RegExp(
      r'\n\s*/\*\*[\s\S]*?\*/\s*@Serializable\s+data object ' +
          RegExp.escape('${pascalCase}Route') +
          r' : NavKey\n',
    );
    if (fallbackPattern.hasMatch(content)) {
      content = content.replaceAll(fallbackPattern, '');
      file.writeAsStringSync(content);
      logger.info('📝 Removed ${pascalCase}Route from :platform AppRoutes.kt');
      return;
    }
    logger.info('✓ ${pascalCase}Route not in AppRoutes.kt');
    return;
  }

  content = content.replaceAll(entry, '');
  file.writeAsStringSync(content);
  logger.info('📝 Removed ${pascalCase}Route from :platform AppRoutes.kt');
}

/// Exact inverse of the `mvi_feature` AppDeepLinks insertion.
void _removeFromAppDeepLinks(String snakeCase, Logger logger) {
  final file = _findFile('packages/platform/src/main/kotlin', 'AppDeepLinks.kt');
  if (file == null) {
    logger.info('✓ AppDeepLinks.kt absent — nothing to unwire');
    return;
  }

  var content = file.readAsStringSync();
  if (!content.contains('feature = "$snakeCase"')) {
    logger.info('✓ "$snakeCase" not in AppDeepLinks.kt');
    return;
  }

  // Matches the FeatureEntryPoint block including any preceding comment and trailing comma/newline
  final pattern = RegExp(
    r'[ \t]*(?://[^\n]*\n)?[ \t]*FeatureEntryPoint\(\s*feature\s*=\s*"' +
        RegExp.escape(snakeCase) +
        r'"[\s\S]*?\),[ \t]*\n',
  );

  content = content.replaceAll(pattern, '');
  file.writeAsStringSync(content);
  logger.info('📝 Removed "$snakeCase" from :platform AppDeepLinks.kt');
}

/// Removes the guarded `:shell` on-demand install branch. Guarded on
/// `shell/build.gradle.kts` — a no-op before Phase 2 / Task 14.
void _removeShellInstallBranch(String snakeCase, String pascalCase, Logger logger) {
  if (!File('shell/build.gradle.kts').existsSync()) {
    logger.info('✓ :shell absent — no on-demand install branch to remove');
    return;
  }

  final file = _findFile('shell/src/main/kotlin', 'OnDemandFeatures.kt');
  if (file == null) {
    logger.info('✓ OnDemandFeatures.kt absent — nothing to remove');
    return;
  }

  // Exact inverse of `mvi_feature`'s registry insertion.
  var content = file.readAsStringSync();
  final line =
      '\n            "$snakeCase" to AppRoutes.${pascalCase}Route,';
  if (content.contains(line)) {
    content = content.replaceAll(line, '');
    file.writeAsStringSync(content);
    logger.info('📝 Removed "$snakeCase" from :shell OnDemandFeatures.kt');
  } else {
    logger.info('✓ :shell OnDemandFeatures.kt has no "$snakeCase" entry');
  }
}

/// Inverse of `mvi_feature`'s aggregated `ServiceLoader` registration — drops
/// this module's line from the single `:app`-owned
/// `META-INF/services/*.platform.FeatureEntry` file (the on-demand `FeatureEntry`
/// FQCNs are aggregated in the base module — bundletool forbids two feature
/// splits shipping the same root resource, design §4.4). Matched by the
/// `.presentation.di.<Name>FeatureEntry` suffix so the module's package is not
/// needed. Leaves the file in place (possibly with only other modules' lines).
void _removeFeatureEntryFromServiceLoader(String pascalCase, Logger logger) {
  final dir = Directory('app/src/main/resources/META-INF/services');
  if (!dir.existsSync()) {
    logger.info('✓ :app META-INF/services absent — nothing to unregister');
    return;
  }
  File? file;
  for (final entity in dir.listSync()) {
    if (entity is File &&
        entity.uri.pathSegments.last.endsWith('.platform.FeatureEntry')) {
      file = entity;
      break;
    }
  }
  if (file == null) {
    logger.info('✓ no *.platform.FeatureEntry file — nothing to unregister');
    return;
  }

  final suffix = '.presentation.di.${pascalCase}FeatureEntry';
  final kept = file
      .readAsLinesSync()
      .where((line) => line.trim().isNotEmpty && !line.trim().endsWith(suffix))
      .toList();
  final original = file.readAsStringSync();
  final rebuilt = kept.isEmpty ? '' : '${kept.join('\n')}\n';
  if (rebuilt == original) {
    logger.info('✓ ${pascalCase}FeatureEntry not registered in ${file.path}');
    return;
  }
  file.writeAsStringSync(rebuilt);
  logger.info('📝 Unregistered ${pascalCase}FeatureEntry from ${file.path}');
}

/// Inverse of `mvi_feature`'s `_writeBaseModuleSplitTitle` — drops the
/// `<name>_feature_title` string an on-demand module's `dist:title` needs from
/// the BASE module's `res/values/strings.xml`.
void _removeSplitTitleFromAppStrings(String snakeCase, Logger logger) {
  final file = _findFile('app/src/main/res/values', 'strings.xml');
  if (file == null) {
    logger.info('✓ app strings.xml absent — no split title to remove');
    return;
  }
  var content = file.readAsStringSync();
  final pattern = RegExp(
    '\\s*<string name="${snakeCase}_feature_title">[^<]*</string>',
  );
  if (pattern.hasMatch(content)) {
    content = content.replaceAll(pattern, '');
    file.writeAsStringSync(content);
    logger.info('📝 Removed ${snakeCase}_feature_title from app strings.xml');
  } else {
    logger.info('✓ ${snakeCase}_feature_title not in app strings.xml');
  }
}

// ---------------------------------------------------------------------------
// shared constants / snippets (kept byte-identical with mvi_feature/post_gen.dart)
// ---------------------------------------------------------------------------

String _appRoutesEntry(String pascalCase) =>
    '\n    /** Entry point of the $pascalCase feature. */\n'
    '    @Serializable\n'
    '    data object ${pascalCase}Route : NavKey\n';

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
