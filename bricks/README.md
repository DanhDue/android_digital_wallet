# Mason Bricks

This directory contains Mason bricks for code generation following the project's Clean Architecture + MVI pattern.

## Available Bricks

| Brick | Description |
|-------|-------------|
| [`mvi_feature`](./mvi_feature/README.md) | Generates complete Android module in `features/` folder |
| [`mvi_subfeature`](./mvi_subfeature/README.md) | Generates MVI screen files within a feature module |
| [`remove_feature`](./remove_feature/README.md) | Removes a feature module and all its configurations |
| [`remove_subfeature`](./remove_subfeature/README.md) | Removes a subfeature screen from a module |

## Setup

### Install Mason CLI

```bash
dart pub global activate mason_cli
```

### Add Bricks Locally

```bash
mason add mvi_feature --path bricks/mvi_feature
mason add mvi_subfeature --path bricks/mvi_subfeature
mason add remove_feature --path bricks/remove_feature
mason add remove_subfeature --path bricks/remove_subfeature
```

## Quick Start

### Create a Feature Module

```bash
mason make mvi_feature --name Payment --package com.danhdue.payment --screen Home
```

### Add a Screen to Existing Module

```bash
mason make mvi_subfeature --module payment --name Detail --package com.danhdue.payment
```

### Remove a Screen

```bash
mason make remove_subfeature --module payment --name Detail
```

### Remove a Feature Module

```bash
mason make remove_feature --name Payment
```

## Deep Links Integration

Newly generated features automatically wire deep link support:
- Deep Link scheme: `myapp://<feature_name>`
- Auto-registers `*DeepLinkResolver` in `presentation/di/` (conforming to Konsist rule K10).
- Auto-registers a `FeatureEntryPoint` in `packages/platform/.../AppDeepLinks.kt` with `tab = null` (`RootFullScreen`).
- If a feature is placed in a bottom navigation tab, update `tab = <tabIndex>` on its `FeatureEntryPoint` entry in `AppDeepLinks.kt`.
- Removing a feature via `mason make remove_feature` cleanly unwires `AppDeepLinks.kt` and `AppRoutes.kt`.

## Architecture Reference

See [ARCHITECTURE.md](../ARCHITECTURE.md) for details on the Clean Architecture + MVI pattern.

