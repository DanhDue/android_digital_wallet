#!/bin/bash
# Post-generation hook for mvi_feature brick
# 
# This script updates project configuration files after generating a new feature module.
# Run this script from the project root after mason generates the module files.
#
# Usage: ./bricks/mvi_feature/hooks/post_gen.sh <feature_name> <module_path>
# Example: ./bricks/mvi_feature/hooks/post_gen.sh Payment features/payment

set -e

FEATURE_NAME="$1"
MODULE_PATH="$2"

if [ -z "$FEATURE_NAME" ] || [ -z "$MODULE_PATH" ]; then
    echo "Usage: $0 <feature_name> <module_path>"
    echo "Example: $0 Payment features/payment"
    exit 1
fi

# Convert feature name to different cases
PASCAL_CASE="$FEATURE_NAME"
CAMEL_CASE="$(echo "${FEATURE_NAME:0:1}" | tr '[:upper:]' '[:lower:]')${FEATURE_NAME:1}"
# Convert to UPPER_SNAKE_CASE (compatible with zsh/bash)
UPPER_SNAKE_CASE=$(echo "$FEATURE_NAME" | sed 's/\([a-z]\)\([A-Z]\)/\1_\2/g' | tr '[:lower:]' '[:upper:]')

# Module path for Gradle (e.g., :features:payment)
GRADLE_PATH=":$(echo "$MODULE_PATH" | tr '/' ':')"

echo "🔧 Configuring module: $GRADLE_PATH"

# 1. Add to settings.gradle.kts
SETTINGS_FILE="settings.gradle.kts"
if ! grep -q "include(\"$GRADLE_PATH\")" "$SETTINGS_FILE"; then
    echo "📝 Adding to settings.gradle.kts..."
    echo "include(\"$GRADLE_PATH\")" >> "$SETTINGS_FILE"
else
    echo "✓ Already in settings.gradle.kts"
fi

# 2. Add to Deps.kt Modules object
DEPS_FILE="buildSrc/src/main/kotlin/Deps.kt"
MODULE_CONST="feature${PASCAL_CASE}"
if ! grep -q "const val $MODULE_CONST" "$DEPS_FILE"; then
    echo "📝 Adding to Deps.kt..."
    # Insert before the closing brace of Modules object
    sed -i '' "/^object Modules {/,/^}$/ {
        /^}$/i\\
    const val $MODULE_CONST = \"$GRADLE_PATH\"
    }" "$DEPS_FILE"
else
    echo "✓ Already in Deps.kt"
fi

# 3. Add to DependencyHandlerExtensions.kt
EXTENSIONS_FILE="buildSrc/src/main/kotlin/extensions/DependencyHandlerExtensions.kt"
ACCESSOR_NAME="FEATURE_${UPPER_SNAKE_CASE}"
if ! grep -q "val DependencyHandler.$ACCESSOR_NAME" "$EXTENSIONS_FILE"; then
    echo "📝 Adding to DependencyHandlerExtensions.kt..."
    echo "" >> "$EXTENSIONS_FILE"
    echo "val DependencyHandler.$ACCESSOR_NAME" >> "$EXTENSIONS_FILE"
    echo "    get() = implementation(project(mapOf(PATH to Modules.$MODULE_CONST)))" >> "$EXTENSIONS_FILE"
else
    echo "✓ Already in DependencyHandlerExtensions.kt"
fi

echo ""
echo "✅ Module configuration complete!"
echo ""
echo "Next steps:"
echo "  Sync Gradle: ./gradlew --refresh-dependencies"
