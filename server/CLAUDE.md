# Ktor Server Module

## Architecture: Feature-Based
Each feature is a self-contained folder containing routes, service, repository, and models.
No layering inside features — everything for a feature lives together.

## Folder Structure
```
server/src/main/kotlin/
├── Application.kt              → entry point, plugin installation
├── plugins/
│   ├── Routing.kt              → installs all feature routes
│   ├── Security.kt             → JWT config
│   ├── Serialization.kt
│   ├── CORS.kt
│   └── StatusPages.kt          → centralized error handling
├── auth/                       → transversal OAuth2 + JWT feature
│   ├── AuthRoutes.kt
│   ├── AuthService.kt
│   └── model/
│       ├── AuthRequest.kt
│       └── TokenResponse.kt
├── user/                       → example feature
│   ├── UserRoutes.kt           → HTTP routes
│   ├── UserService.kt          → business logic
│   ├── UserRepository.kt       → DB access
│   └── model/
│       ├── UserRequest.kt
│       └── UserResponse.kt
└── common/
    └── model/
        └── ApiResponse.kt      → standard response wrapper
```

## Standard Response Format
```kotlin
@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val error: String? = null,
    val success: Boolean = error == null
)
```

## OAuth2 Flow (Google / Facebook)
```
Client → GET /auth/google
       → Ktor redirects to Google OAuth
       → Google callback with access token
       → Ktor fetches user info from Google
       → Create or find user in DB
       → Issue own JWT (stateless)
       → Client stores JWT
       → All subsequent requests: Authorization: Bearer {JWT}
```

## Routing Convention
```kotlin
// UserRoutes.kt
fun Route.userRoutes(userService: UserService) {
    route("/users") {
        authenticate("jwt") {
            get("/{id}") {
                val id = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest,
                        ApiResponse<Unit>(error = "Missing id"))
                val user = userService.getUser(id)
                call.respond(ApiResponse(data = user))
            }
            put("/{id}") {
                val id = call.parameters["id"]
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<UpdateUserRequest>()
                userService.updateUser(id, request)
                call.respond(ApiResponse(data = "Updated"))
            }
        }
    }
}
```

## StatusPages — Centralized Error Handling
```kotlin
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest,
                ApiResponse<Unit>(error = cause.message))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError,
                ApiResponse<Unit>(error = "Internal server error"))
        }
    }
}
```

## Strict Rules
- Routes organized by feature, NOT by type (no `/routes/GetUserRoute.kt`)
- Routes call service — routes NEVER access DB directly
- All private routes have `authenticate("jwt")`
- Error handling is centralized in StatusPages — no try-catch in individual routes
- Request and Response models are separate (never reuse the same model for both)
- Service layer contains business logic — repository only does DB access
