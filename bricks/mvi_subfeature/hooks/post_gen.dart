import 'dart:io';

import 'package:mason/mason.dart';

Future<void> run(HookContext context) async {
  final progress = context.logger.progress(
    'Running gradle prepareKotlinBuildScriptModel...',
  );

  try {
    final result = await Process.run(
        './gradlew',
        [
          ':prepareKotlinBuildScriptModel',
          '--console=plain',
        ],
        runInShell: true);

    if (result.exitCode == 0) {
      progress.complete('Gradle build script model prepared!');
    } else {
      progress.fail('Failed to run gradle command.\n${result.stderr}');
      context.logger.err(result.stderr.toString());
    }

    await _injectDataModule(context);
    await _injectDomainModule(context);
    await _injectNavigationModule(context);
  } catch (e) {
    progress.fail('Failed to run gradle command: $e');
    context.logger.err(e.toString());
  }
}

Future<void> _injectNavigationModule(HookContext context) async {
  final module = context.vars['module'] as String;
  final name = context.vars['name'] as String;
  final pascalCase = _toPascalCase(name);
  final camelCase = _toCamelCase(name);
  final modulePascal = _toPascalCase(module);

  final file = File(
      'features/$module/src/main/kotlin/presentation/di/${modulePascal}NavigationModule.kt');

  if (!file.existsSync()) {
    context.logger.warn('NavigationModule not found at ${file.path}');
    return;
  }

  var content = await file.readAsString();

  // Add imports
  final imports = [
    'import com.danhdue.$module.presentation.$camelCase.${pascalCase}Event',
    'import com.danhdue.$module.presentation.$camelCase.${pascalCase}Root',
    'import com.danhdue.$module.presentation.$camelCase.${pascalCase}Route',
  ];

  for (final importLine in imports) {
    if (!content.contains(importLine)) {
      content = content.replaceFirst('import', '$importLine\nimport');
    }
  }

  // Add entry
  // We look for logic inside EntryProviderInstaller lambda.
  // We can search for "entry<" or just append inside the lambda.
  // Assuming standard formatting from template: "fun provide... = \n {"

  final entryCode = '''
            entry<${pascalCase}Route> {
                ${pascalCase}Root(
                    onEvent = { event ->
                        when (event) {
                             ${pascalCase}Event.NavigateBack -> navigator.popBackStack()
                        }
                    },
                )
            }
''';

  if (!content.contains('entry<${pascalCase}Route>')) {
    // Naive injection: find the last closing brace of the lambda.
    // The module usually ends with:
    //         }
    // }
    // So we look for the last '}' and insert before it?
    // No, the provider is a lambda: { ... } inside a function.
    // Safer to find "entry<" and append after the matching block?
    // Or find the start of the lambda ` = \n {` and prepend/append.

    // Let's try to find the `EntryProviderInstaller` return block.
    // It usually starts with `{` after the function signature.

    // Simplest robust way given the context: Use a known anchor.
    // e.g. "entry<"
    final lastEntryIndex = content.lastIndexOf('entry<');
    if (lastEntryIndex != -1) {
      // Find the closing brace of this entry block to append AFTER it.
      // This requires counting braces.
      int braceCount = 0;
      int i = content.indexOf('{', lastEntryIndex);
      if (i != -1) {
        braceCount++;
        i++;
        while (i < content.length && braceCount > 0) {
          if (content[i] == '{')
            braceCount++;
          else if (content[i] == '}') braceCount--;
          i++;
        }
        if (braceCount == 0) {
          // i is now at position after the closing brace of the last entry.
          content = content.replaceRange(i, i, '\n$entryCode');
        }
      }
    } else {
      // No entries yet? Look for the opening of the lambda.
      // fun provide...(navigator: Navigator): EntryProviderInstaller =\n {
      final lambdaStart = content.indexOf('EntryProviderInstaller =');
      if (lambdaStart != -1) {
        final braceIndex = content.indexOf('{', lambdaStart);
        if (braceIndex != -1) {
          content = content.replaceRange(
              braceIndex + 1, braceIndex + 1, '\n$entryCode');
        }
      }
    }
  }

  await file.writeAsString(content);
  context.logger.success('Injected Navigation into ${file.path}');
}

Future<void> _injectDataModule(HookContext context) async {
  final module = context.vars['module'] as String;
  final name = context.vars['name'] as String;
  final pascalCase = _toPascalCase(name);
  final modulePascal = _toPascalCase(module);

  final file = File(
      'features/$module/src/main/kotlin/data/di/${modulePascal}DataModule.kt');

  if (!file.existsSync()) {
    context.logger.warn('DataModule not found at ${file.path}');
    return;
  }

  var content = await file.readAsString();

  // Add imports
  final imports = [
    'import com.danhdue.$module.data.repository.${pascalCase}RepositoryImpl',
    'import com.danhdue.$module.domain.repository.${pascalCase}Repository',
  ];

  for (final importLine in imports) {
    if (!content.contains(importLine)) {
      content = content.replaceFirst('import', '$importLine\nimport');
    }
  }

  // Add binding
  const bindingMarker = 'abstract class';
  final bindingCode = '''
    @Binds
    @Singleton
    abstract fun bind${pascalCase}Repository(
        impl: ${pascalCase}RepositoryImpl,
    ): ${pascalCase}Repository
''';

  if (!content.contains('bind${pascalCase}Repository')) {
    final classIndex = content.indexOf(bindingMarker);
    if (classIndex != -1) {
      final braceIndex = content.indexOf('{', classIndex);
      if (braceIndex != -1) {
        content = content.replaceRange(
            braceIndex + 1, braceIndex + 1, '\n$bindingCode');
      }
    }
  }

  await file.writeAsString(content);
  context.logger.success('Injected DI into ${file.path}');
}

Future<void> _injectDomainModule(HookContext context) async {
  final module = context.vars['module'] as String;
  final name = context.vars['name'] as String;
  final pascalCase = _toPascalCase(name);
  final modulePascal = _toPascalCase(module);

  final file = File(
      'features/$module/src/main/kotlin/domain/di/${modulePascal}DomainModule.kt');

  if (!file.existsSync()) {
    context.logger.warn('DomainModule not found at ${file.path}');
    return;
  }

  var content = await file.readAsString();

  // Add imports
  final imports = [
    'import com.danhdue.$module.domain.repository.${pascalCase}Repository',
    'import com.danhdue.$module.domain.usecase.Get${pascalCase}DataUseCase',
  ];

  for (final importLine in imports) {
    if (!content.contains(importLine)) {
      content = content.replaceFirst('import', '$importLine\nimport');
    }
  }

  // Add provider
  const providerMarker = 'object';
  final providerCode = '''
    @Provides
    @ViewModelScoped
    fun provideGet${pascalCase}DataUseCase(repository: ${pascalCase}Repository): Get${pascalCase}DataUseCase =
        Get${pascalCase}DataUseCase(repository)
''';

  if (!content.contains('provideGet${pascalCase}DataUseCase')) {
    final objectIndex = content.indexOf(providerMarker);
    if (objectIndex != -1) {
      final braceIndex = content.indexOf('{', objectIndex);
      if (braceIndex != -1) {
        content = content.replaceRange(
            braceIndex + 1, braceIndex + 1, '\n$providerCode');
      }
    }
  }

  await file.writeAsString(content);
  context.logger.success('Injected DI into ${file.path}');
}

String _toPascalCase(String input) {
  if (input.isEmpty) return input;
  return input
      .split('_')
      .map((e) => e.isEmpty
          ? ''
          : '${e[0].toUpperCase()}${e.substring(1).toLowerCase()}')
      .join();
}

String _toCamelCase(String input) {
  if (input.isEmpty) return input;
  final pascal = _toPascalCase(input);
  return '${pascal[0].toLowerCase()}${pascal.substring(1)}';
}
