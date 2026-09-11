import 'package:mason/mason.dart';

Future<void> run(HookContext context) async {
  final name = context.vars['name'] as String;
  final package = context.vars['package'] as String? ?? 'com.danhdue.${name.snakeCase}';
  context.vars['name'] = name.snakeCase;
  context.vars['package'] = package;
  context.vars['packagePath'] = package.replaceAll('.', '/');
}
