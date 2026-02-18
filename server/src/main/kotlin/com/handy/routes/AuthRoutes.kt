package com.handy.routes

import com.handy.auth.JwtConfig
import com.handy.auth.TokenService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

// ── Request / Response DTOs ──────────────────────────────────────────────────

@Serializable
data class GoogleAuthRequest(val idToken: String)

@Serializable
data class FacebookAuthRequest(val accessToken: String)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto,
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
)

// ── Google token verification payload ────────────────────────────────────────

@Serializable
data class GoogleTokenInfo(
    val sub: String,
    val email: String,
    val name: String,
    @SerialName("picture") val photoUrl: String? = null,
    val aud: String,
    val iss: String,
)

// ── Facebook user info payload ────────────────────────────────────────────────

@Serializable
data class FacebookUserInfo(
    val id: String,
    val name: String,
    val email: String? = null,
)

// ── Route definitions ─────────────────────────────────────────────────────────

fun Route.authRoutes() {
    val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    route("/auth") {

        /**
         * POST /auth/google
         * Body: { "idToken": "<google-id-token>" }
         *
         * Verifies the token against Google's tokeninfo endpoint, then issues
         * an app-level JWT.
         */
        post("/google") {
            val request = runCatching { call.receive<GoogleAuthRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
                return@post
            }

            val tokenInfo = runCatching {
                httpClient.get("https://oauth2.googleapis.com/tokeninfo") {
                    parameter("id_token", request.idToken)
                }.body<GoogleTokenInfo>()
            }.getOrElse {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid Google token"))
                return@post
            }

            val expectedAudience = System.getenv("GOOGLE_CLIENT_ID") ?: ""
            if (expectedAudience.isNotBlank() && tokenInfo.aud != expectedAudience) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token audience mismatch"))
                return@post
            }

            val userId = "google_${tokenInfo.sub}"
            val token = TokenService.generateToken(
                userId = userId,
                email = tokenInfo.email,
                name = tokenInfo.name,
            )

            call.respond(
                AuthResponse(
                    token = token,
                    user = UserDto(
                        id = userId,
                        name = tokenInfo.name,
                        email = tokenInfo.email,
                        photoUrl = tokenInfo.photoUrl,
                    ),
                ),
            )
        }

        /**
         * POST /auth/facebook
         * Body: { "accessToken": "<facebook-access-token>" }
         *
         * Verifies the token via Graph API, then issues an app-level JWT.
         */
        post("/facebook") {
            val request = runCatching { call.receive<FacebookAuthRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
                return@post
            }

            val fbUser = runCatching {
                httpClient.get("https://graph.facebook.com/me") {
                    parameter("access_token", request.accessToken)
                    parameter("fields", "id,name,email")
                }.body<FacebookUserInfo>()
            }.getOrElse {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid Facebook token"))
                return@post
            }

            val userId = "facebook_${fbUser.id}"
            val email = fbUser.email ?: "$userId@facebook.com"
            val token = TokenService.generateToken(
                userId = userId,
                email = email,
                name = fbUser.name,
            )

            call.respond(
                AuthResponse(
                    token = token,
                    user = UserDto(
                        id = userId,
                        name = fbUser.name,
                        email = email,
                    ),
                ),
            )
        }

        /**
         * GET /auth/me
         * Header: Authorization: Bearer <jwt>
         *
         * Returns the authenticated user extracted from the JWT.
         */
        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                val email = principal?.payload?.getClaim("email")?.asString()
                val name = principal?.payload?.getClaim("name")?.asString()

                if (userId == null || email == null || name == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                call.respond(
                    UserDto(
                        id = userId,
                        name = name,
                        email = email,
                    ),
                )
            }
        }
    }
}
