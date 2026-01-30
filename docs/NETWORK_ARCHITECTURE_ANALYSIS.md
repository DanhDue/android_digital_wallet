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

## 4. Luồng đi của dữ liệu (Data Flow Lifecycle)

Để hiểu tại sao hệ thống này an toàn, hãy nhìn vào hành trình của một Request:

1. **Trigger:** ViewModel gọi `repository.getData()`.
2. **Call Initiation:** Retrofit khởi tạo request thông qua OkHttp.
3. **Execution:** Request đi qua `GlobalHeaderInterceptor` (đọc `@Tag` để gắn Header).
4. **Raw Response:** OkHttp nhận kết quả thô (hoặc gặp lỗi kết nối).
5. **The Magic (CallAdapter Step):** - Nếu OkHttp ném `SocketTimeoutException` -> CallAdapter "túm" lấy và bọc vào `NetworkResponse.NetworkError`.
   - Nếu Server trả về `200 OK` -> CallAdapter bọc dữ liệu vào `NetworkResponse.Success(data)`.
   - Nếu Server trả về `401/500` -> CallAdapter parse JSON lỗi và bọc vào `NetworkResponse.ApiError`.
6. **Delivery:** Kết quả cuối cùng là một Object `NetworkResponse` được trả về cho Repository. **Không có Exception nào bị lọt ra ngoài.**



---

## 📊 Tổng kết kiến trúc

| Tính năng | Giải pháp truyền thống | Giải pháp Đề xuất (Senior) |
| :--- | :--- | :--- |
| **Xử lý lỗi** | `try-catch` rời rạc tại Repository. | **Automated:** Phân loại lỗi tập trung. |
| **Dòng dữ liệu** | Dữ liệu thô, dễ crash. | **Safe Flow:** Luôn trả về State (Success/Error). |
| **Mở rộng** | Phức tạp, dễ gây lỗi dây chuyền. | **Scalable:** Thêm feature mới cực nhanh. |

---