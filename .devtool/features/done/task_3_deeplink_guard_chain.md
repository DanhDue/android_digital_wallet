---
id: "task_3_deeplink_guard_chain"
status: "done"
priority: "high"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-10T04:18:40+07:00"
completedAt: "2026-09-10T04:18:40+07:00"
labels: ["architecture", "feature", "platform", "security"]
order: "a3"
---

# Task 3: Guard Chain, Pending Store, Auth Guard

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

Build the interception layer that runs between "we know where this link points" and "navigate there".

```kotlin
interface DeepLinkGuard {
    val order: Int get() = 0
    suspend fun check(link: DeepLink, target: DeepLinkTarget): GuardVerdict
}

sealed interface GuardVerdict {
    data object Allow : GuardVerdict
    data class Redirect(val to: String) : GuardVerdict
    data class Block(val reason: String) : GuardVerdict
}

interface PendingDeepLinkStore {
    fun put(link: String)
    fun takeIfAny(): String?
}
```

Ship exactly **one** guard as the worked example: `AuthDeepLinkGuard` (`order = 0`) — reads sign-in state from `SessionManager`; if `target.requiresAuth` and the user is signed out, stores the link and returns `Redirect(loginUri)`.

**Guards must be idempotent.** The router calls the chain twice for links into an auth-gated feature: once as a pre-gate before downloading a split, once fully after resolution (source spec §6.3). This constraint is why it is stated in the interface KDoc and tested here, not discovered later in Task 4.

**`PendingDeepLinkStore` is in-memory with a 10-minute TTL and take-once semantics.** Deliberately not persisted: a deeplink written to disk can fire days later in an unrelated context, which is a security smell rather than a feature.

## Relevant Files & Context Pointers

- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/DeepLinkGuard.kt` — **new** (guard + verdict)
- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/PendingDeepLinkStore.kt` — **new** (interface + `InMemoryPendingDeepLinkStore`, `internal`)
- `packages/platform/src/main/kotlin/com/danhdue/platform/deeplink/AuthDeepLinkGuard.kt` — **new**
- `packages/platform/src/main/kotlin/com/danhdue/platform/di/PlatformModule.kt` — **modify**, provide store and the guard `@IntoSet`
- `packages/core/src/main/kotlin/com/danhdue/core/session/SessionManager.kt` — read; source of sign-in state and the existing `logoutEvent`
- `packages/platform/src/test/kotlin/com/danhdue/platform/deeplink/AuthDeepLinkGuardTest.kt` — **new**
- `packages/platform/src/test/kotlin/com/danhdue/platform/deeplink/PendingDeepLinkStoreTest.kt` — **new**
- `packages/platform/src/test/kotlin/com/danhdue/platform/theme/AppThemeManagerTest.kt` — read for the module's existing test style and fake conventions

## Design Rationale

- **`order: Int` with a default.** Hilt `@IntoSet` produces an unordered `Set`; without an explicit sort key, guard evaluation order would vary between builds and produce non-reproducible behaviour. Sorting by `order` makes the chain deterministic.
- **`Redirect` carries a `String` URI, not a `DeepLink`.** The redirect target re-enters the pipeline through `dispatch(uri)`, so it must be expressible in the same vocabulary as any other source. Passing a parsed `DeepLink` would let a guard hand back something the parser would never have produced.
- **Interfaces with `internal` implementations.** `DeepLinkGuard` and `PendingDeepLinkStore` are interfaces; `InMemoryPendingDeepLinkStore` is `internal`. This is the deliberate correction of assessment finding 3.2 — `AppEventBus` and `Navigator` are concrete classes today, so the host currently pushes implementations down to features. Every new seam does it correctly.
- **TTL is checked on read, not by a timer.** No background coroutine, no cleanup scheduling — `takeIfAny()` compares the stored timestamp and discards a stale entry. Fewer moving parts and trivially testable with an injected clock.
- **Clock is injected** (a `() -> Long` or equivalent) so the TTL test does not sleep.

Applicable skills: `.agents/skills/test-driven-development`; `.agents/skills/quality_check` at the end per `CRITICAL_RULES.md`.

## TDD Checklist

- [ ] **RED**: `PendingDeepLinkStoreTest`, failing first:
  - `put` then `takeIfAny` returns the link
  - a second `takeIfAny` returns `null` (take-once)
  - `takeIfAny` returns `null` past the 10-minute TTL, with an injected clock
  - `put` overwrites an existing pending link
- [ ] **RED**: `AuthDeepLinkGuardTest`, failing first:
  - `requiresAuth = false` → `Allow`, regardless of sign-in state
  - `requiresAuth = true` + signed in → `Allow`; nothing is stored
  - `requiresAuth = true` + signed out → `Redirect(login)` and the link is stored
  - **idempotency**: calling `check` twice with identical arguments yields the same verdict and leaves exactly one pending link, not two
- [ ] **GREEN**: Implement store, guard, and verdict types; wire both into `PlatformModule`.
- [ ] **REFACTOR**: KDoc `DeepLinkGuard` with the idempotency requirement stated as a contract obligation and the reason (called twice, per §6.3). KDoc the store with the reason it is not persisted.

## Definition of Done

- [ ] `./gradlew :packages:platform:testDebugUnitTest` green, including the idempotency test.
- [ ] `DeepLinkGuard` KDoc states the idempotency requirement explicitly.
- [ ] `PendingDeepLinkStore` KDoc states why it is in-memory (security rationale).
- [ ] Only `AuthDeepLinkGuard` ships — no speculative feature-flag or KYC guard (see epic Non-Goals).
- [ ] `./gradlew detekt spotlessCheck assembleDebug` green.

## Dependencies & Blockers

- Blocked by [Task 2](task_2_deeplink_contract.md) — needs `DeepLinkTarget` and `DeepLink`.
- Blocks [Task 4](task_4_deeplink_router_pipeline.md).

## References & Rollback

- Source spec §5.4, §5.5 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- Epic HLD §4.6 (attack surface) — the security rationale behind the non-persisted store
- **Rollback**: remove the `@IntoSet` guard binding and the store provider from `PlatformModule`, then delete the three new files. Nothing consumes the chain until Task 4.
