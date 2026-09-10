# Architecture Documentation / Tài Liệu Kiến Trúc

> 🌐 **Language Selection / Chọn ngôn ngữ:**
>
> - 🇬🇧 **[English Architecture Guide (ARCHITECTURE.en.md)](ARCHITECTURE.en.md)**  
>   Authoritative architecture specification covering Clean Architecture + MVI, Super App Governance (4 Pillars / 8 Criteria), Sandbox Development (`commons.android-sample`), and Binary Compatibility Validator (BCV 0.17.0).
>
> - 🇻🇳 **[Tài Liệu Kiến Trúc Tiếng Việt (ARCHITECTURE.vi.md)](ARCHITECTURE.vi.md)**  
>   Tài liệu kiến trúc chuẩn mực toàn diện về Clean Architecture + MVI, Quản trị Super App (4 Trụ cột / 8 Tiêu chí), Phát triển Sandbox độc lập (`commons.android-sample`), và Quản trị Hợp đồng ABI (BCV 0.17.0).

---

## Quick Navigation / Điều Hướng Nhanh

| Section / Mục | English Version | Phiên bản Tiếng Việt |
|---|---|---|
| **I. Clean Architecture + MVI** | [English (Part I)](ARCHITECTURE.en.md#i-clean-architecture--mvi-diagram) | [Tiếng Việt (Phần I)](ARCHITECTURE.vi.md#i-sơ-đồ-clean-architecture--mvi) |
| **II. MVI Mechanism** | [English (Part II)](ARCHITECTURE.en.md#ii-mvi-mechanism) | [Tiếng Việt (Phần II)](ARCHITECTURE.vi.md#ii-cơ-chế-mvi) |
| **III. Feature-First Organization** | [English (Part III)](ARCHITECTURE.en.md#iii-feature-first-organization--architecture-layers) | [Tiếng Việt (Phần III)](ARCHITECTURE.vi.md#iii-tổ-chức-feature-first--các-layer-kiến-trúc) |
| **• DeepLink Router Engine** | [English (§4.1)](ARCHITECTURE.en.md#41-deeplink-router-engine) | [Tiếng Việt (§4.1)](ARCHITECTURE.vi.md#41-deeplink-router-engine) |
| **• Super App Governance (4 Pillars / 8 Criteria)** | [English (§4.2)](ARCHITECTURE.en.md#42-super-app-governance-4-core-pillars--8-criteria) | [Tiếng Việt (§4.2)](ARCHITECTURE.vi.md#42-super-app-governance-4-trụ-cột--8-tiêu-chí) |
| **• Sandbox Development (:sample runners)** | [English (§4.3)](ARCHITECTURE.en.md#43-sandbox-development-standalone-mini-app-runners) | [Tiếng Việt (§4.3)](ARCHITECTURE.vi.md#43-sandbox-development-standalone-mini-app-runners) |
| **• BCV Contract Governance** | [English (§4.4)](ARCHITECTURE.en.md#44-binary-compatibility-validator-bcv--abi-contract-governance) | [Tiếng Việt (§4.4)](ARCHITECTURE.vi.md#44-binary-compatibility-validator-bcv--quản-trị-hợp-đồng-abi) |
| **• Konsist Rules Gate (K1–K10)** | [English (§4.4 Konsist)](ARCHITECTURE.en.md#44-binary-compatibility-validator-bcv--abi-contract-governance) | [Tiếng Việt (§4.4 Konsist)](ARCHITECTURE.vi.md#44-binary-compatibility-validator-bcv--quản-trị-hợp-đồng-abi) |
| **IV. Modern Android Stack** | [English (Part IV)](ARCHITECTURE.en.md#iv-modern-android-stack) | [Tiếng Việt (Phần IV)](ARCHITECTURE.vi.md#iv-stack-công-nghệ-android-hiện-đại) |
| **V. Code Examples & Best Practices** | [English (Part V)](ARCHITECTURE.en.md#v-code-examples--best-practices) | [Tiếng Việt (Phần V)](ARCHITECTURE.vi.md#v-ví-dụ-code--best-practices) |
| **VI. References** | [English (Part VI)](ARCHITECTURE.en.md#vi-references) | [Tiếng Việt (Phần VI)](ARCHITECTURE.vi.md#vi-tài-liệu-tham-khảo) |
| **VII. Summary & Commands** | [English (Part VII)](ARCHITECTURE.en.md#vii-summary) | [Tiếng Việt (Phần VII)](ARCHITECTURE.vi.md#vii-tổng-kết) |
