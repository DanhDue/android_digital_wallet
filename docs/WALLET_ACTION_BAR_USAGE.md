# WalletActionBar Usage Guide

## Overview

The `WalletActionBar` is a composable component that displays 4 action buttons for wallet operations: Send, Receive, Buy, and Staking.

## Preview

The component displays 4 circular buttons with icons and labels arranged horizontally:

- **Send**: Arrow pointing up-right
- **Receive**: Arrow pointing down-left
- **Buy**: Credit card icon
- **Staking**: Stacked cards icon

## Usage

### Basic Usage

```kotlin
import com.danhdue.components.ui.widgets.WalletActionBar

@Composable
fun MyScreen() {
    WalletActionBar()
}
```

### With Click Handlers

```kotlin
WalletActionBar(
    onSendClick = {
        // Handle send action
        navigateToSendScreen()
    },
    onReceiveClick = {
        // Handle receive action
        navigateToReceiveScreen()
    },
    onBuyClick = {
        // Handle buy action
        navigateToBuyScreen()
    },
    onStakingClick = {
        // Handle staking action
        navigateToStakingScreen()
    }
)
```

### With Custom Modifier

```kotlin
WalletActionBar(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 24.dp),
    onSendClick = { /* ... */ },
    onReceiveClick = { /* ... */ },
    onBuyClick = { /* ... */ },
    onStakingClick = { /* ... */ }
)
```

## Resources Used

### Drawable Resources

- `R.drawable.ic_send` - Send button icon
- `R.drawable.ic_receive` - Receive button icon
- `R.drawable.ic_buy` - Buy button icon
- `R.drawable.ic_staking` - Staking button icon

### String Resources

- `R.string.wallet_action_send` - "Send"
- `R.string.wallet_action_receive` - "Receive"
- `R.string.wallet_action_buy` - "Buy"
- `R.string.wallet_action_staking` - "Staking"

## Design Specifications

### Button Style

- **Size**: 56dp circular background
- **Icon Padding**: 12dp inside circle
- **Background Color**: `#F5F7FA` (light gray-blue)
- **Spacing**: 8dp between icon and label

### Typography

- **Font**: Material Typography `bodyMedium`
- **Size**: 14sp
- **Weight**: Medium
- **Color**: `onBackground` from theme

### Layout

- **Arrangement**: Evenly spaced across width
- **Alignment**: Center vertically

## Customization

The component uses Material 3 theming, so it will automatically adapt to:

- Your app's color scheme (light/dark theme)
- Typography settings (Raleway fonts in this project)
- Content color variations

## Example in Context

```kotlin
@Composable
fun WalletHomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Wallet card
        WalletCard(...)

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        WalletActionBar(
            onSendClick = { viewModel.navigateToSend() },
            onReceiveClick = { viewModel.navigateToReceive() },
            onBuyClick = { viewModel.navigateToBuy() },
            onStakingClick = { viewModel.navigateToStaking() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Transaction list
        TransactionList(...)
    }
}
```

## Accessibility

All buttons include proper content descriptions for screen readers, using the string resources as labels.
