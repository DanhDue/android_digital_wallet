# Flipper Integration Guide

This document provides a comprehensive guide to the Facebook Flipper integration in this project. It covers setup, available plugins, and architectural details.

## Quick Setup

1.  **Download Flipper**: Get the desktop app from [fbflipper.com](https://fbflipper.com/).
2.  **Run Debug Build**: Build and install the app using the `debug` variant.
    ```bash
    ./gradlew assembleDebug
    ```
3.  **Connect**: Open Flipper Desktop. It should automatically detect your running emulator/device.
4.  **Enable Plugins**: In the Flipper sidebar, ensure "Network", "Navigation", "Inspector", and "Shared Preferences" are enabled.

> [!NOTE]
> Flipper is **disabled** in Release builds. The integration uses reflection to ensure no Flipper code or dependencies are included in the final production APK.

---

## Available Plugins

### 1. Network Inspector
View all HTTP requests, responses, headers, and JSON payloads in real-time.
- **Implementation**: Uses a custom OkHttp Interceptor (`FlipperNetworkObject`).
- **Usage**: precise request/response timing and payload inspection.

### 2. Navigation Tracking
Real-time tracking of screen transitions and backstack changes.
- **Features**: Tracks both **Root** (full screen) and **Nested** (tabs) navigation.
- **Implementation**: Uses `snapshotFlow` to observe Navigation 3 backstacks.

**Setup for New Screens:**
To track a new Navigation 3 backstack, add the observer composable next to your `NavDisplay`:

```kotlin
// For Root Navigation
ObserveBackstackForFlipper(backStack = navigator.backStack, prefix = "Root")

// For Tab/Nested Navigation
ObserveBackstackForFlipper(backStack = tabBackStack, prefix = "TabName")
```

**Manual Logging:**
For events that aren't in a backstack (like clicking a bottom tab that simply switches visibility), you can log manually:

```kotlin
// Example: Log a bottom tab click
FlipperNavigationObject.sendNavigation("Tab/Wallet")
```

### 3. Layout Inspector
Inspect the view hierarchy, including Jetpack Compose nodes.
- **Usage**: Hover over elements in the app to see them highlighted in Flipper. View accessibility properties, sizes, and margins.

### 4. LeakCanary
Detect and view memory leaks directly within Flipper.
- **Integration**: While LeakCanary runs on the device, this plugin mirrors the leak traces to the desktop for easier analysis.

### 5. Shared Preferences
View and edit SharedPreferences XML files live.
- **Usage**: Change boolean flags or clear data without clearing app storage.

---

## Architecture: Release-Safe Integration

To keep our Release builds clean and small, we use a **Reflection-Based Proxy Pattern**.

### Core Components

1.  **`FlipperInitializer.kt`**
    - The central entry point.
    - Implements `AppInitializer`.
    - Uses `Class.forName()` to load Flipper classes.
    - **Safety**: If Flipper classes are missing (Release build), it catches exceptions and does nothing.

2.  **`FlipperNetworkObject.kt` & `FlipperNavigationObject.kt`**
    - Singleton objects that hold references to Flipper plugins.
    - They act as **Bridges**: The app calls them, and they forward calls to Flipper via reflection.
    - If Flipper is not initialized, these calls are safe no-ops.

3.  **`FlipperBackstackObserver.kt`**
    - A Composable that bridges the reactive Compose world (Navigation 3) with the imperative Flipper world.
    - Observations are only active when the Composable is in the composition tree.

86: ---
87: 
88: ## Technical Detail: Event Buffering
89: 
90: To resolve race conditions where navigation events (like the initial route) occur before Flipper is fully connected:
91: 
92: 1.  **Event Buffering**: `FlipperNavigationObject` queues events if the plugin is not yet set.
93: 2.  **Connection-Aware Flushing**: `FlipperInitializer` uses a `FlipperStateUpdateListener` to wait for the **CONNECTED** state before setting the plugin.
94: 3.  **Result**: Buffered events are flushed immediately upon connection, ensuring no initial events are lost.
95: 
96: ---

## Important Notes & Best Practices

> [!IMPORTANT]
> **Bottom Navigation / Tabs**
> When integrating navigation tracking for tabs, always pass a unique `prefix` (e.g., `Tab/Wallet`) to `ObserveBackstackForFlipper`. This ensures you can distinguish between different tab stacks in the Flipper log.

> [!TIP]
> **Filtering Events**
> In Flipper's Navigation plugin, use the filter bar.
> - Search "**Root**" to see top-level flows.
> - Search "**Tab**" to inspect tab switching behavior.

> [!WARNING]
> **CPU Profiler**
> Flipper does not have a native CPU profiler. For CPU/Memory profiling, use the **Android Studio Profiler** (View -> Tool Windows -> Profiler).
