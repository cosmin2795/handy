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
├── commonMain/kotlin/
│   ├── domain/
│   │   ├── model/          → pure data classes
│   │   ├── repository/     → interfaces
│   │   └── usecase/        → use case classes
│   ├── data/
│   │   ├── repository/     → implementations
│   │   ├── local/          → SQLDelight DAOs
│   │   ├── remote/         → Ktor client services
│   │   ├── mapper/         → Entity/DTO → Domain model
│   │   └── di/             → Koin data module
│   └── ui/
│       ├── navigation/     → NavHost, routes, destinations
│       ├── screen/         → Screen composables + ViewModels
│       ├── component/      → reusable composables
│       └── di/             → Koin UI module
├── androidMain/kotlin/     → Android actual implementations
└── iosMain/kotlin/         → iOS actual implementations
```

## Mandatory expect/actual for
- `getPlatformName(): String`
- Any native resource access (camera, GPS, etc.)
- Composables that differ visually per platform
- Koin initialization helpers
