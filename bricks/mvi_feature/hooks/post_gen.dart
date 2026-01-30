import 'dart:io';
import 'package:mason/mason.dart';

void run(HookContext context) {
  final name = context.vars['name'] as String;

  // Convert name to different cases
  final pascalCase = _toPascalCase(name);
  final snakeCase = _toSnakeCase(name);
  final upperSnakeCase = snakeCase.toUpperCase();

  final gradlePath = ':features:$snakeCase';

  context.logger.info('🔧 Configuring module: $gradlePath');

  // 1. Update settings.gradle.kts
  _updateSettingsGradle(gradlePath, context.logger);

  // 2. Update Deps.kt
  _updateDepsKt(pascalCase, gradlePath, context.logger);

  // 3. Update DependencyHandlerExtensions.kt
  _updateDependencyHandler(pascalCase, upperSnakeCase, context.logger);

  // 4. Update app/build.gradle.kts
  _updateAppBuildGradle(upperSnakeCase, context.logger);

  context.logger.success('✅ Module configuration complete!');

  // 4. Run Gradle sync
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
    // Find the closing brace of Modules object and insert before it
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

void _updateAppBuildGradle(String upperSnakeCase, Logger logger) {
  final file = File('app/build.gradle.kts');
  if (!file.existsSync()) {
    logger.warn('app/build.gradle.kts not found');
    return;
  }

  var content = file.readAsStringSync();
  final accessorName = 'FEATURE_$upperSnakeCase';
  final importLine = 'import extensions.$accessorName';

  var updated = false;

  // Add import if not present
  if (!content.contains(importLine)) {
    // Find the last import statement and add after it
    final importPattern = RegExp(r'import extensions\.FEATURE_\w+');
    final matches = importPattern.allMatches(content).toList();
    if (matches.isNotEmpty) {
      final lastMatch = matches.last;
      content =
          '${content.substring(0, lastMatch.end)}\n$importLine${content.substring(lastMatch.end)}';
      updated = true;
    }
  }

  // Add dependency accessor if not present
  if (!content.contains('    $accessorName')) {
    // Find the FEATURE_ dependencies section and add before closing
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
    logger.info('📝 Added to app/build.gradle.kts');
  } else {
    logger.info('✓ Already in app/build.gradle.kts');
  }
}

String _toPascalCase(String input) {
  if (input.isEmpty) return input;
  // Handle already PascalCase
  if (input[0] == input[0].toUpperCase() && !input.contains('_')) {
    return input;
  }
  // Handle snake_case
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
  // Handle PascalCase to snake_case
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
