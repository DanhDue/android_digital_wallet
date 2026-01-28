import 'dart:io';
import 'package:mason/mason.dart';

void run(HookContext context) {
  final name = context.vars['name'] as String;

  // Convert name to different cases
  final pascalCase = _toPascalCase(name);
  final snakeCase = _toSnakeCase(name);
  final upperSnakeCase = snakeCase.toUpperCase();

  final gradlePath = ':features:$snakeCase';
  final modulePath = 'features/$snakeCase';

  context.logger.info('🗑️  Removing module: $gradlePath');

  // 1. Remove feature directory
  _removeFeatureDirectory(modulePath, context.logger);

  // 2. Update settings.gradle.kts
  _removeFromSettingsGradle(gradlePath, context.logger);

  // 3. Update Deps.kt
  _removeFromDepsKt(pascalCase, context.logger);

  // 4. Update DependencyHandlerExtensions.kt
  _removeFromDependencyHandler(upperSnakeCase, context.logger);

  // 5. Update app/build.gradle.kts
  _removeFromAppBuildGradle(upperSnakeCase, context.logger);

  context.logger.success('✅ Module removal complete!');

  // 6. Run Gradle sync
  context.logger.info('');
  context.logger.info('🔄 Running Gradle sync...');
  final result = Process.runSync(
    './gradlew',
    ['--refresh-dependencies'],
    runInShell: true,
  );

  if (result.exitCode == 0) {
    context.logger.success('✅ Gradle sync complete!');
  } else {
    context.logger.warn(
        '⚠️ Gradle sync failed. Run manually: ./gradlew --refresh-dependencies');
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

void _removeFromAppBuildGradle(String upperSnakeCase, Logger logger) {
  final file = File('app/build.gradle.kts');
  if (!file.existsSync()) {
    logger.warn('app/build.gradle.kts not found');
    return;
  }

  var content = file.readAsStringSync();
  final accessorName = 'FEATURE_$upperSnakeCase';
  var updated = false;

  // Remove import
  final importLine = 'import extensions.$accessorName\n';
  if (content.contains(importLine)) {
    content = content.replaceAll(importLine, '');
    updated = true;
  }

  // Remove dependency
  final depLine = '    $accessorName\n';
  if (content.contains(depLine)) {
    content = content.replaceAll(depLine, '');
    updated = true;
  }

  if (updated) {
    file.writeAsStringSync(content);
    logger.info('📝 Removed from app/build.gradle.kts');
  } else {
    logger.info('✓ Not in app/build.gradle.kts');
  }
}

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
