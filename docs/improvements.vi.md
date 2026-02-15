# Cải Tiến Kiến Trúc & Hiện Đại Hóa

Tài liệu này phác thảo các cải tiến kiến trúc gần đây được thực hiện cho dự án `android_digital_wallet`, giải thích "cái gì" và "tại sao" cho mỗi thay đổi.

## 1. Version Catalogs (`libs.versions.toml`)

### Đã thay đổi gì?
Chúng tôi đã di chuyển các khai báo phụ thuộc từ `buildSrc/src/main/kotlin/Deps.kt` (hoặc các chuỗi được mã hóa cứng) sang Gradle Version Catalog tiêu chuẩn nằm tại `gradle/libs.versions.toml`.

### Tại sao?
- **Tiêu chuẩn hóa**: Version Catalogs là tiêu chuẩn được khuyến nghị để quản lý sự phụ thuộc trong Gradle.
- **Hiệu suất**: Không giống như `buildSrc`, các thay đổi trong `libs.versions.toml` không yêu cầu xây dựng lại logic build. Điều này tăng tốc độ đồng bộ hóa và thời gian build.
- **An toàn kiểu (Type Safety)**: Gradle tự động tạo các trình truy cập an toàn kiểu (ví dụ: `libs.androidx.core.ktx`) ngăn ngừa lỗi chính tả.
- **Tập trung**: Tất cả các phiên bản được xác định ở một nơi, giúp dễ dàng quản lý nâng cấp và đảm bảo các phiên bản nhất quán trên toàn bộ các module.
- **Hỗ trợ Dependabot**: Các công cụ như Dependabot và Renovate hỗ trợ version catalogs ngay lập tức để cập nhật phụ thuộc tự động.

## 2. CI/CD với GitHub Actions

### Đã thay đổi gì?
Chúng tôi đã giới thiệu quy trình Tích hợp Liên tục (CI) và Phân phối Liên tục (CD) sử dụng GitHub Actions, được định nghĩa trong `.github/workflows/ci.yml`.

### Tại sao?
- **Đảm bảo chất lượng**: Mọi yêu cầu kéo (pull request) và đẩy (push) đến các nhánh chính đều được tự động xác minh.
- **Tự động hóa**:
    - **Kiểm tra Chất lượng**: Chạy `./gradlew check` bao gồm:
        - **Kiểm thử Đơn vị**: `testDebugUnitTest`
        - **Linting**: `lintDebug`
        - **Detekt**: Phân tích mã tĩnh cho Kotlin.
        - **Spotless**: Xác minh định dạng mã.
    - **Xây dựng (Building)**: Chạy `assembleDebug` để xác minh ứng dụng biên dịch thành công.
- **Nhất quán**: Đảm bảo dự án được xây dựng trong môi trường sạch (sử dụng JDK 21), loại bỏ các vấn đề "nó hoạt động trên máy của tôi".

## 3. Composite Builds (`build-logic`)

### Đã thay đổi gì?
Chúng tôi đã cấu trúc lại logic build từ `buildSrc` thành một **Composite Build** tiêu chuẩn có tên là `build-logic`. Module này chứa các convention plugins của chúng tôi (ví dụ: `android-library.gradle.kts` được chuyển đổi thành một plugin).

### Tại sao?
- **Tách biệt & Cô lập**: `buildSrc` tự động thêm mã nguồn của nó vào classpath của *mọi* module trong dự án, dẫn đến sự liên kết chặt chẽ và làm mất đi tính gọn gàng của classpath. `build-logic` (dưới dạng composite build) hoàn toàn tách biệt; các plugin phải được áp dụng một cách rõ ràng, đảm bảo các module chỉ truy cập những gì chúng cần.
- **Hiệu suất Build (Lý do chính)**: `buildSrc` là một nút thắt cổ chai. Một thay đổi đối với *bất kỳ* dòng mã nào trong `buildSrc` sẽ làm mất hiệu lực bộ nhớ cache build cho **toàn bộ dự án**, buộc phải xây dựng lại tất cả. Với `build-logic`, các thay đổi được cô lập. Nếu bạn thay đổi một plugin Kotlin chung, chỉ các module sử dụng plugin đó mới được xây dựng lại. Điều này rất quan trọng để mở rộng quy mô.
- **Khả năng mở rộng**: Khi dự án phát triển, việc tách biệt logic build ngăn "build" trở thành một tập lệnh nguyên khối, khó bảo trì.

### Build Logic (Composite Build) so với buildSrc

| Tính năng | `buildSrc` | `build-logic` (Composite Build) |
| :--- | :--- | :--- |
| **Biên dịch (Compilation)** | Biên dịch lại khi có *bất kỳ* thay đổi nào trong `buildSrc`. | Chỉ biên dịch lại khi plugin cụ thể thay đổi. |
| **Vô hiệu hóa bộ nhớ đệm (Cache Invalidation)** | Vô hiệu hóa bộ nhớ đệm build của **toàn bộ dự án**. | Chỉ vô hiệu hóa các tác vụ phụ thuộc vào logic đã thay đổi. |
| **Classpath** | Tự động được thêm vào classpath của **tất cả** các module. | Phải được thêm vào module và áp dụng một cách rõ ràng. |
| **Phân tách mối quan tâm** | Có xu hướng trở thành "bãi rác" cho tất cả các tập lệnh build. | Khuyến khích mô-đun hóa logic build (ví dụ: các plugin riêng biệt cho Android, Kotlin, v.v.). |
| **Hiệu suất** | Kém đối với các dự án lớn do vô hiệu hóa thường xuyên. | Tuyệt vời, vì nó hoạt động như một dự án độc lập. |

**Tại sao chúng tôi chuyển đổi:**
`buildSrc` thuận tiện cho các dự án nhỏ nhưng trở thành nút thắt cổ chai khi dự án phát triển. Bằng cách chuyển sang `build-logic` (một composite build), chúng tôi đảm bảo rằng việc sửa đổi tập lệnh build không buộc phải xây dựng lại toàn bộ ứng dụng, giúp tiết kiệm đáng kể thời gian phát triển.

### Cách sử dụng?
1.  **Bao gồm Build**: Module `build-logic` được bao gồm trong `settings.gradle.kts` gốc:
    ```kotlin
    includeBuild("build-logic")
    ```
2.  **Áp dụng Plugins**: Trong các module tính năng hoặc thư viện của bạn (ví dụ: `libraries/framework/build.gradle.kts`), áp dụng các convention plugins sử dụng tên gọi có ý khơi gợi trong Version Catalog:
    ```kotlin
    plugins {
        alias(libs.plugins.danhdue.android.library)
        // hoặc
        alias(libs.plugins.danhdue.android.feature)
    }
    ```
    *Lưu ý: Các plugins này tự động cấu hình các cài đặt Android, tùy chọn Kotlin và các phụ thuộc chung.*

## 4. Chế độ Explicit API

### Đã thay đổi gì?
Chúng tôi đã kích hoạt **Explicit API mode** của Kotlin trong module `libraries/framework`.
*Hiện tại được đặt ở chế độ `warning` để tránh phá vỡ bản build do các vi phạm hiện có.*

### Tại sao?
- **Sự rõ ràng của Public API**: Buộc các nhà phát triển phải đánh dấu rõ ràng các lớp và hàm là `public`, `internal`, hoặc `private`.
- **Thiết kế Thư viện**: Quan trọng đối với các module thư viện (như `framework`). Nó đảm bảo chúng tôi không vô tình để lộ các chi tiết triển khai nội bộ cho các module tính năng.
- **Tương thích Nhị phân**: Giúp duy trì diện tích bề mặt API ổn định, điều này quan trọng hiệu quả đối với kiến trúc modular.
- **Chất lượng Mã**: Giảm nợ kỹ thuật bằng cách biến các quyết định về khả năng hiển thị thành một lựa chọn có ý thức thay vì mặc định.
