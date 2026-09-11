import 'dart:io';
import 'package:mason/mason.dart';

Future<void> run(HookContext context) async {
  final name = context.vars['name'] as String;
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

  // 1. Patch build.gradle.kts: add compose plugin, buildFeatures, compose dependencies
  final buildFile = File('$targetDir/build.gradle.kts');
  if (buildFile.existsSync()) {
    var content = buildFile.readAsStringSync();
    if (!content.contains('ANDROID_COMPOSE_PLUGIN_ID')) {
      content = content.replaceFirst(
        'plugins {',
        'plugins {\n    id(Deps.ANDROID_COMPOSE_PLUGIN_ID)',
      );
    }
    if (!content.contains('compose = true')) {
      if (content.contains('buildFeatures {')) {
        content = content.replaceFirst('buildFeatures {', 'buildFeatures {\n        compose = true');
      } else if (content.contains('defaultConfig {')) {
        content = content.replaceFirst(
          'defaultConfig {',
          'buildFeatures {\n        compose = true\n    }\n\n    defaultConfig {',
        );
      }
    }
    if (!content.contains('Deps.Compose.composeBOM')) {
      const composeDeps = '''
    // Jetpack Compose (Pure, No Hilt)
    implementation(platform(Deps.Compose.composeBOM))
    implementation(Deps.Compose.composeUI)
    implementation(Deps.Compose.material3)
    implementation(Deps.Compose.runtime)
    implementation(Deps.Compose.foundation)
    implementation(Deps.Compose.activityCompose)
    implementation(Deps.Compose.lifecycleViewmodelCompose)
''';
      if (content.contains('// Flutter Embedding')) {
        content = content.replaceFirst('// Flutter Embedding', '$composeDeps\n    // Flutter Embedding');
      } else {
        content = content.replaceFirst('dependencies {', 'dependencies {\n$composeDeps');
      }
    }
    buildFile.writeAsStringSync(content);
  }

  // 2. Patch Plugin.kt to register PlatformViewFactory
  final pluginFile = File('$targetDir/src/main/kotlin/$packagePath/platform/${pascalCaseName}Plugin.kt');
  if (pluginFile.existsSync()) {
    var content = pluginFile.readAsStringSync();
    if (!content.contains('${pascalCaseName}PlatformViewFactory')) {
      final registration = '''

        // Register Jetpack Compose PlatformView
        binding.platformViewRegistry.registerViewFactory(
            VIEW_TYPE,
            ${pascalCaseName}PlatformViewFactory {
                component.get${pascalCaseName}ViewModel()
            }
        )''';

      final companion = '''

    companion object {
        const val VIEW_TYPE = "$package/native_view"
    }''';

      if (content.contains('override fun onDetachedFromEngine')) {
        content = content.replaceFirst(
          '    override fun onDetachedFromEngine',
          '$registration\n    }\n\n    override fun onDetachedFromEngine',
        );
      }
      if (!content.contains('VIEW_TYPE')) {
        final lastBrace = content.lastIndexOf('}');
        if (lastBrace != -1) {
          content = '${content.substring(0, lastBrace)}$companion\n}\n';
        }
      }
      pluginFile.writeAsStringSync(content);
    }
  }

  // 3. Patch Component.kt to expose getViewModel()
  final compFile = File('$targetDir/src/main/kotlin/$packagePath/di/${pascalCaseName}Component.kt');
  if (compFile.existsSync()) {
    var content = compFile.readAsStringSync();
    if (!content.contains('get${pascalCaseName}ViewModel')) {
      content = content.replaceFirst(
        'interface ${pascalCaseName}Component {',
        'interface ${pascalCaseName}Component {\n    fun get${pascalCaseName}ViewModel(): $package.presentation.${pascalCaseName}ViewModel',
      );
      compFile.writeAsStringSync(content);
    }
  }

  context.logger.info('Native UI added to $snakeCaseName successfully.');
}
