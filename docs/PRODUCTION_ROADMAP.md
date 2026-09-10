# Lộ Trình Nâng Cấp Thực Chiến & Chiến Lược Quản Lý Bộ Nhớ
## (Production Readiness Roadmap & Memory Management Strategy)

> **Mục đích tài liệu:** Lưu trữ toàn bộ các phân tích, đề xuất kỹ thuật nâng cao và chiến lược quản lý bộ nhớ để phát triển ứng dụng cấp độ Enterprise / Fintech trong tương lai, trong khi vẫn giữ template ở trạng thái tối giản, sạch sẽ và nhẹ nhất ở hiện tại (Lean Template Core).

---

## Mục lục

- [I. Triết Lý Thiết Kế: Core Tinh Gọn (Lean Core)](#i-triết-lý-thiết-kế-core-tinh-gọn-lean-core)
- [II. Chiến Lược Quản Lý Bộ Nhớ Toàn Diện (Memory Management Strategy)](#ii-chiến-lược-quản-lý-bộ-nhớ-toàn-diện-memory-management-strategy)
  - [1. Phân cấp Scope & Vòng đời đối tượng (Scope Hygiene)](#1-phân-cấp-scope--vòng-đời-đối-tượng-scope-hygiene)
  - [2. Quản lý bộ nhớ trong Jetpack Compose](#2-quản-lý-bộ-nhớ-trong-jetpack-compose)
  - [3. Chiến lược nạp & giải phóng hình ảnh (Coil Image Caching)](#3-chiến-lược-nạp--giải-phóng-hình-ảnh-coil-image-caching)
  - [4. Ứng phó áp lực bộ nhớ hệ điều hành (OnTrimMemory & Low Memory)](#4-ứng-phó-áp-lực-bộ-nhớ-hệ-điều-hành-ontrimmemory--low-memory)
  - [5. Tối ưu dữ liệu lớn: Pagination & JSON Streaming](#5-tối-ưu-dữ-liệu-lớn-pagination--json-streaming)
  - [6. Giám sát & Phát hiện rò rỉ bộ nhớ (Leak Detection)](#6-giám-sát--phát-hiện-rò-rỉ-bộ-nhớ-leak-detection)
- [III. Chiến Lược Quản Lý Quyền Chuẩn Mực (Permission Management Strategy)](#iii-chiến-lược-quản-lý-quyền-chuẩn-mực-permission-management-strategy)
  - [1. Phân loại quyền trên Android hiện đại (Android 13–15+)](#1-phân-loại-quyền-trên-android-hiện-đại-android-1315)
  - [2. Ranh giới Clean Architecture & MVI đối với Quyền](#2-ranh-giới-clean-architecture--mvi-đối-với-quyền)
  - [3. Luồng UX: Rationale, Vĩnh Viễn Từ Chối & Suy Thoái Mềm (Graceful Degradation)](#3-luồng-ux-rationale-vĩnh-viễn-từ-chối--suy-thoái-mềm-graceful-degradation)
  - [4. Quản lý quyền trong Super App & Dynamic Feature Modules (DFM)](#4-quản-lý-quyền-trong-super-app--dynamic-feature-modules-dfm)
  - [5. Giải pháp thay thế không cần quyền (Permissionless Alternatives)](#5-giải-pháp-thay-thế-không-cần-quyền-permissionless-alternatives)
- [IV. Ba Bài Toán Sống Còn Cho Siêu Ứng Dụng (Super App Runtime Resilience)](#iv-ba-bài-toán-sống-còn-cho-siêu-ứng-dụng-super-app-runtime-resilience)
  - [1. Quản trị tài nguyên & Vòng đời bộ nhớ: Giải phóng cưỡng bức (Force Eviction / LRU State Hibernation)](#1-quản-trị-tài-nguyên--vòng-đời-bộ-nhớ-giải-phóng-cưỡng-bức-force-eviction--lru-state-hibernation)
  - [2. Bảo mật ranh giới giữa các Mini App: Crash Isolation (Error Boundary) & Permission Gatekeeper](#2-bảo-mật-ranh-giới-giữa-các-mini-app-crash-isolation-error-boundary--permission-gatekeeper)
  - [3. Chiến lược phát hành & Giám sát độc lập: Remote Config, Router Kill-Switch & DFM Dynamic Delivery](#3-chiến-lược-phát-hành--giám-sát-độc-lập-remote-config-router-kill-switch--dfm-dynamic-delivery)
- [V. Danh Mục Nâng Cấp Chiến Lược (Future Enhancements Backlog)](#v-danh-mục-nâng-cấp-chiến-lược-future-enhancements-backlog)
  - [1. Bảo Mật & Fintech Defense (Security Hardening)](#1-bảo-mật--fintech-defense-security-hardening)
  - [2. Khả Năng Chịu Tải & Offline-First (Resilience & Caching)](#2-khả-năng-chịu-tải--offline-first-resilience--caching)
  - [3. Design System & Micro-Interactions (UI/UX Kit)](#3-design-system--micro-interactions-uiux-kit)
  - [4. DevOps, CI/CD & Build Variants (Release Automation)](#4-devops-cicd--build-variants-release-automation)
  - [5. Khả Năng Quan Sát & Telemetry (Observability)](#5-khả-năng-quan-sát--telemetry-observability)
  - [6. Khôi Phục Trạng Thái khi Process Death (State Restoration)](#6-khôi-phục-trạng-thái-khi-process-death-state-restoration)
- [VI. Ma Trận Phân Kỳ Triển Khai (Prioritized Execution Matrix)](#vi-ma-trận-phân-kỳ-triển-khai-prioritized-execution-matrix)
- [VII. Tổng Kết & Đề Xuất Lộ Trình Hành Động (Summary & Next Steps Roadmap)](#vii-tổng-kết--đề-xuất-lộ-trình-hành-động-summary--next-steps-roadmap)
  - [1. Đề xuất nâng cấp cấu hình Template (Lean Mode vs Enterprise Mode)](#1-đề-xuất-nâng-cấp-cấu-hình-template-lean-mode-vs-enterprise-mode)
  - [2. Bảng lộ trình đề xuất bổ sung tiếp theo (Next Steps Roadmap)](#2-bảng-lộ-trình-đề-xuất-bổ-sung-tiếp-theo-next-steps-roadmap)

---

## I. Triết Lý Thiết Kế: Core Tinh Gọn (Lean Core)

Một template thành công phục vụ cho cả dự án quy mô nhỏ (Startup/MVP) lẫn dự án quy mô lớn (Enterprise Super App) cần tuân thủ triết lý:
1. **Khung xương sạch sẽ (Zero Bloat):** Không tích hợp sẵn các SDK cồng kềnh, nặng nề của bên thứ ba khi chưa có nhu cầu thực tế.
2. **Điểm mở rộng chuẩn mực (Well-defined Extension Seams):** Kiến trúc đã phân tách sẵn các interface và vị trí cắm ghép (Seams) tại `:packages:platform`, `:packages:core`, `:packages:network` để khi cần bổ sung tính năng, lập trình viên chỉ cần "plug-in" mà không phải tái cấu trúc hệ thống.
3. **Kích hoạt theo nhu cầu (On-demand Adoption):** Tham khảo danh mục tài liệu này để lựa chọn và áp dụng dần từng module theo yêu cầu thực tế của từng dự án.

---

## II. Chiến Lược Quản Lý Bộ Nhớ Toàn Diện (Memory Management Strategy)

Trong kiến trúc Super App và ứng dụng đa module với Jetpack Compose, quản lý bộ nhớ không chỉ đơn thuần là "tránh leak Activity", mà là quản lý vòng đời tài nguyên xuyên suốt qua các tầng:

### 1. Phân cấp Scope & Vòng đời đối tượng (Scope Hygiene)

| Scope | Vòng đời | Quy tắc áp dụng | Rủi ro nếu dùng sai |
|---|---|---|---|
| `@Singleton` | Toàn bộ tiến trình ứng dụng | **CHỈ** dành cho hạ tầng cấp thấp vô trạng thái hoặc cache ứng dụng cốt lõi: `OkHttpClient`, `RoomDatabase`, `DispatcherProvider`, `SessionManager`. | Rò rỉ bộ nhớ vĩnh viễn (Leak cả vòng đời app) nếu gán state của feature vào Singleton. |
| `@ActivityRetainedScoped` | Tồn tại qua cấu hình xoay màn hình, chết khi Activity kết thúc | Dành cho Navigation state cấp cao nhất (`Navigator`), các scoped coordinator. | Không lưu trữ View/Context tại đây. |
| `@ViewModelScoped` | Gắn liền với vòng đời của ViewModel và Backstack entry | **Mọi business state và data của Feature phải nằm ở đây.** Khi màn hình bị pop khỏi backstack, toàn bộ State/UseCase/Repository liên kết sẽ được Garbage Collector thu hồi. | Nếu giữ reference tới `Context` sẽ gây leak Activity. |

*   **Nguyên tắc Coroutine Cancellation:** Tuyệt đối không dùng `GlobalScope`. Toàn bộ coroutine của Mini App phải khởi chạy trong `viewModelScope`. Khi feature bị đóng, `viewModelScope` tự động cancel mọi job ngầm, giải phóng network request, DB query và giải phóng memory buffer.

### 2. Quản lý bộ nhớ trong Jetpack Compose

Compose vẽ lại UI bằng cơ chế Recomposition. Nếu không kiểm soát, quá trình cấp phát bộ nhớ (Allocation Churn) sẽ diễn ra liên tục gây tràn Garbage Collector (GC pauses) và làm rớt khung hình (Jank):

*   **Ổn định kiểu dữ liệu (@Immutable / @Stable):**
    *   Các class State UI (`*State`, `*UiModel`) phải là `data class` với các thuộc tính `val`.
    *   Tránh dùng `List<T>` thuần trong State mà không bọc bằng `ImmutableList` (hoặc `@Immutable`), vì Compose mặc định coi `List` là unstable, dẫn tới việc recompose toàn bộ màn hình dù dữ liệu không đổi.
*   **Lambda Optimization:**
    *   Dùng Method Reference (`viewModel::onAction`) hoặc lambda scoped để Compose ghi nhớ chữ ký, tránh cấp phát instance lambda mới ở mỗi frame recomposition.
*   **Thu gom tài nguyên LazyList:**
    *   Trong `LazyColumn` / `LazyRow`, luôn luôn chỉ định `key = { item.id }` và `contentType = { item.type }`.
    *   Điều này cho phép Compose tái sử dụng (recycle) các Node Composable tương tự nhau trong bộ nhớ thay vì hủy đi và tạo mới đối tượng Composable khi cuộn.
*   **Lắng nghe State an toàn:**
    *   Sử dụng `collectAsStateWithLifecycle()` thay vì `collectAsState()`. Khi ứng dụng xuống background, flow collection lập tức dừng lại, không giữ các buffer dữ liệu phát sinh liên tục trong RAM.

### 3. Chiến lược nạp & giải phóng hình ảnh (Coil Image Caching)

Hình ảnh và Bitmap là nguyên nhân số 1 gây lỗi Out-Of-Memory (OOM) trên Android:

*   **Giới hạn RAM Cache:** Cấu hình `ImageLoader` trong `:packages:ui_kit` với tỷ lệ trần bộ nhớ RAM:
    ```kotlin
    ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25) // Tối đa 25% heap memory khả dụng của app
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(100L * 1024 * 1024) // 100 MB disk
                .build()
        }
        .respectCacheHeaders(false)
        .build()
    ```
*   **Downsampling chuẩn kích thước View:** Không bao giờ nạp ảnh nguyên bản (`Size.ORIGINAL`) vào thẻ danh sách thumbnail. Luôn resize ảnh khớp với kích thước của Composable để tránh giữ bitmap 4K/8K trong bộ nhớ.
*   **Tự động tái chế Bitmap:** Sử dụng định dạng `Bitmap.Config.HARDWARE` trên Android 8.0+ để chuyển dữ liệu đồ họa vào bộ nhớ đồ họa Graphic Memory, giải phóng triệt để heap RAM của JVM.

### 4. Ứng phó áp lực bộ nhớ hệ điều hành (OnTrimMemory & Low Memory)

Hệ điều hành Android thông báo các mức áp lực bộ nhớ thông qua `ComponentCallbacks2.onTrimMemory(level)`. Một ứng dụng chuyên nghiệp phải biết tự dọn dẹp trước khi bị OS tiêu diệt (Low Memory Killer):

*   **Tạo `MemoryPressureMonitor` tại `:packages:core`:**
    ```kotlin
    interface MemoryPressureListener {
        fun onTrimMemory(level: Int)
        fun onLowMemory()
    }
    ```
*   **Chiến lược phản ứng theo mức cảnh báo:**
    *   `TRIM_MEMORY_UI_HIDDEN` (User bấm Home hoặc chuyển sang app khác):
        *   Lập tức xóa sạch cache ảnh trong RAM: `Coil.imageLoader(context).memoryCache?.clear()`.
        *   Dọn dẹp các cache tạm không cần thiết trong RAM.
    *   `TRIM_MEMORY_RUNNING_CRITICAL` / `TRIM_MEMORY_COMPLETE` (Hệ thống chuẩn bị kill process):
        *   Giải phóng toàn bộ cache dữ liệu trong bộ nhớ của các repository.
        *   Hủy các tiến trình pre-fetching ngầm.

### 5. Tối ưu dữ liệu lớn: Pagination & JSON Streaming

*   **Paging 3 cho danh sách lớn:** Không bao giờ nạp toàn bộ danh sách 1,000+ bản ghi từ cơ sở dữ liệu vào `List<Entity>` trong RAM. Bắt buộc dùng `PagingSource` / `PagingData` để nạp từng trang (Page Size: 20-50 items) và tự động giải phóng các trang đã cuộn qua xa.
*   **Moshi JSON Streaming:** Đối với các file dữ liệu JSON lớn (xuất báo cáo, danh sách giao dịch lịch sử), sử dụng `JsonReader` dạng streaming thay vì parse cả chuỗi JSON khổng lồ vào RAM.

### 6. Giám sát & Phát hiện rò rỉ bộ nhớ (Leak Detection)

*   **LeakCanary trong Debug Builds:**
    *   Tích hợp `com.squareup.leakcanary:leakcanary-android` trong `debugImplementation`.
    *   Tự động phát hiện và cảnh báo các retained references của Activity, Composable nodes, hoặc ViewModel.
*   **Android Studio Memory Profiler:**
    *   Quy trình kiểm tra định kỳ: Record Native Allocations & Java Heap Dump khi thực hiện luồng điều hướng: `Mở Feature -> Thao tác -> Nhấn Back ra ngoài`. Heap memory phải quay về mức bình thường (đường đồ thị phẳng).

---

## III. Chiến Lược Quản Lý Quyền Chuẩn Mực (Permission Management Strategy)

Trong kiến trúc Super App và Jetpack Compose hiện đại, việc quản lý quyền (Permission Management) cần tuân thủ nghiêm ngặt ranh giới Clean Architecture, đảm bảo trải nghiệm người dùng (UX) mượt mà và đối phó linh hoạt với các thay đổi chính sách bảo mật từ Android 13 đến Android 15+.

### 1. Phân loại quyền trên Android hiện đại (Android 13–15+)

| Nhóm quyền | Ví dụ tiêu biểu | Hành vi cấp quyền | Cơ chế xử lý |
|---|---|---|---|
| **Install-time Permissions** | `INTERNET`, `ACCESS_NETWORK_STATE` | Tự động cấp khi cài đặt app từ Play Store | Khai báo trong `AndroidManifest.xml`, không cần code runtime. |
| **Runtime Permissions (Thông thường)** | `CAMERA`, `RECORD_AUDIO`, `ACCESS_FINE_LOCATION` | Người dùng phải chủ động bấm "Allow" trên dialog hệ thống | Quản lý qua `rememberPermissionState` hoặc `PermissionHandler` trong `:packages:ui_kit`. |
| **Quyền đặc thù (Special Permissions)** | `POST_NOTIFICATIONS` (Android 13+), `SCHEDULE_EXACT_ALARM` (Android 12+), `ACCESS_BACKGROUND_LOCATION` | Bắt buộc luồng giải trình (Rationale) rõ ràng; có quyền phải dẫn vào Settings hệ thống | Cần có UI giải thích nghiệp vụ trước khi kích hoạt request. |
| **Quyền truy cập Media** | `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO` (Android 13+) | Thay thế `READ_EXTERNAL_STORAGE` cũ | **Ưu tiên sử dụng Photo Picker** để không cần xin bất kỳ quyền runtime nào! |

### 2. Ranh giới Clean Architecture & MVI đối với Quyền

*   **Layer Domain (Kotlin thuần túy):**
    *   **CẤM:** Không import `android.Manifest.permission.*` (vi phạm Konsist rule K3).
    *   **NÊN:** Domain chỉ quản lý logic nghiệp vụ trừu tượng, ví dụ trạng thái `CameraAccessStatus` hoặc kết quả `Result.failure(SecurityException)`.
*   **Layer Presentation (Compose & MVI):**
    *   Là nơi duy nhất tương tác với Android OS để request quyền.
    *   Sử dụng bộ công cụ sẵn có trong `:packages:ui_kit` (`PermissionHandler`, `PermissionState`).
    *   **MVI Loop:**
        1. User nhấn nút "Quét mã" -> Screen kích hoạt `permissionState.launchPermissionRequest()`.
        2. Nhận kết quả từ OS -> Screen phát Action: `viewModel.dispatch(ScannerAction.PermissionResult(isGranted))`.
        3. ViewModel cập nhật State: `reduce { copy(hasCameraPermission = action.isGranted) }`.
        4. Screen tái dựng (recompose) hiển thị CameraPreview hoặc Fallback UI.

### 3. Luồng UX: Rationale, Vĩnh Viễn Từ Chối & Suy Thoái Mềm (Graceful Degradation)

```text
[Người dùng chạm tính năng]
          ↓
[Kiểm tra hasPermission?] ──(Đã có)──> [Khởi chạy tính năng ngay]
          ↓ (Chưa có)
[Kiểm tra shouldShowRationale?]
    ├── (Đã từ chối 1 lần) ──> [Hiển thị Rationale Dialog giải thích lý do cần thiết]
    │                                  ├── (Đồng ý) ──> [Gọi System Permission Dialog]
    │                                  └── (Hủy)   ──> [Giữ nguyên màn hình / Báo lỗi nhẹ]
    └── (Lần đầu tiên)      ──> [Gọi System Permission Dialog]
          ↓
[Người dùng phản hồi]
    ├── (Cho phép)           ──> [Cập nhật State: Granted -> Chạy tính năng]
    └── (Từ chối vĩnh viễn)  ──> [Hiển thị Settings Redirect Dialog]
                                       ├── (Mở Cài đặt) ──> [Chuyển tới App Settings hệ thống]
                                       └── (Hủy)         ──> [Suy thoái mềm (Graceful Degradation)]
```

*   **Nguyên tắc Suy thoái mềm (Graceful Degradation):**
    *   Ứng dụng **tuyệt đối không bao giờ được crash** hoặc rơi vào trạng thái "màn hình đen" khi bị từ chối quyền.
    *   Cung cấp tính năng thay thế: Ví dụ nếu từ chối quyền Camera khi quét mã QR -> Cung cấp nút *"Tải ảnh QR từ thư viện"* hoặc *"Nhập mã giao dịch bằng tay"*.

### 4. Quản lý quyền trong Super App & Dynamic Feature Modules (DFM)

*   **Hợp nhất Manifest (Manifest Merging):** Quyền của một Mini App (ví dụ Camera của `:features:scanner`) được khai báo ngay trong `features/scanner/src/main/AndroidManifest.xml`. Khi đóng gói App Bundle, Google Play sẽ hợp nhất quyền này vào APK.
*   **Xin quyền đúng thời điểm (Just-in-Time Permissions):**
    *   Không xin toàn bộ quyền khi vừa mở Super App (gây khó chịu và làm giảm tỷ lệ chuyển đổi của user).
    *   Chỉ khi người dùng bước vào màn hình của Mini App cụ thể và thực hiện thao tác tương ứng thì mới kích hoạt luồng xin quyền.

### 5. Giải pháp thay thế không cần quyền (Permissionless Alternatives)

*   **Photo Picker (Android 13+ và backport tới Android 5.0 qua Google Play Services):**
    *   Để chọn ảnh đại diện hoặc chọn ảnh hóa đơn: Không dùng `READ_MEDIA_IMAGES` hay `READ_EXTERNAL_STORAGE`.
    *   Dùng `ActivityResultContracts.PickVisualMedia()`. Hệ thống sẽ mở bộ chọn ảnh bảo mật của Google, người dùng chọn ảnh nào thì app chỉ nhận đúng URI của ảnh đó mà **hoàn toàn không cần xin quyền đọc bộ nhớ**.

---

## IV. Ba Bài Toán Sống Còn Cho Siêu Ứng Dụng (Super App Runtime Resilience)

Để biến một template từ chuẩn kiến trúc tĩnh thành một nền tảng Super App thực chiến "bất khả chiến bại", hệ thống phải giải quyết triệt để 3 bài toán sống còn tại runtime:

### 1. Quản trị tài nguyên & Vòng đời bộ nhớ: Giải phóng cưỡng bức (Force Eviction / LRU State Hibernation)

*   **Vấn đề:** Người dùng mở liên tiếp Mini App A ➡️ Mini App B ➡️ Mini App C... Nếu Host App không giải phóng các Mini App ngầm, việc tích lũy ViewModel, Image Cache, và Composable Nodes sẽ dẫn đến lỗi Out-Of-Memory (OOM) và crash diện rộng trên các thiết bị RAM 2GB–3GB.
*   **Giải pháp kiến trúc: LRU Active Mini Apps Cache & Hibernation:**
    1.  Host Shell duy trì giới hạn Mini App hoạt động đồng thời (ví dụ: `MAX_ACTIVE_MINI_APPS = 2`).
    2.  Khi người dùng mở Mini App thứ 3, Host kích hoạt tín hiệu **Force Evict / Hibernate** đối với Mini App lâu nhất chưa tương tác:
        *   Mini App lưu snapshot state hiện tại vào `SavedStateHandle` hoặc Disk Cache tạm.
        *   Giải phóng toàn bộ image/bitmap cache của feature đó (`Coil.imageLoader.memoryCache.remove(keys)`).
        *   Dispose Composable node và hủy `CoroutineScope` của màn hình đó.
    3.  Khi người dùng bấm Back quay lại Mini App đã bị giải phóng: Host kích hoạt luồng **Hydrate / Restore State** từ snapshot và nạp lại giao diện mượt mà.
*   **Dynamic Split De-allocation (DFM):** Khi người dùng thoát hẳn khỏi một Mini App dạng DFM (`com.android.dynamic-feature`), Host kích hoạt dọn rác đối tượng nặng và làm mới đăng ký ServiceLoader.

### 2. Bảo mật ranh giới giữa các Mini App: Crash Isolation (Error Boundary) & Permission Gatekeeper

*   **Vấn đề:** 
    *   Một Mini App do bên thứ ba hoặc outsource phát triển gặp crash không bắt được (`NullPointerException`, lỗi JNI/C++...) có thể kéo sập toàn bộ Host App.
    *   Mini App độc hại có thể đăng ký lắng nghe `AppEventBus` để đánh cắp Auth Token hoặc dữ liệu nhạy cảm của người dùng.
*   **Giải pháp kiến trúc:**
    1.  **Composable Error Boundary (Hộp cát cô lập sự cố):** Bọc mọi entry point của Mini App trong một container an toàn:
        ```kotlin
        @Composable
        fun MiniAppContainer(
            featureName: String,
            content: @Composable () -> Unit,
        ) {
            SafeErrorBoundary(
                onError = { throwable ->
                    Logger.e("MiniApp [$featureName] crashed: ${throwable.message}")
                },
                fallback = {
                    MiniAppCrashFallbackScreen(
                        featureName = featureName,
                        onRetry = { /* reload mini app */ },
                        onExit = { navigator.pop() }
                    )
                }
            ) {
                content()
            }
        }
        ```
        👉 **Kết quả:** Mini App bị lỗi chỉ hiển thị màn hình fallback, Host App và các Mini App khác vẫn chạy bình thường 100%.
    2.  **Permission Gatekeeper & Scoped Token Exchange:**
        *   Tuyệt đối không phát Master Token lên EventBus.
        *   Host App cung cấp `HostSecurityGatekeeper.requestScopedToken(featureId, requiredScope)`.
        *   Host kiểm tra danh sách quyền được cấp phép (`MiniAppCapabilityRegistry`) và chỉ cấp **Downscoped Temporary Token** (chỉ có quyền gọi đúng endpoint của feature đó với thời hạn ngắn).
        *   Host giữ toàn quyền xác thực và kiểm duyệt phần cứng (Camera, GPS) trước khi cho phép Mini App kích hoạt sensor.

### 3. Chiến lược phát hành & Giám sát độc lập: Remote Config, Router Kill-Switch & DFM Dynamic Delivery

*   **Vấn đề:** Khi một Mini App bị phát hiện lỗi nghiêm trọng (lỗ hổng bảo mật, tính toán sai), việc chờ Google Play duyệt bản cập nhật mất 1–3 ngày sẽ gây thiệt hại lớn.
*   **Giải pháp kiến trúc:**
    1.  **Router-Level Kill-Switch (Ngắt khẩn cấp tại cửa ngõ DeepLinkRouter):**
        *   Tích hợp trực tiếp `FeatureFlagGuard` vào chuỗi `DeepLinkGuard Pipeline` sẵn có trong kiến trúc Tier 1:
            ```mermaid
            flowchart LR
                URI["DeepLink / Click Tab"] --> Router["DeepLinkRouter"]
                Router --> Guard{"FeatureFlagGuard"}
                Guard -- "Bình thường" --> Target["Mở Mini App"]
                Guard -- "Bị khóa / Lỗi" --> Maint["Hiển thị Maintenance Screen"]
            ```
        *   Host App cập nhật cấu hình Remote Config (Firebase Remote Config hoặc Backend Gateway).
        *   Nếu `features.<name>.status == "MAINTENANCE"`, Router lập tức chặn điều hướng và hiển thị màn hình thông báo bảo trì mà không cần nộp bản cập nhật lên Google Play.
    2.  **OTA / DFM Dynamic Delivery:** Kết hợp Android App Bundle Dynamic Delivery (`SplitInstallManager`). Bản vá của từng DFM split có thể tải ngầm theo nhu cầu mà không bắt người dùng tải lại toàn bộ ứng dụng lớn.

---

## V. Danh Mục Nâng Cấp Chiến Lược (Future Enhancements Backlog)

Dưới đây là các module tính năng đã được thiết kế sẵn để đưa vào thực chiến khi dự án mở rộng quy mô:

### 1. Bảo Mật & Fintech Defense (Security Hardening)
*   **1.1 Biometrics Authentication Manager:**
    *   *Mục đích:* Xác thực vân tay / FaceID an toàn chuẩn AndroidX Biometric.
    *   *Vị trí:* `:packages:core` (interface) và `:packages:ui_kit` (prompt UI wrapper).
    *   *Tích hợp:* Sử dụng `BiometricPrompt` kết hợp `CryptoObject` mã hóa token qua Android KeyStore.
*   **1.2 Device Integrity & Root/Hook Detector:**
    *   *Mục đích:* Phát hiện thiết bị đã bị can thiệp (Root, Magisk, KernelSU, Xposed, Frida, Emulator).
    *   *Vị trí:* `:packages:core` (`SecurityIntegrityChecker`).
*   **1.3 Screen & Memory Privacy:**
    *   *Mục đích:* Chống chụp màn hình / quay video ở màn hình nhạy cảm (`FLAG_SECURE`).
    *   *Tiện ích:* Modifier hoặc DisposableEffect `LockScreenSecurity()`.
*   **1.4 Network Security Config & SSL Pinning:**
    *   *Mục đích:* Khóa chặt certificate và chặn cleartext traffic HTTP.
    *   *Vị trí:* `res/xml/network_security_config.xml` và `CertificatePinner` trong `:packages:network`.

### 2. Khả Năng Chịu Tải & Offline-First (Resilience & Caching)
*   **2.1 Realtime Network Connectivity Observer:**
    *   *Mục đích:* Theo dõi trạng thái kết nối mạng qua `ConnectivityManager.NetworkCallback`.
    *   *Vị trí:* `:packages:core` phát ra `Flow<NetworkStatus>`.
    *   *Giao diện:* `@Composable NetworkBanner` trong `:packages:ui_kit` tự hiện thanh báo offline.
*   **2.2 Intelligent Network Retry Interceptor:**
    *   *Mục đích:* Tự động thử lại theo cấp số nhân (Exponential Backoff) với các request idempotent gặp lỗi mạng chập chờn.
*   **2.3 Room + Paging 3 RemoteMediator Pattern:**
    *   *Mục đích:* Mẫu chuẩn nạp dữ liệu từ DB trước, tải ngầm qua API sau và tự động cập nhật UI.

### 3. Design System & Micro-Interactions (UI/UX Kit)
*   **3.1 Shimmer Loading Skeleton:**
    *   *Mục đích:* Hiệu ứng skeleton card chuyển động khi chờ tải dữ liệu (`Modifier.shimmer()`) thay cho icon xoay tròn đơn điệu.
*   **3.2 Design System Tokens:**
    *   *Mục đích:* Chuẩn hóa toàn bộ Spacing, Radius, Typography scales, và Elevation thành các object token dùng chung.
*   **3.3 Global Feedback & Dialog Coordinator:**
    *   *Mục đích:* Quản lý thông báo lỗi hệ thống, Alert Dialog, BottomSheet tập trung qua `AppEventBus`.

### 4. DevOps, CI/CD & Build Variants (Release Automation)
*   **4.1 GitHub Actions Workflows Mẫu:**
    *   `.github/workflows/ci.yml`: Chạy tự động khi mở PR (`spotlessCheck`, `:konsist-test:test`, `apiCheck`, `detekt`, `testDebugUnitTest`).
    *   `.github/workflows/release.yml`: Build production App Bundle, tự động ký số bằng Keystore từ GitHub Secrets.
*   **4.2 Proguard / R8 Consumer Rules Chuẩn Chỉ:**
    *   Mỗi package và DFM split duy trì `consumer-rules.pro` để bảo vệ DTO, `ServiceLoader`, và Navigation serialization khi chạy R8 minification.
*   **4.3 Multi-Environment Flavors (Dev / Staging / Prod):**
    *   Cấu hình Base URL, Application ID suffix (`.dev`, `.staging`) và icon riêng biệt theo từng môi trường.

### 5. Khả Năng Quan Sát & Telemetry (Observability)
*   **5.1 Analytics Tracker Seam:**
    *   Khai báo interface `AnalyticsTracker` tại `:packages:platform`. Các Mini App chỉ phát event tracking qua interface này mà không phụ thuộc vào SDK cụ thể (Firebase, Mixpanel, AppsFlyer).
*   **5.2 MVI Action & State Breadcrumbs:**
    *   Tự động ghi log hành vi tương tác khi debug để tái hiện sự cố và đính kèm crash log trên Crashlytics.

### 6. Khôi Phục Trạng Thái khi Process Death (State Restoration)
*   **6.1 SavedStateHandle trong `MviViewModel`:**
    *   Cung cấp cơ chế lưu trữ và khôi phục các biến trạng thái quan trọng vào `SavedStateHandle` khi ứng dụng bị OS kill trong nền do thiếu RAM.

---

## VI. Ma Trận Phân Kỳ Triển Khai (Prioritized Execution Matrix)

Khi bắt đầu một dự án mới từ template này, hãy dựa vào tính chất dự án để kích hoạt theo thứ tự ưu tiên:

| Giai đoạn | Hạng mục | Nhóm chức năng | Tác động kiến trúc | Khi nào cần áp dụng? |
|:---:|---|---|:---:|---|
| **Phase A (P0)** | **Network Connectivity Monitor** | Core / UI Kit | Thấp (Đã có sẵn seam) | Ngay khi app có tương tác API cần báo mất mạng |
| **Phase A (P0)** | **Biometrics Authentication** | Security | Trung bình | Dự án Fintech, Ngân hàng, Ví điện tử |
| **Phase A (P0)** | **R8 / ProGuard Rules chuẩn** | Build / Release | Thấp | Trước khi đóng gói bản Release đầu tiên |
| **Phase B (P1)** | **Shimmer Loading Skeleton** | UI Kit | Thấp | Khi hoàn thiện UI/UX theo Design System |
| **Phase B (P1)** | **GitHub Actions CI/CD** | DevOps | Độc lập (CI file) | Khi bắt đầu làm việc theo team nhiều người |
| **Phase B (P1)** | **Build Variants (Dev/Staging/Prod)** | Build Config | Trung bình | Khi bắt đầu kết nối hệ thống Backend thật |
| **Phase C (P2)** | **Analytics Tracker Bridge** | Platform | Thấp (Đã có seam) | Khi đội ngũ Product yêu cầu đo lường dữ liệu |
| **Phase C (P2)** | **SavedStateHandle Restoration** | Framework | Trung bình | Khi có các form nhập liệu phức tạp nhiều bước |

---

## VII. Tổng Kết & Đề Xuất Lộ Trình Hành Động (Summary & Next Steps Roadmap)

### 1. Đề xuất nâng cấp cấu hình Template (Lean Mode vs Enterprise Mode)

Tạo một script config nhanh hoặc tham số trong `scripts/rename_project.sh` cung cấp tùy chọn cấu hình template ở 2 chế độ:
1.  **Standard / Lean Mode:** Thích hợp cho ứng dụng độc lập, MVP, startup, team nhỏ (tinh gọn, build cực nhanh).
2.  **Enterprise Super App Mode:** Giữ nguyên đầy đủ Governance, DFM, BCV, Sandbox runners, và DeepLink Router Engine.

### 2. Bảng lộ trình đề xuất bổ sung tiếp theo (Next Steps Roadmap)

| STT | Hạng mục bổ sung | Phân loại | Độ cần thiết | File dự kiến tác động |
|:---:|---|---|:---:|---|
| **1** | **Network Connectivity Monitor** + Thanh thông báo Offline | Network / UX | ⭐⭐⭐⭐⭐ | `:packages:core`, `:packages:ui_kit` |
| **2** | **Biometrics Authentication Manager** (Vân tay / FaceID) | Security | ⭐⭐⭐⭐⭐ | `:packages:core` |
| **3** | **Network Security Config & SSL Pinning** mẫu | Security | ⭐⭐⭐⭐ | `:packages:network`, `:app` |
| **4** | **ProGuard / R8 Consumer Rules** chuẩn hóa cho toàn bộ module | Build & Release | ⭐⭐⭐⭐⭐ | `buildSrc`, các `consumer-rules.pro` |
| **5** | **GitHub Actions CI/CD Pipeline** (`ci.yml`, `release.yml`) | DevOps | ⭐⭐⭐⭐⭐ | `.github/workflows/` |
| **6** | **Shimmer Loading Skeleton** & Global Feedback Coordinator | UI Kit | ⭐⭐⭐⭐ | `:packages:ui_kit` |
| **7** | **Analytics Tracker Seam** (`AnalyticsTracker` interface) | Platform | ⭐⭐⭐⭐ | `:packages:platform` |
| **8** | **Environment Flavoring** (Dev / Staging / Prod) | Build Config | ⭐⭐⭐⭐ | `buildSrc`, `app/build.gradle.kts` |

---

> 💡 **Kết luận:** Template hiện tại đã đạt độ hoàn chỉnh vững chắc nhất về mặt Kiến trúc và Quản trị ranh giới (Architecture & Governance). Danh mục lộ trình trên là cẩm nang sẵn sàng để bạn "nhấc ra sử dụng" bất cứ khi nào dự án đòi hỏi yêu cầu chuyên sâu.

