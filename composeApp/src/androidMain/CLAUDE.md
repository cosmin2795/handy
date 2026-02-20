# Android App Module

## Role
This module contains ONLY:
- `MainActivity` — CMP entry point
- `Application` class with Koin initialization
- `actual` implementations in `shared/src/androidMain/`
- `AndroidManifest.xml` — permissions and features
- Android-specific Gradle config (compileSdk, minSdk, buildTypes)

## What NOT to do here
- Do NOT write Composables in this module — all UI lives in shared/ui
- Do NOT write ViewModels here
- Do NOT write business logic here

## Required Patterns

### MainActivity
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App() // composable from shared module
        }
    }
}
```

### Application + Koin
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                appModule,    // from shared
                dataModule,   // from shared
                domainModule, // from shared
                uiModule      // from shared
            )
        }
    }
}
```

### actual implementations (in shared/src/androidMain/)
```kotlin
actual fun getPlatformName(): String = "Android"
```

## Strict Rules
- Koin modules are defined in shared — Android only initializes them
- `actual` implementations live in `shared/androidMain/`, NOT in `androidApp/`
- Always call `enableEdgeToEdge()` in MainActivity
