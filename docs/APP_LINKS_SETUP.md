# Android App Links Setup Guide

This guide describes how to configure, deploy, and verify **Android App Links** (`https://`) in the Android Super App Template.

---

## 1. App Links vs. Custom Schemes

The template supports two URI surfaces configured in `app/src/main/AndroidManifest.xml`:

| Type | URI Format | Ownership Verification | Hijack Risk | Use Case |
|---|---|---|---|---|
| **Custom Scheme** | `${deepLinkScheme}://...` (default: `myapp://`) | None (any installed app can declare the scheme) | High | Internal testing, local debugging, development |
| **Android App Links** | `https://${appLinkHost}/...` (default: `https://app.example.com/`) | Cryptographic verification via Digital Asset Links (`assetlinks.json`) | None (OS grants routing exclusively to verified app) | Production traffic, sensitive flows, auth callbacks, marketing links |

> [!WARNING]
> Because custom schemes can be claimed by malicious apps, sensitive transactions, authentication callbacks, or account modifications must **only** be exposed via verified HTTPS App Links.

---

## 2. Digital Asset Links (`assetlinks.json`)

To enable seamless routing without showing the Android disambiguation dialog, your web domain must host a statement list file at:

```
https://<your-domain>/.well-known/assetlinks.json
```

### Requirements:
- Served over HTTPS with a valid SSL/TLS certificate.
- HTTP status code `200 OK` (no redirects: HTTP 301/302 will fail verification).
- `Content-Type: application/json`.

### File Format:

```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "com.danhdue.androiddigitalwallet",
      "sha256_cert_fingerprints": [
        "14:6D:E9:7F:0E:52:D7:1E:27:52:83:B6:B7:A0:C6:42:F0:FC:CF:11:47:26:4F:43:C7:23:4C:45:AD:2E:65:DA"
      ]
    }
  }
]
```

Replace `package_name` with your app's package name (`applicationId`) and `sha256_cert_fingerprints` with your release (or debug) signing certificate fingerprint.

### Extracting SHA-256 Fingerprint:

From your debug keystore:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Or via Gradle:
```bash
./gradlew :app:signingReport
```

Look for the `SHA256:` fingerprint line and copy the uppercase colon-separated hex string.

---

## 3. Template Configuration & Project Rename

### Centralized Configuration (`buildSrc/src/main/kotlin/AppConfig.kt`)

The scheme and host are defined as constants in `buildSrc`:
```kotlin
object AppConfig {
    const val deepLinkScheme = "myapp"
    const val appLinkHost = "app.example.com"
}
```

These values are injected into the manifest via `manifestPlaceholders` in `app/build.gradle.kts`:
```kotlin
manifestPlaceholders["deepLinkScheme"] = AppConfig.deepLinkScheme
manifestPlaceholders["appLinkHost"] = AppConfig.appLinkHost
```

### Renaming via `rename_project.sh`

When initializing a new project from this template:
```bash
scripts/rename_project.sh <app_name> <bundle_id>
```
The rename script automatically updates:
- `deepLinkScheme` to `<app_name>` (e.g. `acme_wallet`)
- `appLinkHost` to `<app_name>.example.com` (e.g. `acme.example.com`)
- All doc and code references

> [!NOTE]
> The template defaults to `app.example.com`. Since this domain does not host your app's `assetlinks.json`, App Links domain verification will report `legacy_failure` or `1024` on test devices until you configure your production domain. Custom scheme links (`myapp://`) remain fully functional during development.

---

## 4. Verification & Testing via ADB

### Checking Domain Verification Status

To inspect domain verification state on Android 12+ (API 31+):
```bash
adb shell pm get-app-links com.danhdue.androiddigitalwallet
```

Expected output for an unverified test domain:
```
com.danhdue.androiddigitalwallet:
    ID: 01234567-89ab-cdef-0123-456789abcdef
    Signatures: [...]
    Domain verification state:
      app.example.com: 1024
```
Status `1024` (or `legacy_failure`) confirms the OS attempted verification against the placeholder domain. Once `assetlinks.json` is properly deployed on your real domain, the state changes to `verified`.

To manually approve a domain during development without hosting `assetlinks.json`:
```bash
adb shell pm set-app-links --package com.danhdue.androiddigitalwallet 1 app.example.com
```

### Simulating App Links via ADB

Test opening a deep link:
```bash
# App Link (HTTPS)
adb shell am start -a android.intent.action.VIEW \
  -c android.intent.category.BROWSABLE \
  -d "https://app.example.com/settings/profile"

# Custom Scheme
adb shell am start -a android.intent.action.VIEW \
  -d "myapp://settings/profile"

# DFM Split Module
adb shell am start -a android.intent.action.VIEW \
  -d "myapp://scanner"
```

---

## 5. Push Notifications & In-App Routing

### Push Notifications
The template provides `DeepLinkIntentFactory` in `:app`:
```kotlin
val pendingIntent = DeepLinkIntentFactory.createPendingIntent(
    context = context,
    uri = Uri.parse("myapp://settings/profile"),
    requestCode = NOTIFICATION_ID,
)
```
Pass this `pendingIntent` to `NotificationCompat.Builder.setContentIntent(pendingIntent)`.

### In-App Programmatic Dispatch
To trigger deep link navigation directly from code without creating an Android `Intent`:
```kotlin
@Inject lateinit var deepLinkRouter: DeepLinkRouter

// Dispatch URI
deepLinkRouter.dispatch("myapp://settings/profile")
```
