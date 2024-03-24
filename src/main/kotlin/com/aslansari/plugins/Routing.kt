package com.aslansari.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

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
    }
}

@Serializable
data class BaseResponseDTO(
    val title: String,
    val message: String,
)
