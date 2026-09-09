---
id: "task_1_deeplink_parser"
status: "todo"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-09T20:50:45Z"
completedAt: null
labels: ["architecture", "feature", "platform"]
order: "a1"
---

# Task 1: DeepLink Model and Parser

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

The engine accepts links from four sources that arrive in two different URI shapes:

- custom scheme — `myapp://settings/profile?ref=push`
- App Links — `https://app.example.com/settings/profile?ref=push`

Every downstream component (tier-1 lookup, resolvers, guards) must see **one** normalised shape, never the raw difference. This task builds that normalisation and nothing else.

Normalisation rules:

| Input shape | `feature` | `segments` |
|---|---|---|
| `myapp://settings/profile` | authority → `"settings"` | path → `["profile"]` |
| `https://host/settings/profile` | first path segment → `"settings"` | remainder → `["profile"]` |

The parser must be **pure JVM** — no `android.net.Uri`, no Robolectric — so that it and every test above it run in a plain unit-test source set. This is a hard requirement, not a preference: it is what keeps criterion 4.1 (sandbox development) intact for every feature that later writes resolver tests.

## Relevant Files & Context Pointers

- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/DeepLink.kt` — **new**
- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/DeepLinkParser.kt` — **new**
- `packages/platform/src/test/kotlin/com/danhdue/platform/deeplink/DeepLinkParserTest.kt` — **new**
- `packages/platform/build.gradle.kts` — verify no new dependency is needed (`java.net.URI` is stdlib)
- `packages/platform/src/main/kotlin/com/danhdue/platform/AppRoutes.kt` — read for the house KDoc/style conventions in this module

## Design Rationale

- **`java.net.URI` over `android.net.Uri`.** `android.net.Uri` is stubbed in unit tests and returns null for everything unless Robolectric is added. `java.net.URI` parses `myapp://settings/profile?x=1` correctly (scheme / authority / path / query) and runs anywhere.
- **`data class` with a `raw` field.** `raw` is kept so a link can be stored in `PendingDeepLinkStore` and replayed verbatim after login, and so failure logs name the exact input.
- **Query parsing is hand-rolled, not delegated.** `URI.query` gives a raw string; splitting on `&` / `=` with URL-decoding is a few lines and avoids pulling in a dependency for it.
- **Parser returns `DeepLink?`, never throws.** A malformed URI is an expected steady state (an exported `intent-filter` accepts arbitrary input from any app), not an exceptional one. The router maps `null` to `FailureReason.Malformed`.

Applicable skill: `.agents/skills/test-driven-development` — this task is textbook TDD, the parser is pure and total. Run `.agents/skills/quality_check` at the end, per `CRITICAL_RULES.md`.

## TDD Checklist

- [ ] **RED**: Write `DeepLinkParserTest` covering, all failing first:
  - custom scheme with and without path, with and without query
  - App Links form; verify the domain is dropped and the first path segment becomes `feature`
  - percent-encoded params (`%20`, `%2F`) decode correctly
  - unicode in params
  - trailing slash (`myapp://settings/`) yields the same result as no slash
  - uppercase scheme (`MYAPP://`) is accepted (schemes are case-insensitive per RFC 3986)
  - empty path (`myapp://settings`) yields `segments == emptyList()`
  - malformed input returns `null`: empty string, `"not a uri"`, scheme-only (`"myapp://"`), `https://host` with no path segment
  - duplicate query keys — assert the documented winner (last wins)
- [ ] **GREEN**: Implement `DeepLink` and `DeepLinkParser` to pass.
- [ ] **REFACTOR**: Extract the query-decoding helper; make sure KDoc states the normalisation table and the last-wins rule explicitly.

## Definition of Done

- [ ] `./gradlew :packages:platform:testDebugUnitTest` green.
- [ ] Zero references to `android.*` or `androidx.*` in both new main-source files (this is also what Konsist K3 enforces for domain code; hold the same bar here by choice).
- [ ] `./gradlew detekt spotlessCheck` clean.
- [ ] Public API carries KDoc, including the normalisation table and the last-wins duplicate-key rule.
- [ ] `./gradlew assembleDebug` green — app behaviour unchanged (nothing calls this yet).

## Dependencies & Blockers

- Blocks [Task 2](task_2_deeplink_contract.md), [Task 4](task_4_deeplink_router_pipeline.md).
- Blocked by: nothing. This is the first task of the epic.

## References & Rollback

- Source spec §5.1 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- RFC 3986 §3.1 (scheme case-insensitivity)
- **Rollback**: delete the `deeplink/` package directory and its test. Nothing else references it at this point, so removal is total and risk-free.
