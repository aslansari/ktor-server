package com.aslansari.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        get("/") {
            call.respond(BaseResponseDTO("title", "message"))
        }

        get("/api/v1/core/home") {
            call.respond(BaseResponseDTO("Horray!","Message is sent from ktor server"))
        }
        authenticate("auth-basic") {
            get("/api/v1/core/user/me") {
                call.respond(BaseResponseDTO("Horray!","Message is sent from ktor server ${call.principal<UserIdPrincipal>()?.name}"))
            }
        }

        // login route
        post("/api/v1/core/login") {
            val user = call.receive<UserDTO>()
            val jwtAudience = "client"
            val jwtDomain = "ktor-server"
            val jwtSecret = "secret"
            val expiryDate = Date(System.currentTimeMillis() + 60000)
            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtDomain)
                .withClaim("username", user.username)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(
                hashMapOf(
                    "accessToken" to token,
                    "expiresIn" to expiryDate.toString()
                )
            )
        }

        authenticate("auth-jwt") {
            get("/api/v1/core/user/settings") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
            }
        }
    }
}

@Serializable
data class UserDTO(
    val username: String,
    val password: String,
)

@Serializable
data class BaseResponseDTO(
    val title: String,
    val message: String,
)
