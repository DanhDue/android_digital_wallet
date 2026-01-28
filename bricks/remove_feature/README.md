# Remove Feature Brick

Removes an MVI feature module and all its configurations from the project.

## Usage

```bash
mason make remove_feature --name Sample
```

This automatically:
1. Removes `features/sample/` directory
2. Removes `include(":features:sample")` from `settings.gradle.kts`
3. Removes `featureSample` from `Deps.kt` Modules object
4. Removes `FEATURE_SAMPLE` accessor from `DependencyHandlerExtensions.kt`
5. Removes import and `FEATURE_SAMPLE` from `app/build.gradle.kts`
6. Runs Gradle sync to verify removal

## Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `name` | Feature name to remove (PascalCase) | `Sample`, `Payment` |

## Warning

⚠️ This action is **irreversible**. Make sure you have committed or backed up any changes before removing a feature module.
