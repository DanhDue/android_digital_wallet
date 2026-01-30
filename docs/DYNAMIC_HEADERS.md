# 📁 Technical Summary: Dynamic Headers Strategy

**Project:** Android_Digital_Wallet
**Architecture:** Hybrid Network (Multi-module with Hilt/Retrofit)

---

## 1. Giới thiệu (Overview)

Giải pháp **Dynamic Headers** sử dụng cơ chế dán nhãn `@Tag` của Retrofit kết hợp với `GlobalHeaderInterceptor` duy nhất tại lớp Framework.
Mục tiêu là tách biệt cấu hình mạng chung và nhu cầu định danh riêng biệt của từng Feature (Auth, Payment, Wallet...).

---

## 2. Lợi ích phía Backend (BE) & Infrastructure

Đối với hệ thống Backend quy mô lớn, Dynamic Header đóng vai trò là "chỉ dấu điều hướng" cốt lõi.

| Lợi ích            | Chi tiết áp dụng                                                                                                            |
|:-------------------|:----------------------------------------------------------------------------------------------------------------------------|
| **Smart Routing**  | Giúp **API Gateway** nhận diện request thuộc tính năng nào để điều hướng (route) đến đúng cụm microservices tương ứng.      |
| **Rate Limiting**  | Thiết lập giới hạn số lượng request (Throttling) linh hoạt (ví dụ: Module Auth có tần suất thấp hơn nhưng bảo mật cao hơn). |
| **Priority Queue** | Ưu tiên xử lý các request quan trọng (Thanh toán, Chuyển tiền) và tạm hoãn các request ít ưu tiên hơn khi hệ thống quá tải. |
| **Observability**  | Dễ dàng phân tích log (Kibana/Grafana) để biết chính xác Module nào trên Mobile đang gây lỗi nhiều nhất.                    |
| **Cost Tracking**  | Thống kê chi phí hạ tầng Cloud chi tiết cho từng đơn vị kinh doanh (Business Unit) dựa trên tag gắn kèm request.            |

---

## 3. Lợi ích phía Mobile App (Android Client)

Về phía ứng dụng, giải pháp này giúp tối ưu hóa quản lý mã nguồn và hiệu suất vận hành.

| Lợi ích                   | Chi tiết áp dụng                                                                                                                           |
|:--------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------|
| **Centralized Logic**     | Mọi logic thêm Header được tập trung tại một chỗ duy nhất ở `libraries:framework`, giảm thiểu code dư thừa (DRY).                          |
| **Clean Architecture**    | Tuân thủ nguyên tắc **Separation of Concerns**. Các Feature chỉ cần dán nhãn nhu cầu, không cần biết cách thức thực thi Network bên dưới.  |
| **Resource Optimization** | Tái sử dụng một `OkHttpClient` duy nhất (Shared Connection Pool) giúp tiết kiệm RAM và giảm độ trễ khi tạo kết nối.                        |
| **Context-Awareness**     | Framework có thể xử lý thông minh dựa trên tag (ví dụ: Tự động retry 3 lần nếu là request Thanh toán, nhưng không retry nếu là Quảng cáo). |
| **Scalability**           | Dễ dàng mở rộng thêm hàng chục Feature mới mà không gây xung đột mã nguồn (Merge Conflict) tại Network Layer.                              |

---

## 4. Ví dụ kịch bản thực tế

Trong dự án **Android_Digital_Wallet**, việc dán nhãn giúp phân biệt rõ ràng hành vi hệ thống:

* **Authentication Module:** - Tag: `FeatureConfig(appId = "wallet_auth")`
    - Hành vi: BE áp dụng bảo mật WAF gắt gao; Mobile tự động điều hướng về màn hình Login nếu nhận mã 401.
* **Payment Module:** - Tag: `FeatureConfig(appId = "wallet_payment")`
    - Hành vi: BE ưu tiên luồng xử lý nhanh nhất; Mobile cấu hình `Long Timeout` để chờ xác nhận giao dịch.

---

## 5. Kết luận

Sử dụng Dynamic Header không chỉ là một kỹ thuật lập trình, mà là một **tư duy thiết kế hệ thống** (System Design). Nó giúp dự án Android
chuẩn hóa được hạ tầng mạng, sẵn sàng cho việc mở rộng quy mô kỹ sư và tính năng mà không phát sinh nợ kỹ thuật (Technical Debt).
