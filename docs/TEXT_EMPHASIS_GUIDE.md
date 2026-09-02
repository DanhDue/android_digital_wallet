# Text Emphasis Guide for Material 3

This guide explains how to use emphasized text in your Android Digital Wallet app using Material 3 design principles.

## What is Text Emphasis?

In Material Design 3, emphasis helps create visual hierarchy and draw attention to important content. There are two main approaches:

### 1. **Font Weight Emphasis** (Boldness)

Uses different font weights to make text stand out visually.

### 2. **Opacity-Based Emphasis** (Material Design Recommendation)

Uses different opacity levels to indicate content importance:

- **High emphasis**: 100% opacity (most important content)
- **Medium emphasis**: 60% opacity (secondary content)
- **Disabled**: 38% opacity (disabled/inactive content)

## Quick Start Examples

### Method 1: Direct Font Weight Parameter

The simplest way - just add `fontWeight` parameter to your Text composable:

```kotlin
Text(
    text = "Important Message",
    style = MaterialTheme.typography.bodyLarge,
    fontWeight = FontWeight.Bold  // ← Add this
)
```

### Method 2: Modify TextStyle with copy()

Create a new style by copying and modifying an existing one:

```kotlin
Text(
    text = "Important Message",
    style = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Bold
    )
)
```

### Method 3: Use Naturally Emphasized Styles

Some Material 3 typography styles already have Medium font weight:

- `titleMedium` and `titleSmall`
- All `label` styles (`labelLarge`, `labelMedium`, `labelSmall`)

```kotlin
Text(
    text = "Button Label",
    style = MaterialTheme.typography.labelLarge  // Already Medium weight
)
```

### Method 4: Use Custom Extension Functions

We've created helper functions in `TextEmphasis.kt`:

```kotlin
// Font weight emphasis
Text(
    text = "Important Message",
    style = MaterialTheme.typography.bodyLarge.withFontWeight(FontWeight.Bold)
)

// Opacity-based emphasis
Text(
    text = "Secondary text",
    style = MaterialTheme.typography.bodySmall.withEmphasis(TextEmphasis.Medium)
)
```

### Method 5: Pre-defined Emphasized Body Text

Use the `BodyTextEmphasis` object for common body text emphasis:

```kotlin
Text(
    text = "Emphasized body text",
    style = BodyTextEmphasis.bodyLargeEmphasis()
)
```

## Available Font Weights

From lightest to boldest:

- `FontWeight.Thin` (100)
- `FontWeight.ExtraLight` (200)
- `FontWeight.Light` (300)
- `FontWeight.Normal` (400) ← Default for most text
- `FontWeight.Medium` (500) ← Default for titles and labels
- `FontWeight.SemiBold` (600)
- `FontWeight.Bold` (700) ← Most common for emphasis
- `FontWeight.ExtraBold` (800)
- `FontWeight.Black` (900)

## Real-World Example

Here's a complete example showing a card with different emphasis levels:

```kotlin
@Composable
fun TransactionCard() {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title - naturally emphasized with titleMedium
            Text(
                text = "Payment Received",
                style = MaterialTheme.typography.titleMedium
            )

            // Amount - bold emphasis for importance
            Text(
                text = "$250.00",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            // From label - medium weight (naturally emphasized)
            Text(
                text = "FROM",
                style = MaterialTheme.typography.labelSmall
            )

            // Name - regular body text
            Text(
                text = "John Doe",
                style = MaterialTheme.typography.bodyMedium
            )

            // Timestamp - de-emphasized with medium opacity
            Text(
                text = "2 hours ago",
                style = MaterialTheme.typography.bodySmall.withEmphasis(TextEmphasis.Medium)
            )
        }
    }
}
```

## Best Practices

1. **Don't overuse bold text** - Use it sparingly for truly important content
2. **Combine with typography scale** - Use larger sizes for more important content, not just bold
3. **Consider accessibility** - Don't rely solely on opacity for meaning (color blind users)
4. **Be consistent** - Use the same emphasis patterns throughout your app
5. **Use semantic styles** - Prefer `titleMedium` over `bodyMedium.copy(fontWeight = Bold)`

## When to Use Each Approach

- **Font Weight**: Best for making specific content stand out (amounts, titles, important actions)
- **Opacity**: Best for de-emphasizing secondary information (timestamps, hints, metadata)
- **Natural Styles**: Best for semantic content (titles use title styles, labels use label styles)

## Integration in Your App

All these utilities are available in your components library:

```kotlin
import com.danhdue.uikit.ui.theme.BodyTextEmphasis
import com.danhdue.uikit.ui.theme.TextEmphasis
import com.danhdue.uikit.ui.theme.withEmphasis
import com.danhdue.uikit.ui.theme.withFontWeight
```

Start using them in any of your feature modules!
