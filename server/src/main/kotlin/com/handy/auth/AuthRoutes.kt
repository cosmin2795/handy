package com.handy.auth

import com.handy.auth.model.FacebookAuthRequest
import com.handy.auth.model.GoogleAuthRequest
import com.handy.auth.model.UserDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {

        /**
         * POST /auth/google
         * Body: { "idToken": "<google-id-token>" }
         */
        post("/google") {
            val request = runCatching { call.receive<GoogleAuthRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
                return@post
            }

            val response = runCatching { authService.loginWithGoogle(request.idToken) }.getOrElse {
                val msg = if (it is IllegalArgumentException) it.message ?: "Unauthorized" else "Invalid Google token"
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to msg))
                return@post
            }

            call.respond(response)
        }

        /**
         * POST /auth/facebook
         * Body: { "accessToken": "<facebook-access-token>" }
         */
        post("/facebook") {
            val request = runCatching { call.receive<FacebookAuthRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
                return@post
            }

            val response = runCatching { authService.loginWithFacebook(request.accessToken) }.getOrElse {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid Facebook token"))
                return@post
            }

            call.respond(response)
        }

        /**
         * GET /auth/me
         * Header: Authorization: Bearer <jwt>
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

                call.respond(UserDto(id = userId, name = name, email = email))
            }
        }
    }
}
