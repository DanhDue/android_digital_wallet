import 'dart:io';
import 'package:mason/mason.dart';

Future<void> run(HookContext context) async {
  final name = context.vars['name'] as String;
  final hasUi = context.vars['has_ui'] as bool? ?? false;
  final snakeCaseName = name.snakeCase;
  final pascalCaseName = name.pascalCase;
  final package = context.vars['package'] as String;
  final packagePath = package.replaceAll('.', '/');

  String targetDir = snakeCaseName;
  if (!Directory(targetDir).existsSync()) {
    if (File('build.gradle.kts').existsSync()) {
      targetDir = '.';
    }
  }

  if (!hasUi) {
    // 1. Remove presentation directory
    final presDir = Directory('$targetDir/src/main/kotlin/$packagePath/presentation');
    if (presDir.existsSync()) {
      presDir.deleteSync(recursive: true);
    }

    // 2. Remove PlatformViewFactory
    final factoryFile = File('$targetDir/src/main/kotlin/$packagePath/platform/${pascalCaseName}PlatformViewFactory.kt');
    if (factoryFile.existsSync()) {
      factoryFile.deleteSync();
    }

    // 3. Patch build.gradle.kts: remove compose plugin, compose buildFeatures, and compose dependencies
    final buildFile = File('$targetDir/build.gradle.kts');
    if (buildFile.existsSync()) {
      var content = buildFile.readAsStringSync();
      content = content.replaceAll(RegExp(r'^\s*id\(Deps\.ANDROID_COMPOSE_PLUGIN_ID\)\s*$\n?', multiLine: true), '');
      content = content.replaceAll(RegExp(r'^\s*buildFeatures\s*\{\s*compose\s*=\s*true\s*\}\s*$\n?', multiLine: true), '');
      content = content.replaceAll(RegExp(r'^\s*implementation\(platform\(Deps\.Compose\.composeBOM\)\)\s*$\n?', multiLine: true), '');
      content = content.replaceAll(RegExp(r'^\s*implementation\(Deps\.Compose\..*\)\s*$\n?', multiLine: true), '');
      buildFile.writeAsStringSync(content);
    }

    // 4. Patch Plugin.kt to omit PlatformView registration
    final pluginFile = File('$targetDir/src/main/kotlin/$packagePath/platform/${pascalCaseName}Plugin.kt');
    if (pluginFile.existsSync()) {
      var content = pluginFile.readAsStringSync();
      content = content.replaceAll(RegExp(r'^\s*// Register Jetpack Compose PlatformView[\s\S]*?VIEW_TYPE\s*\)\s*$\n?', multiLine: true), '');
      content = content.replaceAll(RegExp(r'^\s*companion\s*object\s*\{[\s\S]*?VIEW_TYPE[\s\S]*?\}\s*$\n?', multiLine: true), '');
      pluginFile.writeAsStringSync(content);
    }

    // 5. Patch Component.kt to omit getViewModel()
    final compFile = File('$targetDir/src/main/kotlin/$packagePath/di/${pascalCaseName}Component.kt');
    if (compFile.existsSync()) {
      var content = compFile.readAsStringSync();
      content = content.replaceAll(RegExp(r'^\s*fun get.*ViewModel\(\):.*$\n?', multiLine: true), '');
      compFile.writeAsStringSync(content);
    }
  }

  context.logger.info('Plugin $snakeCaseName initialized (has_ui=$hasUi).');
}
