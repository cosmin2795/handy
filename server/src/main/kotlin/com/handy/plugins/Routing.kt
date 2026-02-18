package com.handy.plugins

import com.handy.routes.authRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        authRoutes()
    }
}
