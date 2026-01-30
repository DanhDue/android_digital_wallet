import 'dart:io';
import 'package:mason/mason.dart';

Future<void> run(HookContext context) async {
  final module = context.vars['module'] as String;
  final name = context.vars['name'] as String;

  // Convert name to different cases
  final camelCase = _toCamelCase(name);
  final pascalCase = _toPascalCase(name);

  final basePath = 'features/$module/src/main/kotlin';

  context.logger.info('🗑️  Removing subfeature: $pascalCase from $module');

  _removeDirectory(
      '$basePath/com/danhdue/$module/presentation/$camelCase', context.logger);

  // 2. Remove data layer files
  _removeFile('$basePath/com/danhdue/$module/data/models/${pascalCase}Dto.kt',
      context.logger);
  _removeFile(
      '$basePath/com/danhdue/$module/data/mappers/${pascalCase}Mapper.kt',
      context.logger);
  _removeFile(
      '$basePath/com/danhdue/$module/data/repository/${pascalCase}RepositoryImpl.kt',
      context.logger);

  // 3. Remove domain layer files
  _removeFile(
      '$basePath/com/danhdue/$module/domain/entities/${pascalCase}Entity.kt',
      context.logger);
  _removeFile(
      '$basePath/com/danhdue/$module/domain/repository/${pascalCase}Repository.kt',
      context.logger);
  _removeFile(
      '$basePath/com/danhdue/$module/domain/usecase/Get${pascalCase}DataUseCase.kt',
      context.logger);

  context.logger.success('✅ Subfeature removal complete!');

  final progress = context.logger
      .progress('Running gradle prepareKotlinBuildScriptModel...');
  try {
    final result = await Process.run(
      './gradlew',
      [':prepareKotlinBuildScriptModel', '--console=plain'],
      runInShell: true,
    );

    if (result.exitCode == 0) {
      progress.complete('Gradle build script model prepared!');
    } else {
      progress.fail('Failed to run gradle command.\n${result.stderr}');
      context.logger.err(result.stderr.toString());
    }

    await _removeFromDataModule(context);
    await _removeFromDomainModule(context);
    await _removeFromNavigationModule(context);
  } catch (e) {
    progress.fail('Failed to run gradle command: $e');
    context.logger.err(e.toString());
  }
}

Future<void> _removeFromNavigationModule(HookContext context) async {
  final module = context.vars['module'] as String;
  final name = context.vars['name'] as String;
  final pascalCase = _toPascalCase(name);
  final camelCase = _toCamelCase(name);
  final modulePascal = _toPascalCase(module);

  final file = File(
      'features/$module/src/main/kotlin/com/danhdue/$module/presentation/di/${modulePascal}NavigationModule.kt');

  if (!file.existsSync()) {
    return;
  }

  var content = await file.readAsString();

  final imports = [
    'import com.danhdue.$module.presentation.$camelCase.${pascalCase}Event\n',
    'import com.danhdue.$module.presentation.$camelCase.${pascalCase}Root\n',
    'import com.danhdue.$module.presentation.$camelCase.${pascalCase}Route\n',
    'import com.danhdue.$module.presentation.$camelCase.${pascalCase}Event',
    'import com.danhdue.$module.presentation.$camelCase.${pascalCase}Root',
    'import com.danhdue.$module.presentation.$camelCase.${pascalCase}Route',
  ];

  for (final importLine in imports) {
    content = content.replaceAll(importLine, '');
  }

  // Remove the entry block.
  // entry<DetailNav2Route> { ... }
  // Using Regex to match entry<...Route> { ... } including nested braces if possible,
  // but standard Regex doesn't support recursive balancing.
  // However, we know the structure of our generated code.

  // Regex to match:
  // entry<NameRoute> {\n ... \n            }
  // We can try to match loosely until the closing brace indentation.

  final entryRegex = RegExp(
    r'\s+entry<' + pascalCase + r'Route>\s*\{[\s\S]*?\}\s*\}',
    multiLine: true,
  );

  // Note: the greedy match [\s\S]*? might match too little or too much if brace counting isn't strict.
  // But since we control the generated code format, it's safer to rely on the indentation or specific closing pattern.
  // The generated code ends with:
  //                 )
  //             }

  // A safer regex might be capturing the specific block structure we inject.
  final specificEntryRegex = RegExp(
      r'\s+entry<' +
          pascalCase +
          r'Route>\s*\{\s+' +
          pascalCase +
          r'Root\(\s+onEvent\s*=\s*\{[\s\S]*?\}\s*,\s*\)\s+\}',
      multiLine: true);

  content = content.replaceAll(specificEntryRegex, '');

  await file.writeAsString(content);
  context.logger.success('Removed Navigation from ${file.path}');
}

Future<void> _removeFromDataModule(HookContext context) async {
  final module = context.vars['module'] as String;
  final name = context.vars['name'] as String;
  final pascalCase = _toPascalCase(name);
  final modulePascal = _toPascalCase(module);

  final file = File(
      'features/$module/src/main/kotlin/com/danhdue/$module/data/di/${modulePascal}DataModule.kt');

  if (!file.existsSync()) {
    return;
  }

  var content = await file.readAsString();

  final imports = [
    'import com.danhdue.$module.data.repository.${pascalCase}RepositoryImpl\n',
    'import com.danhdue.$module.domain.repository.${pascalCase}Repository\n',
    'import com.danhdue.$module.data.repository.${pascalCase}RepositoryImpl',
    'import com.danhdue.$module.domain.repository.${pascalCase}Repository',
  ];

  for (final importLine in imports) {
    content = content.replaceAll(importLine, '');
  }

  final bindingCodeRegex = RegExp(
    r'\n[ \t]*@Binds\s+@Singleton\s+abstract\s+fun\s+bind' +
        pascalCase +
        r'Repository\([^)]+\):\s*' +
        pascalCase +
        r'Repository',
    multiLine: true,
  );

  content = content.replaceAll(bindingCodeRegex, '');

  await file.writeAsString(content);
  context.logger.success('Removed DI from ${file.path}');
}

Future<void> _removeFromDomainModule(HookContext context) async {
  final module = context.vars['module'] as String;
  final name = context.vars['name'] as String;
  final pascalCase = _toPascalCase(name);
  final modulePascal = _toPascalCase(module);

  final file = File(
      'features/$module/src/main/kotlin/com/danhdue/$module/domain/di/${modulePascal}DomainModule.kt');

  if (!file.existsSync()) {
    return;
  }

  var content = await file.readAsString();

  final imports = [
    'import com.danhdue.$module.domain.repository.${pascalCase}Repository\n',
    'import com.danhdue.$module.domain.usecase.Get${pascalCase}DataUseCase\n',
    'import com.danhdue.$module.domain.repository.${pascalCase}Repository',
    'import com.danhdue.$module.domain.usecase.Get${pascalCase}DataUseCase',
  ];

  for (final importLine in imports) {
    content = content.replaceAll(importLine, '');
  }

  final providerCodeRegex = RegExp(
    r'\n[ \t]*@Provides\s+@ViewModelScoped\s+fun\s+provideGet' +
        pascalCase +
        r'DataUseCase\([^)]+\):\s*Get' +
        pascalCase +
        r'DataUseCase\s*=\s*Get' +
        pascalCase +
        r'DataUseCase\([^)]+\)',
    multiLine: true,
  );

  content = content.replaceAll(providerCodeRegex, '');

  await file.writeAsString(content);
  context.logger.success('Removed DI from ${file.path}');
}

void _removeDirectory(String path, Logger logger) {
  final dir = Directory(path);
  if (dir.existsSync()) {
    dir.deleteSync(recursive: true);
    logger.info('🗑️  Removed $path/');
  }
}

void _removeFile(String path, Logger logger) {
  final file = File(path);
  if (file.existsSync()) {
    file.deleteSync();
    logger.info('🗑️  Removed $path');
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

String _toCamelCase(String input) {
  if (input.isEmpty) return input;
  final pascal = _toPascalCase(input);
  return '${pascal[0].toLowerCase()}${pascal.substring(1)}';
}
