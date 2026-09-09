# Epic: DeepLink Router Engine — đóng trụ 2.1 của khung Super App Governance

**Ngày**: 2026-09-10
**Trạng thái**: Draft — chờ review
**Epic name (dự kiến)**: `deeplink_router_engine`
**Tiền đề**: epic `android_super_app_template` đã hoàn tất (Task 1–16)

---

## 0. Nguồn tham chiếu

| Tài liệu / mã nguồn | Vai trò |
|---|---|
| `.devtool/epic/android_super_app_template/2026-09-02-android-super-app-template-design.md` | **Kiến trúc nền**. §4.4 (hợp đồng DFM), §5 (giao tiếp cross-feature), §6.1 (Konsist K1–K9). Epic này bám nguyên các đường biên đó, không phát minh đường biên mới. |
| `docs/architecture/ARCHITECTURE.md` | Quy tắc layer `Presentation → Domain ← Data`, MVI, feature-first. |
| `packages/platform/**` | `AppRoutes`, `AppEventBus`, `AppEvent`, `FeatureEntry`, `FeatureInstaller` — engine mới cắm vào đây. |
| `shell/**` | `ShellViewModel` / `ShellState` — nơi thi hành lệnh điều hướng. |
| `app/src/main/AndroidManifest.xml`, `app/.../MainActivity.kt` | Cửa vào Intent. |

### 0.1 Ghi chú về khung governance được áp

Khung 4 trụ / 8 tiêu chí được phát biểu ở hai bản: bản gốc (thiên Android) và bản **đã điều chỉnh cho Flutter**. Repo này là **native Android**, nên hai chỗ "đã điều chỉnh" trong bản Flutter (tiêu chí 1.2 — Dynamic Feature Module; 3.2 — Dagger/Hilt) **phải đảo ngược về bản gốc** khi áp ở đây: chính những cơ chế mà bản Flutter tuyên bố "không tồn tại trong hệ sinh thái" thì ở đây tồn tại và đã được implement (xem `features/scanner` — `com.android.dynamic-feature` on-demand thật, `SplitInstallManager`, `SplitCompat`, `ServiceLoader`).

Epic này vì thế đánh giá và thiết kế theo **bản gốc Android-native**.

---

## 1. Bối cảnh — đánh giá hiện trạng theo 8 tiêu chí

Đánh giá `android_digital_wallet` @ `android_super_app_template` trước khi làm epic này:

| # | Tiêu chí | Kết quả | Bằng chứng |
|---|---|---|---|
| **1.1** | Host App là Container thuần | ✅ Đạt | `:app` chỉ có Hilt aggregation + `NavDisplay` + `Application` + 1 Activity + `FeatureInstallerImpl`. `:shell` chỉ tab-shell. Network → `:packages:network`; Storage → `:packages:core` (`CacheStore`, `room/`); Session → `core/session/SessionManager`. |
| **1.2** | Mini App tách module độc lập | ✅ Đạt — **vượt bản Flutter** | `features/scanner` là `com.android.dynamic-feature` on-demand thật: `dist:onDemand`, `SplitInstallManager` (`FeatureInstallerImpl`), `SplitCompat.install`, discovery qua `ServiceLoader`. Tải code lúc runtime đúng nghĩa. |
| **2.1** | **DeepLink Router Engine** | 🔴 **Nửa vời** | Central Router có (`Navigator` + `NestedNavigator` + `AppRoutes` + `EntryProviderInstaller`; K1 enforced, whitelist rỗng). **URL Schema / Deeplink hoàn toàn không tồn tại**: manifest chỉ có `MAIN`/`LAUNCHER`, không `<data android:scheme>`, không `ACTION_VIEW`, không `onNewIntent`, không parse URI, không map URI → `NavKey`. |
| **2.2** | Event Bridge | ✅ Đạt (chiều broadcast) | `AppEventBus` — `SharedFlow` typed, `replay = 0`. Không có chiều request→response; epic nền cố ý thay bằng Dependency Inversion. |
| **3.1** | Đóng gói State cục bộ | ✅ Đạt | `MviViewModel` ở `:packages:framework`; mỗi feature có `*Action/*State/*Event/*ViewModel` riêng; không global store. Konsist K2/K5 ép. |
| **3.2** | DI phân tầng | 🟡 Một phần | Hilt có (tooling thoả tiêu chí gốc). Nhưng DI **phẳng** 1 `SingletonComponent` (non-goal của epic nền), và `AppEventBus` + `Navigator` là **class cụ thể** — host đang đẩy *implementation* xuống feature, đúng chỗ tiêu chí cấm. `FeatureInstaller` / `AppThemeManager` / `AppLocalizationManager` thì làm đúng. |
| **4.1** | Sandbox Development | 🟡 Một phần | `:features:settings:testDebugUnitTest` chạy độc lập ✅. Nhưng `:features:scanner` **phụ thuộc `:app`** (đảo hướng DFM) → module thoả 1.2 tốt nhất lại vi phạm 4.1. Không có UI runner độc lập (ghi nhận là gap đã biết). |
| **4.2** | API Contract nội bộ | ✅ Đạt (mức compile-time) | `.github/workflows/ci.yml`: `konsist-test:test` + `detekt` + `spotless` + `testDebugUnitTest` + `assembleDebug` + `bundleDebug` + assert scanner split. K1–K9 enforced hết, whitelist rỗng, baseline rỗng. |

**Tổng: 5 đạt / 3 chưa trọn. Trụ yếu nhất là 2.1** — và đó là phạm vi epic này.

### 1.1 Ba ràng buộc đặc thù của template làm bài toán khó hơn deeplink thông thường

1. **Nested backstack theo tab** — `ShellState` giữ ba backstack rời (`homeBackStack` / `scannerBackStack` / `settingsBackStack`). Deeplink phải biết đẩy vào backstack nào và chuyển tab nào.
2. **DFM chưa cài** — link tới `scanner` khi split chưa về: code biết pattern đó **nằm trong split chưa tải**. Phải cài trước rồi mới parse được.
3. **Quyền sở hữu bảng map** — để ở `:platform` thì host "biết" mọi feature; để feature tự khai thì vỡ với DFM.

### 1.2 Doc drift phát hiện kèm

`features/scanner/build.gradle.kts` mô tả `shell/.../navigation/OnDemandFeatures.kt` là "registry cho DFM được gọi từ call site bất kỳ". **File đó chưa bao giờ được viết.** Epic này đóng nó lại — không phải bằng một file registry mới, mà bằng chính `AppDeepLinks.entryPoints` (đã mang `dynamicModule`).

---

## 2. Mục tiêu (Goals)

| # | Mục tiêu |
|---|---|
| **D1** | Đưa tiêu chí **2.1 từ 🔴 nửa vời lên ✅ đạt đủ**: bổ sung nửa "URL Schema / Deeplink" còn thiếu, giữ nguyên nửa "Central Router" đã có. |
| **D2** | **Bốn nguồn link, một pipeline duy nhất**: custom scheme `myapp://`, Android App Links `https://`, push-notification payload, và điều hướng nội bộ bằng URL string. Không có nhánh xử lý riêng cho nguồn nào. |
| **D3** | **Không phá tiêu chí 1.2**: link tới một feature on-demand chưa cài phải tự kích hoạt `FeatureInstaller.ensureInstalled(...)`, hiện UI tiến trình sẵn có, rồi mở đúng đích. |
| **D4** | **Không phá tiêu chí 2.1 nửa "feature mù nhau"**: feature khai URL của riêng nó, không feature nào biết URL của feature khác. K1 vẫn xanh, whitelist vẫn rỗng. |
| **D5** | **Không làm xấu tiêu chí 4.1**: test resolver của một feature phải chạy được bằng `:features:<x>:testDebugUnitTest` mà không cần `:app`. |
| **D6** | **Chiến lược backstack khai báo được trên từng link** (`Placement`), mặc định là `InTab` + synthesize parents. Đổi hành vi một link = sửa một dòng trong feature đó. |
| **D7** | **Guard chain mở rộng được**: auth guard + pending link ship sẵn làm mẫu; thêm guard (feature-flag, KYC, kill-switch) = thêm một `@IntoSet`, không đụng engine. |
| **D8** | **Template-ready**: `mvi_feature` sinh sẵn resolver và tự wire; `rename_project.sh` đổi được scheme; không hard-code `myapp://` ở đâu. |

---

## 3. Non-Goals

- **`FirebaseMessagingService`.** Repo cố ý không có `google-services.json` (CI chạy không secret). Ship `DeepLinkIntentFactory.pendingIntent(...)` + tài liệu; ai cần FCM tự cắm.
- **Hosting `assetlinks.json`.** Cần domain thật; template chỉ ship placeholder host + hướng dẫn verify.
- **Deferred deep link / attribution** (install-referrer, fingerprint matching). Địa hạt SDK marketing, không phải router.
- **Path typed sinh tự động** (`/order/{id}` → `OrderRoute(id)` type-safe). Resolver tự parse. Typed builder cần KSP processor — đã cân nhắc và loại (xem §4.2).
- **Bọc `AppEventBus` / `Navigator` thành interface.** Đúng về nguyên tắc (phát hiện 3.2 ở §1) nhưng là **follow-up riêng**; nhét vào đây làm phình diff và trộn hai lý do thay đổi. Engine mới làm đúng ngay từ đầu để tạo tiền lệ.
- **Đổi cơ chế navigation của host.** navigation3 + `Navigator` / `NestedNavigator` / `NavDisplay` giữ nguyên tuyệt đối — y như ràng buộc của epic nền.
- **DI phân tầng thật (hierarchical Hilt component).** Vẫn 1 `SingletonComponent` phẳng.

---

## 4. Quyết định kiến trúc

### 4.1 Ba hướng đã cân nhắc

| Hướng | Được | Mất |
|---|---|---|
| **A — Registry tập trung ở `:platform`** (`AppDeepLinks` liệt kê mọi pattern) | Đơn giản nhất; một file thấy toàn bộ URL surface; DFM không thành vấn đề | `:platform` phình theo số feature; thêm 1 link = sửa file dùng chung → merge conflict; host "biết" mọi feature |
| **B — Phân tán hoàn toàn** (mỗi feature contribute `DeepLinkResolver` qua Hilt `@IntoSet`) | Đối xứng 1:1 với `EntryProviderInstaller` đã có; feature sở hữu URL của mình | **Vỡ với DFM on-demand.** Link `myapp://scanner/qr` khi split chưa cài → không class nào trong app biết pattern đó tồn tại → không parse được → không biết cài module nào. `ServiceLoader` không cứu được (chỉ thấy class *sau khi* split về). Phá đúng tiêu chí 1.2. |
| **C — Hybrid hai tầng** ✅ **CHỌN** | Xem §4.3 | Hai tầng thay vì một; phức tạp hơn A về khái niệm |

### 4.2 Hướng đã loại: KSP annotation processor

Feature chỉ cần `@DeepLink("settings/profile")` trên `NavKey`, processor sinh registry lúc compile, check trùng pattern ngay tại compile-time. Rất gọn cho người viết feature.

**Loại vì**: thêm một KSP processor tự viết vào template (rào cản lớn cho người clone, thêm build step), **và vẫn không giải được DFM** — processor không sinh code xuyên qua ranh giới split vào base module. Chi phí cao, không mua được thứ đắt nhất.

### 4.3 Hướng C — tận dụng chính rule K9 sẵn có

**Nhận xét quyết định**: Konsist K9 nói *"`NavKey` nào được dùng ngoài feature của nó thì phải khai ở `:platform`"*. Tập route mà host cần biết **trước** để deeplink hoạt động chính là tập route **vượt biên giới feature** — trong đó có toàn bộ entry point của DFM (`AppRoutes.ScannerRoute` đã nằm sẵn ở `:platform`). Hai tập này trùng nhau. Đó không phải trùng hợp: cả hai đều là "thứ mà host phải biết mà không được import feature".

Nên chia hai tầng **đúng trên đường biên đã có**, không tạo đường biên mới:

| Tầng | Ở đâu | Nội dung | Ai sửa |
|---|---|---|---|
| **1 — Entry point** | `:platform`, cạnh `AppRoutes` | `feature` key → `entryRoute` + `tab` + `dynamicModule` + `requiresAuth` | Chỉ khi thêm **feature mới** (1 dòng) |
| **2 — Pattern chi tiết** | Trong từng feature | pattern → `destination` + `placement` + `requiresAuth` | Team sở hữu feature, không đụng ai |

### 4.4 Hệ quả: va chạm pattern bị chặn bằng cấu trúc, không bằng rule

Câu hỏi thường giết các deeplink engine — *"làm sao biết hai feature không cùng giành một URL?"* — ở hướng C **không tồn tại**. Tầng 1 định tuyến bằng `feature` key trước; key là duy nhất (unit test ép); nên hai feature **không thể về mặt vật lý** cùng nhận một URI.

Va chạm chỉ còn xảy ra *bên trong* một feature, giữa các pattern do chính team đó viết — vấn đề cục bộ, test cục bộ.

Hướng C biến một bài toán governance thành một bất biến cấu trúc.

---

## 5. Contract — package mới `com.danhdue.platform.deeplink`

### 5.1 Link đã chuẩn hoá

```kotlin
data class DeepLink(
    val raw: String,                    // giữ nguyên để log / pending
    val feature: String,                // "settings" — khoá tầng 1
    val segments: List<String>,         // ["profile"] — tầng 2 parse
    val params: Map<String, String>,
)
```

Bốn nguồn cho ra URI khác dạng (`myapp://settings/profile` vs `https://wallet.acme.com/settings/profile`). `DeepLinkParser` quy về một dạng duy nhất **trước khi bất kỳ ai nhìn thấy**:

- **custom scheme**: `authority` → `feature`; path segments → `segments`
- **App Links (`https`)**: bỏ domain; path segment **đầu tiên** → `feature`; phần còn lại → `segments`

Parser dùng `java.net.URI`, **không dùng `android.net.Uri`** — để chạy được trong unit test JVM thuần, không cần Robolectric.

### 5.2 Tầng 1 — entry point

```kotlin
data class FeatureEntryPoint(
    val feature: String,                 // "scanner"
    val entryRoute: NavKey,              // AppRoutes.ScannerRoute — K9 đã ép nằm ở :platform
    val tab: Int?,                       // index tab shell; null nếu không thuộc tab nào
    val dynamicModule: String? = null,   // "scanner" nếu là DFM on-demand
    val requiresAuth: Boolean = false,   // gate thô cấp feature — xem §6.3
)

object AppDeepLinks {
    val entryPoints: List<FeatureEntryPoint> = listOf(
        FeatureEntryPoint("settings", AppRoutes.SettingsRoute, tab = 2),
        FeatureEntryPoint("scanner",  AppRoutes.ScannerRoute,  tab = 1, dynamicModule = "scanner"),
    )
}
```

Đây là **toàn bộ** phần dùng chung — một dòng cho mỗi feature, chỉ đụng khi thêm feature mới.

`tab` là `Int` chứ không phải `ShellTab` vì `:platform` **không được** biết `:shell` (chiều phụ thuộc ngược lại). Index này vốn đã là từ vựng của shell: `ShellTab.Home(0)` / `Scanner(1)` / `Settings(2)`.

### 5.3 Tầng 2 — feature tự khai pattern

```kotlin
fun interface DeepLinkResolver {
    /** Target nếu nhận ra link; null nếu link không phải của feature này. */
    fun resolve(link: DeepLink): DeepLinkTarget?
}

data class DeepLinkTarget(
    val destination: NavKey,
    val placement: Placement? = null,   // null = dẫn xuất mặc định từ tầng 1
    val requiresAuth: Boolean = false,  // gate mịn cấp từng link
)

sealed interface Placement {
    /** Chuyển sang [tab] và thay backstack của nó bằng parents + destination. */
    data class InTab(val tab: Int, val parents: List<NavKey> = emptyList()) : Placement

    /** Đè full-screen lên ROOT backstack (Navigator), không đụng tab. */
    data object RootFullScreen : Placement

    /** Append vào backstack của tab đang active, không đổi tab. */
    data object CurrentTab : Placement
}
```

**Cách đóng góp resolver:**

- **Feature install-time** → Hilt `@Provides @IntoSet DeepLinkResolver`. Đối xứng chính xác với `EntryProviderInstaller` đang có — không có cơ chế mới để học.
- **Feature on-demand (DFM)** → mở rộng `FeatureEntry` sẵn có thêm **một method có default**, không thêm kênh discovery thứ hai:

```kotlin
interface FeatureEntry {
    fun installer(): EntryProviderInstaller
    fun resolver(): DeepLinkResolver? = null   // MỚI — có default, không phá impl hiện tại
}
```

**Mặc định `placement`**: `placement == null` → `InTab(tab = entryPoint.tab, parents = listOf(entryPoint.entryRoute))`. Mặc định được **dẫn xuất từ tầng 1**, feature không phải lặp lại. Nên `settings/profile` chỉ cần `DeepLinkTarget(ProfileRoute)` là đã có hành vi đúng; link nào cần khác thì khai `placement` tường minh.

Nếu `entryPoint.tab == null` mà `placement == null` → mặc định là `RootFullScreen`.

### 5.4 Guard chain

```kotlin
/**
 * Guard PHẢI idempotent: chain có thể được gọi hai lần cho cùng một link
 * (pre-gate trước khi tải split, và lần đầy đủ sau khi resolve) — xem §6.3.
 */
interface DeepLinkGuard {
    val order: Int get() = 0     // Set từ Hilt không có thứ tự → order quyết định
    suspend fun check(link: DeepLink, target: DeepLinkTarget): GuardVerdict
}

sealed interface GuardVerdict {
    data object Allow : GuardVerdict
    data class Redirect(val to: String) : GuardVerdict     // URI đích, vd link login
    data class Block(val reason: String) : GuardVerdict    // reason hiển thị cho user
}
```

Template ship sẵn **một** guard mẫu: `AuthDeepLinkGuard` (`order = 0`) — đọc `SessionManager` (`:packages:core`), thấy `requiresAuth` mà chưa đăng nhập thì cất link vào `PendingDeepLinkStore` rồi trả `Redirect(loginUri)`.

Thêm guard feature-flag / KYC / kill-switch về sau = thêm một `@IntoSet DeepLinkGuard`. Không đụng engine, không đụng feature khác.

### 5.5 Pending link

```kotlin
interface PendingDeepLinkStore {
    fun put(link: String)
    fun takeIfAny(): String?     // đọc-và-xoá, đúng một lần
}
```

**In-memory, KHÔNG persist** — có chủ ý. Một deeplink ghi xuống đĩa có thể bắn lại sau nhiều ngày trong ngữ cảnh hoàn toàn khác; đó là mùi bảo mật, không phải tính năng.

Kèm **TTL 10 phút**, để link cất lúc user bỏ dở login không bật lên vào một phiên khác.

Kích hoạt replay: thêm `AppEvent.UserLoggedIn` vào sealed `AppEvent` — đối xứng với `UserLoggedOut` đã có. Router `on<UserLoggedIn>()` → `takeIfAny()?.let(::dispatch)`.

### 5.6 Mặt tiền

```kotlin
interface DeepLinkRouter {
    fun dispatch(uri: String)                    // cả 4 nguồn đều vào đây
    val commands: Flow<NavigationCommand>        // host thi hành
}

sealed interface NavigationCommand {
    data class OpenInTab(val tab: Int, val stack: List<NavKey>) : NavigationCommand
    data class OpenFullScreen(val destination: NavKey) : NavigationCommand
    data class OpenInCurrentTab(val destination: NavKey) : NavigationCommand
    data class EnsureModule(val module: String, val replay: String) : NavigationCommand
    data class Failed(val raw: String, val reason: FailureReason) : NavigationCommand
}

enum class FailureReason { Malformed, UnknownFeature, NoResolver, Blocked, RedirectLoop, InstallFailed }
```

**`DeepLinkRouter` là `interface`; impl `DefaultDeepLinkRouter` là `internal`.** Chủ ý: nó làm đúng ngay từ đầu điều mà phát hiện 3.2 ở §1 chỉ ra — `AppEventBus` và `Navigator` hiện là class cụ thể, host đang đẩy *implementation* xuống feature. Seam mới này là tiền lệ để về sau bọc hai cái kia (follow-up, không thuộc epic này).

---

## 6. Luồng end-to-end

### 6.1 Bốn nguồn, một cửa vào

| Nguồn | Cơ chế | Ship trong template |
|---|---|---|
| Custom scheme `myapp://` | `intent-filter` `ACTION_VIEW` trên `MainActivity` | ✅ đầy đủ |
| App Links `https://` | Cùng `intent-filter`, `android:autoVerify="true"` | ✅ manifest + placeholder host; `assetlinks.json` là việc của người clone |
| Push notification | `PendingIntent` bọc **chính Intent `ACTION_VIEW` đó** | ⚠️ ship `DeepLinkIntentFactory` + doc; **không** ship `FirebaseMessagingService` |
| Internal | `deepLinkRouter.dispatch("myapp://settings/profile")` | ✅ đầy đủ |

Ba nguồn đầu quy về một `Intent`; nguồn thứ tư gọi thẳng. **Chỉ có một pipeline.**

### 6.2 Hai thay đổi bắt buộc ở `MainActivity`

**(a) `android:launchMode="singleTop"`.** Hiện `MainActivity` là `standard`. App đang chạy mà nhận deeplink thì Android dựng **instance `MainActivity` thứ hai** chồng lên → hai `Navigator` (`@ActivityRetainedScoped`), hai backstack, state phân đôi. `singleTop` cho `onNewIntent` thay vì instance mới.

Chọn `singleTop` chứ không `singleTask`: app một-Activity thì `singleTop` là đủ, và không kéo theo tác dụng phụ về task affinity / clear stack.

**(b) Đánh dấu Intent đã tiêu thụ.** `onCreate` đọc `intent.data`; xoay màn hình → `onCreate` chạy lại với **cùng Intent đó** → link bắn lại lần hai. Sửa bằng cách set extra `EXTRA_CONSUMED` lên `getIntent()` sau khi dispatch (mutate đúng object Intent được giữ qua config change).

### 6.3 Pipeline

```
dispatch(uri)
  │
  ├─ parse ──────────────────────────────── hỏng ──► Failed(Malformed)
  │
  ├─ tra AppDeepLinks theo link.feature ─ không có ──► Failed(UnknownFeature)
  │
  ├─ PRE-GATE (chỉ chạy khi entryPoint.requiresAuth == true)
  │     └─ guard chain với target dẫn xuất từ tầng 1
  │          (destination = entryRoute, requiresAuth = entryPoint.requiresAuth)
  │        chưa đăng nhập ──► cất pending + Redirect(login)
  │
  ├─ entryPoint.dynamicModule != null && module chưa ready?
  │     └─ YES ──► emit EnsureModule(module, replay = uri)   ──┐
  │                                                            │
  │        shell cài split (progress UI sẵn có) rồi gọi lại ───┘
  │        dispatch(uri) — vòng hai đi tiếp xuống dưới
  │
  ├─ hỏi lần lượt DeepLinkResolver (Hilt @IntoSet + ServiceLoader)
  │     lấy target đầu tiên non-null ── không ai nhận ──► Failed(NoResolver)
  │
  ├─ guard chain đầy đủ, theo `order`
  │     ├─ Allow    ──► đi tiếp
  │     ├─ Redirect ──► dispatch(to) đệ quy, cap depth = 3 ──► Failed(RedirectLoop)
  │     └─ Block    ──► Failed(Blocked)
  │
  ├─ dẫn xuất placement (null → InTab từ tầng 1; tab null → RootFullScreen)
  │
  └─ emit NavigationCommand
```

**Vì sao pre-gate auth đặt TRƯỚC bước cài split**: nếu để guard chạy sau resolve như thường lệ, một user chưa đăng nhập bấm link `scanner` sẽ **tải xong cả split rồi mới bị đá về login**. Nên `FeatureEntryPoint` mang thêm `requiresAuth` (thô, cấp feature) kiểm trước khi tải; `DeepLinkTarget.requiresAuth` vẫn kiểm sau (mịn, cấp từng link).

Cùng một chain được gọi ở hai điểm ⇒ **guard bắt buộc phải idempotent**. Ghi rõ trong KDoc của `DeepLinkGuard`.

**Cap depth = 3 cho `Redirect`**: guard A đá sang B, B đá lại A → vòng lặp vô hạn. Cap rồi `Failed(RedirectLoop)`.

### 6.4 Kênh giao lệnh — và vì sao KHÔNG dùng `AppEventBus`

Cold start là ràng buộc quyết định: link tới trong `MainActivity.onCreate`, lúc đó `ShellViewModel` **chưa tồn tại**.

`AppEventBus` là `SharedFlow(replay = 0)` — không ai nghe thì sự kiện **rơi mất**. Đúng thiết kế cho signal broadcast, sai hoàn toàn cho deeplink, thứ cần **đảm bảo giao đúng một lần**.

Nên router dùng kênh riêng: `Channel(Channel.BUFFERED)`, consumer duy nhất là `ShellViewModel`.

- Chưa ai nghe → lệnh nằm trong buffer, không mất.
- `ShellViewModel` là `@HiltViewModel` → sống qua config change → lệnh **không** chạy lại lần hai.

**Hai kênh, mỗi kênh làm đúng việc nó giỏi**: `AppEventBus` mang *tín hiệu* (`UserLoggedIn` — mất cũng không sao, vì không ai chờ thì cũng không có pending link); `Channel` của router mang *lệnh điều hướng* (bắt buộc không được rơi).

### 6.5 Ai thi hành

**`ShellViewModel`, một chỗ duy nhất.** Nó là nơi duy nhất có đủ cả hai thứ:

- ba nested backstack (state của chính nó),
- root `Navigator` — inject được vì `@HiltViewModel` nằm trong `ViewModelComponent`, con của `ActivityRetainedComponent` nơi `Navigator` được `@ActivityRetainedScoped`.

---

## 7. Thi hành, lỗi, ca biên

### 7.1 Thi hành `Placement`

```kotlin
is NavigationCommand.OpenInTab -> reduce {
    when (cmd.tab) {
        0 -> copy(selectedTab = ShellTab.Home,     homeBackStack     = cmd.stack)
        1 -> copy(selectedTab = ShellTab.Scanner,  scannerBackStack  = cmd.stack)
        2 -> copy(selectedTab = ShellTab.Settings, settingsBackStack = cmd.stack)
        else -> this
    }
}
```

**Thay thế backstack, không append.** "Synthesize" nghĩa là *"stack phải trông đúng như thế này"* — kết quả tất định, bấm cùng một link hai lần cho ra cùng một trạng thái. Append thì stack phình dần mỗi lần mở link.

**Ngoại lệ duy nhất**: destination đã ở đỉnh stack của tab đó → **no-op**. Tránh nhấp nháy và mất state màn hình khi user bấm lại đúng link đang mở. Áp dụng cho cả `OpenFullScreen` và `OpenInCurrentTab`.

**`tab` sai (tầng 1 khai `tab = 7`)**: `:platform` không biết `:shell` nên không tự kiểm được. Đặt unit test **trong `:shell`** (module duy nhất biết cả hai): mọi `FeatureEntryPoint.tab` phải là index `ShellTab` hợp lệ. Sai → CI đỏ.

### 7.2 Fallback — gần như miễn phí

**Nhận xét**: cold start không cần fallback riêng. `MainActivity` đã seed `AppRoutes.ShellRoute` và `ShellState` đã mặc định mở tab Settings *trước khi* link được xử lý. Link hỏng lúc cold start → app đã đứng sẵn ở màn mặc định. **Fallback chính là trạng thái mặc định.**

Quy tắc: **`Failed` không bao giờ điều hướng.** Chỉ báo cáo.

| `FailureReason` | Log | Báo cho user |
|---|---|---|
| `Malformed` | ✅ | ❌ — user không làm gì được với nó |
| `UnknownFeature` / `NoResolver` | ✅ | ✅ *"Liên kết này cần bản app mới hơn"* — đúng ca app cũ nhận link bản mới |
| `Blocked(reason)` | ✅ | ✅ — hiện `reason` do guard cung cấp |
| `RedirectLoop` | ✅ | ❌ — lỗi cấu hình của dev, không phải của user |
| `InstallFailed` | ✅ | ✅ — *"Không tải được tính năng, thử lại"* |

Chỗ để báo: `ShellEvent` **hiện đang rỗng** (chỉ có comment ví dụ). Thêm `ShellEvent.ShowMessage` vào đó là dùng đúng cơ chế MVI sẵn có, không phát minh kênh mới.

### 7.3 Refactor kéo theo (có chủ đích)

`ShellState` hiện có `scannerInstalling: Boolean` + `scannerReady: Boolean` — hard-code cho đúng một module; `ShellViewModel` có hằng `SCANNER_MODULE = "scanner"`. `EnsureModule` phải chạy với module bất kỳ:

```kotlin
val installingModules: Set<String> = emptySet(),
val readyModules: Set<String> = emptySet(),
```

`ShellScreen` đọc `state.readyModules` thay vì `state.scannerReady` để quyết định nạp `ServiceLoader` installers và hiện spinner.

Đây cũng chính là `OnDemandFeatures.kt` mà `features/scanner/build.gradle.kts` mô tả nhưng chưa từng tồn tại (§1.2) — đóng lại bằng `AppDeepLinks.entryPoints`, không cần file registry riêng.

### 7.4 Ca biên còn lại

- **Cài split lỗi** → `Failed(InstallFailed)`; shell tắt spinner (logic `onFailure` đã có trong `ensureScannerInstalled`).
- **Nhiều link liên tiếp** → `Channel` giữ thứ tự, xử lý tuần tự, link sau thắng.
- **App Links chưa verify** (người clone chưa dựng `assetlinks.json`) → Android mở trình duyệt thay vì app. Đúng hành vi hệ điều hành, không phải lỗi engine — ghi vào doc kèm `adb shell pm get-app-links <pkg>`.
- **Link tới feature không có tab** (`tab == null`) → `RootFullScreen`.

---

## 8. Mặt tấn công

Khai `intent-filter` nghĩa là `MainActivity` trở thành **exported**: bất kỳ app nào trên máy cũng gửi được URI tuỳ ý vào. Bốn điều phải ghi vào tài liệu template:

1. **`params` là input không tin cậy.** Không bao giờ dùng thẳng cho quyết định nhạy cảm (số tiền, tài khoản đích). Deeplink được phép *mở đúng màn hình với dữ liệu điền sẵn*; xác nhận vẫn phải qua UI và kiểm quyền vẫn phải ở tầng domain/API.

2. **Custom scheme không độc quyền.** App khác khai `myapp://` là cướp được link. Chỉ **App Links** (`autoVerify` + `assetlinks.json`) mới có bảo chứng của hệ điều hành. Link nhạy cảm chỉ đi qua `https://`; `myapp://` để cho điều hướng nội bộ và dev/test.

3. **`requiresAuth` là phòng thủ chiều sâu, không phải cổng bảo mật.** Nó ngăn UX sai, không ngăn kẻ tấn công — kẻ tấn công có thể đăng nhập bằng tài khoản của chính họ.

4. **Hướng C cho không một tính chất an ninh**: engine **không** có đường nào để điều hướng tới một route tuỳ ý theo tên. Chỉ route nào có `DeepLinkResolver` khai tường minh mới tới được — đây là **allowlist**, không phải reflection. Một thiết kế kiểu `myapp://route?class=com.acme.SecretRoute` sẽ mở toang mọi màn nội bộ; hướng C đóng cửa đó từ trong kiến trúc, không cần rule nào canh.

---

## 9. Governance & kiểm thử

### 9.1 Kiểm hợp đồng

| Kiểm | Bằng gì | Ở đâu |
|---|---|---|
| `feature` key duy nhất trong `AppDeepLinks` | unit test | `:packages:platform` |
| `entryRoute` phải là `NavKey` khai ở `:platform` (không phải route private của feature) | unit test | `:packages:platform` |
| `tab` index hợp lệ với `ShellTab` | unit test | `:shell` |
| **K10 (mới)**: class implement `DeepLinkResolver` phải tên `*DeepLinkResolver` và nằm ở `..presentation.di..` | Konsist | `:konsist-test` |

K10 nối tiếp họ rule K5 (naming). Giá trị thật: vị trí đoán được → brick sinh đúng chỗ, review biết chỗ mà nhìn.

Ba kiểm còn lại **cố ý dùng unit test thay vì Konsist**: chúng là kiểm *giá trị runtime*, không phải kiểm *cấu trúc AST*; ép vào Konsist sẽ vừa mong manh vừa khó đọc.

**CI không cần đổi một dòng nào** — cả bốn kiểm chạy trong `testDebugUnitTest` mà `.github/workflows/ci.yml` đã gọi sẵn.

### 9.2 Kiểm thử theo tầng

| Tầng | Nội dung test | Chạy độc lập |
|---|---|---|
| `DeepLinkParser` | URI hỏng, param encode, unicode, `/` thừa, scheme hoa/thường, `https` vs custom, path rỗng | ✅ JVM thuần |
| Guard chain | thứ tự `order`, ba verdict, cap redirect depth, idempotency | ✅ |
| `PendingDeepLinkStore` | take-once, TTL hết hạn | ✅ |
| `DefaultDeepLinkRouter` | pipeline đầy đủ với resolver giả; nhánh pre-gate; nhánh `EnsureModule` | ✅ |
| Resolver của từng feature | pattern của riêng nó | ✅ **`:features:settings:testDebugUnitTest` chạy một mình** — D5 |
| `ShellViewModel` | thi hành 3 `Placement`, no-op khi trùng đỉnh, `EnsureModule` → cài → replay, `Failed` → `ShowMessage` | ✅ |
| Thủ công | `adb shell am start -W -a android.intent.action.VIEW -d "myapp://settings/profile" <pkg>` | — |

### 9.3 Mason brick

`mvi_feature` đã tự wire `settings.gradle.kts` + `AppRoutes` + Hilt `@IntoSet` (`bricks/mvi_feature/hooks/post_gen.dart`, 1031 dòng). Thêm vào đúng mạch đó:

- Sinh `{{Name}}DeepLinkResolver` chạy được ngay (resolve màn gốc của feature).
- Append một dòng vào `AppDeepLinks.entryPoints` — **cùng cơ chế** nó đang append vào `AppRoutes`.
- `--delivery on-demand`: resolver trả qua `FeatureEntry.resolver()` thay vì Hilt `@IntoSet`; dòng entry point mang thêm `dynamicModule = "<name>"`.
- `remove_feature`: gỡ dòng entry point.

**Không có biến brick mới** — deeplink là mặc định, không phải tuỳ chọn. Feature nào không muốn thì xoá resolver.

### 9.4 Scheme phải đổi tên được

Template không được hard-code `myapp://`. `AppConfig` thêm `deepLinkScheme` + `appLinkHost` → đẩy xuống `manifestPlaceholders`; `scripts/rename_project.sh` cập nhật cùng lúc với `applicationId` / `namespace`. Người clone chạy đúng một lệnh sẵn có là có scheme riêng.

---

## 10. Module bị đụng

| Module | Thay đổi |
|---|---|
| `:packages:platform` | **Mới**: package `deeplink/` (`DeepLink`, `DeepLinkParser`, `DeepLinkResolver`, `DeepLinkTarget`, `Placement`, `DeepLinkGuard`, `GuardVerdict`, `DeepLinkRouter` + `DefaultDeepLinkRouter` internal, `AuthDeepLinkGuard`, `PendingDeepLinkStore`, `NavigationCommand`, `FailureReason`), `AppDeepLinks`. **Sửa**: `FeatureEntry` (+1 method có default), `AppEvent` (+`UserLoggedIn`) |
| `:app` | Manifest (`intent-filter` `ACTION_VIEW`, `singleTop`, `manifestPlaceholders`), `MainActivity` (`onNewIntent` + dispatch + đánh dấu tiêu thụ), `DeepLinkIntentFactory`, Hilt binding router + guard set |
| `:shell` | `ShellViewModel` collect + thi hành `NavigationCommand`; `ShellState` (2 boolean → 2 `Set<String>`); `ShellScreen` đọc `readyModules`; `ShellEvent.ShowMessage` |
| `:features:settings` | `SettingsDeepLinkResolver` (mẫu install-time) |
| `:features:scanner` | `ScannerFeatureEntry.resolver()` (mẫu DFM) |
| `:konsist-test` | K10 |
| `bricks/` | `mvi_feature/hooks/post_gen.dart`, `remove_feature` |
| `buildSrc` + `scripts/` | `AppConfig` placeholders, `rename_project.sh` |

---

## 11. Lộ trình

Nguyên tắc kế thừa từ epic nền: **app build & chạy được ở MỌI bước**; mỗi phase là một PR review được.

### Phase 1 — Contract + parser (không đổi hành vi)
- Package `deeplink/` ở `:platform`: toàn bộ type ở §5, `DefaultDeepLinkRouter`, `AuthDeepLinkGuard`, `PendingDeepLinkStore`.
- `AppDeepLinks` với hai entry point hiện có.
- `FeatureEntry.resolver()` default; `AppEvent.UserLoggedIn`.
- Unit test §9.2 cho parser / guard / store / router.
- **Ra**: `:platform` có engine đầy đủ, chưa ai gọi. `assembleDebug` xanh, app chạy y hệt.

### Phase 2 — Thi hành ở host
- `ShellState` refactor (2 boolean → 2 `Set<String>`), `ShellScreen` đọc `readyModules`.
- `ShellViewModel` collect `commands`, thi hành 3 `Placement` + `EnsureModule` + `Failed`.
- `ShellEvent.ShowMessage` + hiển thị.
- Test `:shell` gồm cả contract test `tab` index.
- **Ra**: gọi `dispatch(...)` từ code là điều hướng đúng. Deeplink nội bộ (nguồn 4) đã chạy.

### Phase 3 — Cửa vào Intent
- Manifest: `intent-filter` `ACTION_VIEW` (custom scheme + App Links), `singleTop`, `manifestPlaceholders`.
- `AppConfig` + `rename_project.sh`.
- `MainActivity`: `onCreate` + `onNewIntent` + đánh dấu tiêu thụ.
- `DeepLinkIntentFactory` cho push.
- **Ra**: `adb shell am start -a android.intent.action.VIEW -d "myapp://settings/profile"` mở đúng màn, cả cold lẫn warm start.

### Phase 4 — Feature resolver + DFM + governance
- `SettingsDeepLinkResolver` (install-time mẫu).
- `ScannerFeatureEntry.resolver()` (DFM mẫu) + nghiệm thu nhánh `EnsureModule` bằng `bundletool --local-testing`.
- Konsist K10.
- Brick `mvi_feature` / `remove_feature`.
- Tài liệu: `docs/architecture/ARCHITECTURE.md` thêm mục deeplink; ghi chú bảo mật §8; hướng dẫn `assetlinks.json`.
- **Ra**: `mason make mvi_feature --name payments` sinh feature có deeplink chạy ngay; K10 xanh; tiêu chí 2.1 đạt đủ.

---

## 12. Giả định · Rủi ro · Phụ thuộc

**Giả định**
- Cơ chế navigation host giữ nguyên tuyệt đối (navigation3 + `Navigator` + `NestedNavigator` + `NavDisplay` + Hilt `@IntoSet`).
- Hilt vẫn phẳng một `SingletonComponent`.
- `ShellViewModel` inject được `Navigator` (`ViewModelComponent` là con của `ActivityRetainedComponent`) — **cần verify ở Phase 2, task đầu tiên**.

**Rủi ro**

| Rủi ro | Giảm thiểu |
|---|---|
| `ShellViewModel` không inject được `Navigator` như giả định | Verify ngay task đầu Phase 2. Nếu sai: `MainActivity` collect riêng nhánh `OpenFullScreen`, shell collect phần còn lại — vẫn chạy, chỉ mất tính "một chỗ thi hành" |
| `singleTop` đổi hành vi Activity, có thể lộ bug tiềm ẩn về state | Phase 3 là PR riêng; test tay cold/warm/rotate/background-restore trước khi merge |
| Guard không idempotent → hành vi lạ ở pre-gate | KDoc bắt buộc; unit test idempotency trong bộ test guard (§9.2) |
| `EnsureModule` → replay tạo vòng lặp nếu cài xong mà `readyModules` không cập nhật | Router giữ `Set<String>` các module đã phát `EnsureModule` trong phiên; module nào đã nằm trong set thì vòng hai **không** phát lại mà đi thẳng xuống bước resolve — không có resolver thì `Failed(InstallFailed)`. Replay tối đa một lần cho mỗi module. |
| Va chạm pattern trong cùng một feature | Ngoài phạm vi engine (§4.4) — test cục bộ của feature; ghi rõ trong doc brick |
| App Links không verify được trên máy dev | Ghi rõ đây là hành vi OS; `myapp://` luôn dùng được cho dev/test |
| `:platform` phình thêm một package lớn | Package `deeplink/` tự chứa, không class nào ngoài nó phụ thuộc chi tiết bên trong; chỉ contract lộ ra |

**Phụ thuộc**
- Không phụ thuộc epic đang chạy nào khác.
- Không cần secret / dịch vụ ngoài cho Phase 1–3. Phase 4 cần `bundletool --local-testing` (đã có trong CI của epic nền).
- Cần chốt scheme + host mặc định cho template trước Phase 3 (đề xuất: `myapp` và `app.example.com`, để `rename_project.sh` đổi).

---

## 13. Self-review

- **Placeholder**: không còn TBD/TODO.
- **Mâu thuẫn**: §5.3 nói mặc định `placement` là `InTab`; §5.3 cuối và §7.4 nói `tab == null` → `RootFullScreen`. Nhất quán: `InTab` là mặc định *khi feature có tab*; không có tab thì không có `InTab` hợp lệ để dẫn xuất. Đã ghi rõ ở cả hai chỗ.
- **Scope**: đủ lớn → epic, 4 phase. Mỗi phase ra `task_*.md` qua `epic-designer`.
- **Nhập nhằng đã đóng**: (a) "guard chạy mấy lần" → hai lần khi `entryPoint.requiresAuth`, một lần còn lại; guard phải idempotent (§6.3). (b) "backstack replace hay append" → replace, trừ khi trùng đỉnh thì no-op (§7.1). (c) "link hỏng nhảy đi đâu" → không nhảy đâu cả; mặc định của shell chính là fallback (§7.2).
- **Helicopter view**: engine không tạo đường biên module mới — nó chạy trên đúng đường biên `:platform` mà K9 đã ép và CI đã canh. `:shell` là nơi thi hành vì nó là nơi duy nhất sở hữu backstack. `:app` chỉ là cửa Intent. Không tầng nào biết nhiều hơn mức cần.
- **YAGNI**: đã loại KSP typed-path (§4.2), FCM service, deferred deep link, hosting `assetlinks.json`, và việc bọc `AppEventBus`/`Navigator` (§3).
