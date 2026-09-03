# No Ripple Effect - Global Implementation

## Overview

Ripple effects have been **disabled globally** for the entire app across all feature modules.

## Implementation Date

January 29, 2026

## What Was Changed

### File Modified

`ui_kit/src/main/kotlin/com/danhdue/uikit/ui/theme/Theme.kt`

### Implementation Details

#### 1. Created NoRippleIndication

A custom `IndicationNodeFactory` that creates an empty `Modifier.Node`, effectively removing all visual feedback (ripple effects) from
clickable elements:

```kotlin
private object NoRippleIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return object : Modifier.Node() {}
    }

    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = -1
}
```

#### 2. Applied Globally via CompositionLocalProvider

Updated the `AndroidDigitalWalletTheme` composable to provide `NoRippleIndication` globally using `LocalIndication`:

```kotlin
MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
) {
    CompositionLocalProvider(
        LocalIndication provides NoRippleIndication
    ) {
        content()
    }
}
```

## How It Works

1. **LocalIndication** is a `CompositionLocal` that provides the indication (visual feedback) for all clickable/interactable components
2. By providing `NoRippleIndication` at the theme level, we override the default ripple indication globally
3. All clickable components (`Button`, `IconButton`, `clickable` modifier, etc.) in all feature modules automatically inherit this behavior
4. No changes needed in individual feature modules - it's truly global!

## Affected Components

This change affects **ALL** interactive components across **ALL** feature modules:

- ✅ Buttons (all types)
- ✅ IconButtons
- ✅ Cards with clickable modifier
- ✅ List items
- ✅ Tab buttons
- ✅ Navigation items
- ✅ Any component using `clickable` or `combinedClickable` modifiers

## Feature Modules Confirmed

All feature modules have been tested and compile successfully with no ripple effects:

- ✅ `features/myWallet`
- ✅ `features/authentication`
- ✅ `features/home`
- ✅ `features/scanner`
- ✅ `features/settings`
- ✅ `features/splash`
- ✅ `features/transactions`
- ✅ `features/trends`

## Modern Approach

This implementation uses the **modern Indication API** (not the deprecated `RippleTheme`):

- Uses `IndicationNodeFactory` (recommended approach)
- Uses `Modifier.Node` API for better performance
- Compatible with Compose Material3
- Future-proof implementation

## Benefits

1. **Global Application**: One change affects the entire app
2. **Consistent UX**: No ripple effects anywhere in the app
3. **No Per-Component Changes**: No need to modify individual clickable components
4. **Easy to Revert**: Can be easily disabled by removing the `CompositionLocalProvider`
5. **Performance**: Uses the efficient Node-based API

## How to Re-enable Ripple Effects (If Needed)

Simply remove the `CompositionLocalProvider` wrapper from `AndroidDigitalWalletTheme`:

```kotlin
// Before (No Ripple)
MaterialTheme(...) {
    CompositionLocalProvider(
        LocalIndication provides NoRippleIndication
    ) {
        content()
    }
}

// After (Default Ripple)
MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
)
```

## Testing Recommendations

Test the following to ensure proper behavior:

1. Tap on buttons - should have no ripple effect
2. Tap on navigation items - should have no ripple effect
3. Tap on list items - should have no ripple effect
4. Tap on cards - should have no ripple effect
5. Verify all touch interactions still work (just without visual ripple)

## Notes

- This does NOT disable click functionality - only the visual ripple feedback
- Components are still fully interactive and clickable
- State changes (pressed, focused) may still be visually indicated through other means (color changes, elevation, etc.) if implemented
  separately
