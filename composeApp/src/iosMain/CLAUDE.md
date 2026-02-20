# iOS App Module

## Role
This module contains ONLY:
- `iOSApp.swift` — SwiftUI entry point that boots CMP
- `AppDelegate` if required
- `actual` implementations in `shared/src/iosMain/` (Kotlin)
- `Info.plist` — iOS permissions
- CocoaPods / SPM configuration

## What NOT to do here
- Do NOT write UI in Swift — all UI is Compose MP in shared/ui
- Do NOT write business logic in Swift
- Do NOT duplicate logic that exists in shared

## Required Patterns

### Swift Entry Point
```swift
@main
struct iOSApp: App {

    init() {
        KoinInitKt.doInitKoin() // calls function from iosMain
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

### Koin init in iosMain (Kotlin)
```kotlin
// shared/src/iosMain/kotlin/KoinInit.kt
fun initKoin() {
    startKoin {
        modules(
            appModule,
            dataModule,
            domainModule,
            uiModule
        )
    }
}
```

### MainViewController in iosMain
```kotlin
// shared/src/iosMain/kotlin/MainViewController.kt
fun MainViewController() = ComposeUIViewController {
    App() // same App() composable as Android
}
```

### actual implementations (in shared/src/iosMain/)
```kotlin
actual fun getPlatformName(): String = "iOS"
```

## Strict Rules
- Koin is initialized from Kotlin (iosMain), NOT from Swift
- Swift contains MINIMUM code — just a bridge to CMP
- `actual` implementations live in `shared/iosMain/`, NOT in `iosApp/`
