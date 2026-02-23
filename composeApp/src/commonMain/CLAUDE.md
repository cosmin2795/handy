# Shared Module — KMP + CMP

## Allowed in commonMain
- Pure Kotlin, Kotlin Coroutines, Flow
- Compose Multiplatform composables
- Ktor Client, SQLDelight
- Koin for DI
- ViewModel from `androidx.lifecycle:lifecycle-viewmodel` (KMP version)

## NOT allowed in commonMain
- Any `android.*` imports
- `UIKit`, `AppKit`, `Foundation` iOS imports
- `LocalContext.current` — Android only, use expect/actual instead
- `Context` as a parameter in shared functions
- `BlurMaskFilter` in Canvas (not supported on iOS)
- `FontFamily.SansSerif` directly — use expect/actual

## Folder Structure
```
shared/src/
├── commonMain/kotlin/com/myapp/
│   ├── feature/
│   │   ├── auth/
│   │   │   ├── domain/
│   │   │   │   ├── model/          → AuthUser, Credentials
│   │   │   │   ├── repository/     → AuthRepository interface
│   │   │   │   └── usecase/        → LoginUseCase, LogoutUseCase
│   │   │   ├── data/
│   │   │   │   ├── repository/     → AuthRepositoryImpl
│   │   │   │   ├── remote/         → AuthApiService
│   │   │   │   ├── mapper/         → AuthMapper
│   │   │   │   └── di/             → AuthDataModule
│   │   │   └── ui/
│   │   │       ├── LoginScreen.kt
│   │   │       ├── LoginViewModel.kt
│   │   │       └── di/             → AuthUiModule
│   │   ├── home/
│   │   │   ├── domain/
│   │   │   ├── data/
│   │   │   └── ui/
│   │   └── profile/
│   │       ├── domain/
│   │       ├── data/
│   │       └── ui/
│   ├── core/
│   │   ├── navigation/     → AppNavHost, Screen sealed class
│   │   ├── component/      → reusable composables (buttons, cards)
│   │   ├── network/        → Ktor client setup, interceptors
│   │   ├── database/       → SQLDelight driver setup
│   │   └── di/             → CoreModule (network, database)
├── androidMain/kotlin/     → Android actual implementations
└── iosMain/kotlin/         → iOS actual implementations
```

## Mandatory expect/actual for
- `getPlatformName(): String`
- Any native resource access (camera, GPS, etc.)
- Composables that differ visually per platform
- Koin initialization helpers
