# KMP Project — Compose Multiplatform

## Tech Stack
- **Kotlin Multiplatform** — shared module for domain, data, and UI
- **Compose Multiplatform** — UI shared between Android and iOS
- **Navigation Compose (Jetbrains)** — cross-platform navigation in shared (`org.jetbrains.androidx.navigation`)
- **Koin** — multiplatform dependency injection
- **StateFlow + ViewModel KMP** — state management (`androidx.lifecycle:lifecycle-viewmodel`)
- **SQLDelight** — cross-platform database
- **Ktor Client** — HTTP calls from shared module
- **Ktor Server** — separate backend module
- **Kotlin Coroutines + Flow** — async and reactive streams

## Architecture

### Client (shared module)
Clean Architecture with 3 layers:
```
shared/src/commonMain/kotlin/
├── domain/     → models, use cases, repository interfaces (pure Kotlin)
├── data/       → repository implementations, SQLDelight, Ktor client
└── ui/         → Compose MP screens, ViewModels, navigation
```

### Server (server module)
Feature-based architecture — no layering inside features:
```
server/src/main/kotlin/
├── plugins/    → Ktor global config
├── auth/       → OAuth2 + JWT (transversal feature)
└── {feature}/  → routes, service, repository, models all together
```

Platform modules (androidApp, iosApp) contain only:
- CMP entry point
- Platform-specific `actual` implementations
- Native configuration (manifest, permissions, etc.)

## Available Agents
- `shared-domain-agent` — domain models, use cases, repository interfaces
- `shared-data-agent` — SQLDelight, Ktor client, repository implementations
- `shared-ui-agent` — Compose MP screens, ViewModels, Navigation
- `android-agent` — Android entry point, actual implementations, manifest
- `ios-agent` — iOS entry point, actual implementations, AppDelegate
- `ktor-server-agent` — feature-based endpoints, routing, authentication
- `test-agent` — unit tests for all layers
- `review-agent` — read-only code review before PR

## Global Rules
- Shared module NEVER imports `android.*` or `UIKit`
- Koin modules are defined in shared; platforms only initialize them
- All Repository interfaces live in the domain layer
- ViewModels live in shared/ui — never in platform modules
- Complete navigation graph lives in shared/ui
- No `LocalContext.current` in commonMain — use expect/actual
