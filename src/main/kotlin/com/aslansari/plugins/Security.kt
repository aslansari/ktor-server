package com.aslansari.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.*

fun Application.configureSecurity() {
    // TODO get these values from environment variables
    val jwtAudience = "client"
    val jwtDomain = "ktor-server"
    val jwtRealm = "ktor sample app"
    val jwtSecret = "secret"
    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
        basic("auth-basic") {
            realm = "access the 'user/me' path"
            validate { credentials ->
                hashedUserTable.authenticate(credentials)
            }
        }
    }
}

val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }
val hashedUserTable = UserHashedTableAuth(
    table = mapOf(
        "jetbrains" to digestFunction("foobar"),
        "admin" to digestFunction("password")
    ),
    digester = digestFunction
)
