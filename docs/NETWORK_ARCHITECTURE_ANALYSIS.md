# 📘 Network Architecture Analysis: The Triple-Threat Solution
**Project:** Android_Digital_Wallet  
**Level:** Senior Architect  
**Tech Stack:** Hilt, Retrofit, OkHttp, Coroutines

---

## 1. Hybrid Dependency Injection (DI) Strategy
**Cơ chế:** Cung cấp `Retrofit.Builder` tại `:libraries:framework` và hoàn thiện (build) thực thể `Retrofit` tại từng Feature Module.

### ✅ Pros (Ưu điểm)
- **Tái sử dụng tối đa:** Dùng chung các thành phần nặng (OkHttpClient, Connection Pool, Converters).
- **Linh hoạt:** Mỗi Feature (Auth, Payment) có thể có Base URL và Interceptor riêng.
- **Tính đóng gói:** Framework sạch sẽ, không chứa thông tin đặc thù của tính năng.

---

## 2. Dynamic Headers (via @Tag)
**Cơ chế:** Sử dụng Annotation `@Tag` trong Retrofit để đính kèm `FeatureConfig`. Interceptor tại Framework sẽ đọc tag này để ghi Header tương ứng.

### ✅ Pros (Ưu điểm)
- **Centralized Control:** Dễ dàng thay đổi chuẩn Header cho toàn bộ App tại một nơi.
- **BE Optimization:** Hỗ trợ API Gateway định tuyến (Routing) và giám sát (Monitoring).
- **Clean Code:** Loại bỏ việc khai báo Header thủ công lặp đi lặp lại.

---

## 3. Custom CallAdapter: The Infrastructure Transformer
**Cơ chế:** Lớp trung gian can thiệp vào quá trình chuyển đổi từ kết quả thô của OkHttp thành kiểu dữ liệu có cấu trúc `NetworkResponse<T>`.

### 🎯 3 Trụ cột cốt lõi
#### 3.1. Thay đổi "Kiểu trả về" (Return Type Transformation)
Retrofit mặc định trả về `Call<T>`. CallAdapter đóng vai trò là "bộ thông dịch", cho phép bạn khai báo các hàm API cực kỳ tường minh:
`suspend fun getBalance(): NetworkResponse<BalanceDto>`
Lúc này, dữ liệu trả về không còn là một "lời hứa" đơn thuần mà là một **Trạng thái (State)** đã được định nghĩa rõ ràng.

#### 3.2. Tập trung hóa xử lý lỗi (Centralized Error Handling)
- **Xử lý Exception:** Tự động bắt `IOException` (mất mạng) và chuyển thành `NetworkError`.
- **Phân tách mã lỗi HTTP:** Tự động parse `errorBody` từ Server thành `ApiError` chứa message nghiệp vụ.
- **Tính nhất quán:** Mọi Feature đều có cấu trúc dữ liệu lỗi giống hệt nhau, giúp việc xử lý ở UI đồng bộ.

#### 3.3. Đơn giản hóa tầng Repository (Boilerplate Elimination)
Loại bỏ hoàn toàn sự phụ thuộc vào khối `try-catch`. Code tại Repository chỉ còn là logic điều hướng dữ liệu thông qua cấu trúc `when (result)`.

---

## 4. NetworkResponse Extension Functions

`NetworkResponse` cung cấp các extension functions để xử lý kết quả một cách fluent và type-safe:

### 4.1. Transformation Functions

```kotlin
// Map success body to another type
fun <R> map(transform: (T) -> R): NetworkResponse<R>

// Fold to a single result type
fun <R> fold(onSuccess: (T) -> R, onError: (Throwable) -> R): R
```

### 4.2. Side-Effect Functions (Chainable)

```kotlin
// Execute action on success
fun onSuccess(action: (T) -> Unit): NetworkResponse<T>

// Execute action on any error
fun onError(action: (Throwable) -> Unit): NetworkResponse<T>
```

### 4.3. Bridge to DataState

```kotlin
// Simple conversion
fun <T> NetworkResponse<T>.toDataState(): DataState<T>

// With domain transformation (most common)
fun <T, R> NetworkResponse<T>.toDataState(transform: (T) -> R): DataState<R>

// With suspend transformation (for caching)
suspend fun <T, R> NetworkResponse<T>.toDataStateSuspend(transform: suspend (T) -> R): DataState<R>
```

---

## 5. Simplified Repository Pattern

### Before (40+ lines)
```kotlin
return when (val response = remoteDataSource.login(request)) {
    is NetworkResponse.Success -> DataState.Success(response.body.toDomain())
    is NetworkResponse.ApiError -> DataState.Error(Exception(response.body.toString()))
    is NetworkResponse.NetworkError -> DataState.Error(response.error)
    is NetworkResponse.UnknownError -> DataState.Error(response.error ?: Exception("Unknown error"))
}
```

### After (1 line)
```kotlin
return remoteDataSource.login(request).toDataState { it.toDomain() }
```

### Usage Examples

```kotlin
// Basic API call with transformation
override suspend fun getUser(id: String): DataState<User> =
    remoteDataSource.getUser(id).toDataState { it.toDomain() }

// With side effects (logging, analytics)
val result = remoteDataSource.fetchData()
    .onSuccess { data -> analytics.logSuccess(data) }
    .onError { error -> logger.logError(error) }
    .toDataState { it.toDomain() }

// Using fold for custom handling
val uiState = networkResponse.fold(
    onSuccess = { UiState.Content(it) },
    onError = { UiState.Error(it.message) }
)
```

---

## 6. Luồng đi của dữ liệu (Data Flow Lifecycle)

Để hiểu tại sao hệ thống này an toàn, hãy nhìn vào hành trình của một Request:

1. **Trigger:** ViewModel gọi `repository.getData()`.
2. **Call Initiation:** Retrofit khởi tạo request thông qua OkHttp.
3. **Execution:** Request đi qua `GlobalHeaderInterceptor` (đọc `@Tag` để gắn Header).
4. **Raw Response:** OkHttp nhận kết quả thô (hoặc gặp lỗi kết nối).
5. **The Magic (CallAdapter Step):** - Nếu OkHttp ném `SocketTimeoutException` -> CallAdapter "túm" lấy và bọc vào `NetworkResponse.NetworkError`.
   - Nếu Server trả về `200 OK` -> CallAdapter bọc dữ liệu vào `NetworkResponse.Success(data)`.
   - Nếu Server trả về `401/500` -> CallAdapter parse JSON lỗi và bọc vào `NetworkResponse.ApiError`.
6. **Bridge to Domain:** Repository sử dụng `toDataState { dto.toDomain() }` để chuyển đổi sang domain layer.
7. **Delivery:** Kết quả cuối cùng là một Object `DataState` được trả về cho ViewModel. **Không có Exception nào bị lọt ra ngoài.**

---

## 7. Error Type Hierarchy

Hệ thống cung cấp các exception types rõ ràng:

| Exception Type | Mô tả |
|:---|:---|
| `ApiException(code, message)` | Lỗi HTTP từ server (4xx, 5xx) |
| `UnknownNetworkException` | Lỗi không xác định |
| `Failure.ConnectivityError` | Mất kết nối mạng |
| `Failure.SocketTimeoutError` | Timeout |
| `Failure.UnAuthorizedException` | 401 Unauthorized |

Utility function để convert HTTP code sang Failure:
```kotlin
fun httpCodeToFailure(code: Int, message: String?): Failure
```

---

## 📊 Tổng kết kiến trúc

| Tính năng | Giải pháp truyền thống | Giải pháp Đề xuất (Senior) |
| :--- | :--- | :--- |
| **Xử lý lỗi** | `try-catch` rời rạc tại Repository. | **Automated:** Phân loại lỗi tập trung. |
| **Dòng dữ liệu** | Dữ liệu thô, dễ crash. | **Safe Flow:** Luôn trả về State (Success/Error). |
| **Repository Code** | 40+ dòng xử lý `when` expression. | **1 dòng:** `toDataState { it.toDomain() }` |
| **Mở rộng** | Phức tạp, dễ gây lỗi dây chuyền. | **Scalable:** Thêm feature mới cực nhanh. |

---

## 📁 Package Structure

```
network/
├── calladapter/
│   ├── NetworkResponse.kt           # Sealed class + extension functions
│   ├── NetworkResponseAdapter.kt    # Retrofit CallAdapter
│   ├── NetworkResponseAdapterFactory.kt
│   ├── NetworkResponseCall.kt       # Call wrapper
│   └── NetworkResponseExtensions.kt # Bridge to DataState
├── environment/
│   ├── Environment.kt
│   └── EnvironmentInterceptor.kt
├── interceptor/
│   ├── GlobalHeaderInterceptor.kt
│   └── HttpRequestInterceptor.kt
├── model/
│   └── FeatureConfig.kt
├── moshi/
│   └── EnumValueJsonAdapter.kt
├── ApiCallExtension.kt
├── DataState.kt                     # Domain layer result wrapper
├── HandleError.kt                   # Failure hierarchy + utilities
├── HttpStatusCode.kt
└── NetworkHelper.kt                 # OkHttp/Retrofit factories
```

---