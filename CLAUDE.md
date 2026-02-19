# CLAUDE.md

## Project Overview

**Handy** is a Kotlin Multiplatform (KMP) mobile application targeting Android and iOS, built for the handyman services market. The app provides Google and Facebook OAuth login and communicates with a Ktor-based backend server that is also part of this repository.

| Attribute | Value |
|---|---|
| Repository | `cosmin2795/handy` |
| App Type | KMP mobile app + Ktor backend |
| UI Framework | Compose Multiplatform |
| DI | Koin |
| Async | Kotlin Coroutines |
| Backend | Ktor (server module) |
| Auth | Google Sign-In + Facebook Login (OAuth → JWT) |

---

## Repository Structure

```
handy/
├── CLAUDE.md                          # This file
├── .gitignore
├── settings.gradle.kts                # Project modules: composeApp, server
├── build.gradle.kts                   # Root build config
├── gradle/
│   ├── libs.versions.toml             # Version catalog (single source of truth for deps)
│   └── wrapper/
│       └── gradle-wrapper.properties
├── composeApp/                        # Shared Compose Multiplatform UI + business logic
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/com/handy/
│       │   ├── App.kt                 # Root composable
│       │   ├── di/AppModule.kt        # Koin modules
│       │   ├── auth/
│       │   │   ├── AuthRepository.kt  # expect interface
│       │   │   ├── AuthResult.kt      # Sealed class
│       │   │   └── AuthViewModel.kt   # Shared ViewModel
│       │   └── ui/
│       │       ├── login/LoginScreen.kt
│       │       └── theme/Theme.kt
│       ├── androidMain/kotlin/com/handy/
│       │   ├── MainActivity.kt
│       │   └── auth/AndroidAuthRepository.kt  # actual impl (Google + Facebook SDKs)
│       └── iosMain/kotlin/com/handy/
│           ├── MainViewController.kt
│           └── auth/IosAuthRepository.kt      # actual impl
├── server/                            # Ktor backend
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/handy/
│       ├── Application.kt             # Entry point
│       ├── plugins/
│       │   ├── Routing.kt
│       │   ├── Security.kt            # JWT config
│       │   └── Serialization.kt
│       └── routes/
│           └── AuthRoutes.kt          # /auth/google, /auth/facebook, /auth/me
└── iosApp/                            # iOS native entry point (Xcode project)
    ├── iosApp.xcodeproj/
    └── iosApp/
        ├── iOSApp.swift
        └── ContentView.swift
```

---

## Tech Stack & Key Dependencies

All dependency versions are centralized in `gradle/libs.versions.toml`.

| Library | Purpose | Module |
|---|---|---|
| `org.jetbrains.kotlin.multiplatform` | KMP plugin | all |
| `org.jetbrains.compose` | Compose Multiplatform UI | composeApp |
| `io.insert-koin:koin-core` | DI (common) | composeApp |
| `io.insert-koin:koin-android` | DI (Android) | composeApp |
| `io.insert-koin:koin-compose` | DI + Compose | composeApp |
| `org.jetbrains.kotlinx:kotlinx-coroutines-core` | Async (common) | composeApp |
| `io.ktor:ktor-client-core` | HTTP client (common) | composeApp |
| `io.ktor:ktor-client-okhttp` | HTTP client (Android) | composeApp |
| `io.ktor:ktor-client-darwin` | HTTP client (iOS) | composeApp |
| `io.ktor:ktor-server-core` | Ktor server | server |
| `io.ktor:ktor-server-netty` | Ktor engine | server |
| `io.ktor:ktor-server-auth-jwt` | JWT auth | server |
| `com.google.android.gms:play-services-auth` | Google Sign-In (Android) | composeApp |
| Facebook Login SDK | Facebook auth (Android) | composeApp |

---

## Architecture

### Auth Flow

```
Mobile (common) ──► AuthViewModel ──► AuthRepository (expect)
                                              │
               ┌──────────────────────────────┤
               │                              │
    AndroidAuthRepository              IosAuthRepository
    (Google/Facebook SDKs)             (Google/Facebook SDKs)
               │                              │
               └──────────────┬───────────────┘
                              │ ID token / access token
                              ▼
                    Ktor Server (/auth/google or /auth/facebook)
                              │ verifies token with provider
                              │ issues JWT
                              ▼
                    Mobile stores JWT ──► subsequent API calls
```

### Layer Responsibilities

- **`commonMain`**: Business logic, ViewModels, repository interfaces, UI screens (Compose)
- **`androidMain`/`iosMain`**: Platform-specific `actual` implementations (auth SDKs, platform utilities)
- **`server`**: JWT issuance, token verification against Google/Facebook APIs, user management

### Expect/Actual Pattern

Auth is the primary use of `expect`/`actual`. The common interface is declared in `commonMain`, and platform-specific implementations use native SDKs:

```kotlin
// commonMain
expect class AuthRepository {
    suspend fun signInWithGoogle(): AuthResult
    suspend fun signInWithFacebook(): AuthResult
    suspend fun signOut()
}

// androidMain
actual class AuthRepository { ... }  // uses Google Identity / Facebook SDK

// iosMain
actual class AuthRepository { ... }  // uses GoogleSignIn / FBSDKLoginKit
```

---

## Build & Run Commands

```bash
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Run Android unit tests
./gradlew :composeApp:testDebugUnitTest

# Run Ktor server (development)
./gradlew :server:run

# Run server tests
./gradlew :server:test

# Lint
./gradlew :composeApp:lintDebug

# Clean
./gradlew clean

# Build all
./gradlew build
```

For iOS, open `iosApp/iosApp.xcodeproj` in Xcode and run on simulator or device.

---

## Environment & Configuration

### Required (not committed)

| File | Purpose |
|---|---|
| `local.properties` | Android SDK path |
| `google-services.json` | Firebase/Google services config (Android) |
| `iosApp/GoogleService-Info.plist` | Firebase/Google services config (iOS) |
| `server/.env` or env vars | JWT secret, database URL, etc. |

### Server Environment Variables

```
JWT_SECRET=<secret>
JWT_ISSUER=https://handy.com
JWT_AUDIENCE=handy-app
GOOGLE_CLIENT_ID=<google-oauth-client-id>
FACEBOOK_APP_ID=<facebook-app-id>
FACEBOOK_APP_SECRET=<facebook-app-secret>
DATABASE_URL=<optional, for user persistence>
```

---

## Ktor Backend API

Base URL (local dev): `http://localhost:8080`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/google` | None | Exchange Google ID token for JWT |
| POST | `/auth/facebook` | None | Exchange Facebook access token for JWT |
| GET | `/auth/me` | Bearer JWT | Get current authenticated user |

### Request/Response Examples

**POST /auth/google**
```json
// Request
{ "idToken": "<google-id-token>" }

// Response 200
{ "token": "<jwt>", "user": { "id": "...", "name": "...", "email": "..." } }
```

**POST /auth/facebook**
```json
// Request
{ "accessToken": "<facebook-access-token>" }

// Response 200
{ "token": "<jwt>", "user": { "id": "...", "name": "...", "email": "..." } }
```

---

## Key Conventions for AI Assistants

1. **Never commit secrets.** `google-services.json`, `GoogleService-Info.plist`, `*.jks`, `*.keystore`, `local.properties`, `.env`, and any file containing API keys must never be staged.

2. **Dependency versions live in `gradle/libs.versions.toml` only.** Do not hardcode version strings in module `build.gradle.kts` files.

3. **Use `expect`/`actual` for platform-specific code.** Declare the interface in `commonMain` and implement in `androidMain` / `iosMain`.

4. **ViewModels are in `commonMain`.** They must not import any Android or iOS types directly.

5. **DI via Koin.** Register all dependencies in `composeApp/src/commonMain/kotlin/com/handy/di/AppModule.kt`. Use platform-specific modules for platform-specific bindings.

6. **Async via Coroutines.** No blocking calls on the main thread. Use `viewModelScope` or `CoroutineScope` tied to the component lifecycle.

7. **Package name**: `com.handy` across all modules.

8. **Server and client share data models** via the `shared` source set or by duplicating simple data classes if sharing is impractical.

9. **Branch naming**: AI agent branches use `claude/<session-id>`. Feature branches use `feature/<description>`. Never push directly to `master`.

10. **Update this file** when new modules, dependencies, or conventions are introduced.

---

## Development Notes

- The Ktor server verifies Google ID tokens using Google's public JWKS endpoint (`https://www.googleapis.com/oauth2/v3/certs`) — no Firebase Admin SDK required.
- Facebook access tokens are verified by calling `https://graph.facebook.com/me?access_token=<token>`.
- JWTs issued by the server use HS256 with the `JWT_SECRET` environment variable.
- The iOS project requires CocoaPods (or SPM) for Google Sign-In and Facebook SDK dependencies — see `iosApp/Podfile`.
