import 'dart:io';
import 'package:mason/mason.dart';

void run(HookContext context) {
  final module = context.vars['module'] as String;
  final name = context.vars['name'] as String;

  // Convert name to different cases
  final camelCase = _toCamelCase(name);
  final pascalCase = _toPascalCase(name);

  final basePath = 'features/$module/src/main/kotlin';

  context.logger.info('🗑️  Removing subfeature: $pascalCase from $module');

  // 1. Remove presentation folder (camelCase)
  _removeDirectory('$basePath/presentation/$camelCase', context.logger);

  // 2. Remove data layer files
  _removeFile('$basePath/data/di/${pascalCase}DataModule.kt', context.logger);
  _removeFile('$basePath/data/models/${pascalCase}Dto.kt', context.logger);
  _removeFile('$basePath/data/mappers/${pascalCase}Mapper.kt', context.logger);
  _removeFile('$basePath/data/repository/${pascalCase}RepositoryImpl.kt',
      context.logger);

  // 3. Remove domain layer files
  _removeFile(
      '$basePath/domain/di/${pascalCase}DomainModule.kt', context.logger);
  _removeFile(
      '$basePath/domain/entities/${pascalCase}Entity.kt', context.logger);
  _removeFile(
      '$basePath/domain/repository/${pascalCase}Repository.kt', context.logger);
  _removeFile('$basePath/domain/usecase/Get${pascalCase}DataUseCase.kt',
      context.logger);

  context.logger.success('✅ Subfeature removal complete!');
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
