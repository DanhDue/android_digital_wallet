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

  // on-demand-only wire points (no-ops when the feature was install-time).
  _removeFromAppDynamicFeatures(gradlePath, context.logger);
  _removeRouteFromAppRoutes(pascalCase, context.logger);
  _removeShellInstallBranch(snakeCase, pascalCase, context.logger);

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

/// Exact inverse of `mvi_feature`'s `appDfmCompileOnlyGuard()`.
String _appDfmCompileOnlyGuard() =>
    '\n// Added by `mvi_feature --delivery on-demand`: a dynamic-feature base module\n'
    '// (`:app`) must not expose `compileOnly` Android dependencies.\n'
    '// TODO(task_14): fold this into the :app / buildSrc DFM setup.\n'
    'configurations.configureEach {\n'
    '    exclude(group = "org.projectlombok", module = "lombok")\n'
    '}\n';

/// Exact inverse of the `mvi_feature` on-demand `AppRoutes` insertion.
void _removeRouteFromAppRoutes(String pascalCase, Logger logger) {
  final file = File(_appRoutesPath);
  if (!file.existsSync()) {
    logger.info('✓ AppRoutes.kt absent — nothing to unwire');
    return;
  }

  var content = file.readAsStringSync();
  final entry = _appRoutesEntry(pascalCase);
  if (!content.contains(entry)) {
    logger.info('✓ ${pascalCase}Route not in AppRoutes.kt');
    return;
  }

  content = content.replaceAll(entry, '');
  file.writeAsStringSync(content);
  logger.info('📝 Removed ${pascalCase}Route from :platform AppRoutes.kt');
}

/// Removes the guarded `:shell` on-demand install branch. Guarded on
/// `shell/build.gradle.kts` — a no-op before Phase 2 / Task 14.
void _removeShellInstallBranch(String snakeCase, String pascalCase, Logger logger) {
  if (!File('shell/build.gradle.kts').existsSync()) {
    logger.info('✓ :shell absent — no on-demand install branch to remove');
    return;
  }

  final file = File(
    'shell/src/main/kotlin/com/danhdue/shell/navigation/OnDemandInstallBranches.kt',
  );
  if (!file.existsSync()) {
    logger.info('✓ OnDemandInstallBranches.kt absent — nothing to remove');
    return;
  }

  var content = file.readAsStringSync();
  final line =
      '\n// on-demand: $snakeCase -> AppRoutes.${pascalCase}Route  // TODO(task_14): ensureInstalled("$snakeCase")\n';
  if (content.contains(line)) {
    content = content.replaceAll(line, '');
    file.writeAsStringSync(content);
    logger.info('📝 Removed :shell install branch for "$snakeCase"');
  } else {
    logger.info('✓ :shell install branch for "$snakeCase" not present');
  }
}

// ---------------------------------------------------------------------------
// shared constants / snippets (kept byte-identical with mvi_feature/post_gen.dart)
// ---------------------------------------------------------------------------

const _appRoutesPath =
    'platform/src/main/kotlin/com/danhdue/platform/AppRoutes.kt';

String _appRoutesEntry(String pascalCase) =>
    '\n    /** Entry point of the $pascalCase feature (an on-demand dynamic feature module). */\n'
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
