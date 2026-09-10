---
id: "task_13_konsist_k10"
status: "done"
priority: "medium"
assignee: null
epic: "deeplink_router_engine"
dueDate: null
created: "2026-09-09T20:50:45Z"
modified: "2026-09-10T09:19:30Z"
completedAt: "2026-09-10T09:19:30Z"
labels: ["governance", "konsist"]
order: "a13"
---

# Task 13: Konsist Rule K10

Epic: [deeplink_router_engine](../epic/deeplink_router_engine/deeplink_router_engine.en.md)

## Requirement Analysis

Add **K10** to the existing K1–K9 family:

> A class implementing `DeepLinkResolver` must be named `*DeepLinkResolver` and must live in a `..presentation.di..` package.

This continues the K5 naming family and gives resolvers a predictable home: the Mason brick (Task 14) generates into that location, and a reviewer knows where to look for a feature's URL surface.

### What K10 deliberately does not check

**Pattern collision between features is not a rule, because it is structurally impossible.** Tier 1 routes by `feature` key, keys are unique (asserted by the Task 2 unit test), so two features cannot claim the same URI. Only intra-feature collisions remain, and those are opaque lambda bodies — undetectable by AST analysis and owned by the team that wrote them.

Do not attempt a Konsist rule for collisions. Record the reason in the rule's KDoc so a future maintainer does not try and fail.

The other three contract checks in this epic are **unit tests, not Konsist rules**, and that is deliberate: they assert runtime *values* (key uniqueness, route provenance, tab-index validity), not source *structure*. Forcing them into Konsist would be brittle and hard to read.

## Relevant Files & Context Pointers

- `konsist-test/src/test/kotlin/com/danhdue/konsist/NamingRulesTest.kt` — **modify**; K5 lives here and K10 is a naming rule
- `konsist-test/src/test/kotlin/com/danhdue/konsist/support/ArchScope.kt` — read; how scopes are built
- `konsist-test/src/test/kotlin/com/danhdue/konsist/support/Assertions.kt` — read; the shared assertion helper with `ruleId`
- `konsist-test/src/test/kotlin/com/danhdue/konsist/support/Baseline.kt` — read; baseline mechanics
- `konsist-test/konsist_baseline.txt` — **read; expected to stay empty**
- `konsist-test/src/test/kotlin/com/danhdue/konsist/BaselineParserTest.kt` — **modify**; the `enforced` set currently lists K1–K9 and must gain K10
- `features/settings/.../di/SettingsDeepLinkResolver.kt` and `features/scanner/.../di/ScannerDeepLinkResolver.kt` — the two classes the rule governs

## Design Rationale

- **K10 belongs in `NamingRulesTest`** beside K5, not in a new file. It is the same kind of rule with the same failure mode, and the existing file already carries the naming conventions a reviewer expects to find together.
- **Enforce immediately, with no baseline entry.** The baseline is currently empty and both resolvers are written to comply by construction (Tasks 11 and 12 place them correctly). Adding a baseline entry for a rule that passes clean would be dead weight — and the predecessor epic worked hard to empty that file.
- **Scanner is a dynamic-feature module**, so verify the Konsist scope actually includes it. `HostRulesTest` K8 already reads `android.dynamicFeatures` to exempt that module; check whether the same scope construction covers `features/scanner` sources for K10, and fix the scope rather than exempting the module if it does not — the DFM resolver should be held to the same naming rule.
- **Update `BaselineParserTest`'s enforced set.** That test asserts the baseline only references enforced rules; leaving K10 out of the set would make a future K10 baseline entry silently invalid.

Applicable skills: `.agents/skills/test-driven-development`; `.agents/skills/quality_check` at the end.

## TDD Checklist

- [x] **RED**: add the K10 test to `NamingRulesTest` and prove it can fail — temporarily rename one resolver (or add a throwaway `FooResolver` in the wrong package), confirm the rule reports it with `ruleId = "K10"`, then revert the deliberate violation.
- [x] **RED**: extend `BaselineParserTest`'s enforced-rules assertion to include `K10`; watch it fail before the rule exists.
- [x] **GREEN**: implement K10 using the existing assertion helper so its failure message matches the house format.
- [x] **Verify scope**: confirm the rule actually sees `features/scanner` sources. If the scope excludes dynamic-feature modules, widen the scope; do not exempt the module.
- [x] **REFACTOR**: KDoc the rule — what it enforces, and **why collision detection is deliberately absent** (structurally impossible; see HLD §4.1).

## Definition of Done

- [x] `./gradlew :konsist-test:test` green with K10 enforced.
- [x] `konsist_baseline.txt` remains **empty** — no new entries.
- [x] `BaselineParserTest` enforced set is `K1`–`K10`.
- [x] The rule was demonstrated to fail on a deliberate violation before being left green.
- [x] K10's scope includes the dynamic-feature module.
- [x] The KDoc records why collision detection is out of scope.
- [x] `./gradlew detekt spotlessCheck assembleDebug` green.

## Dependencies & Blockers

- Blocked by [Task 11](task_11_settings_resolver.md) and [Task 12](task_12_scanner_dfm_resolver.md) — the rule needs conforming classes to pass against.
- Blocks [Task 14](task_14_mason_brick_deeplink.md) — the brick must generate into the location K10 requires.

## References & Rollback

- Source spec §9.1 — [2026-09-10-deeplink-router-engine-design.md](../epic/deeplink_router_engine/2026-09-10-deeplink-router-engine-design.md)
- Predecessor epic design §6.1 — the K1–K9 table this extends
- **Rollback**: delete the K10 test and remove it from the enforced set. No production code depends on the rule.
