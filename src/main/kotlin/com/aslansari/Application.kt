package com.aslansari

import com.aslansari.plugins.configureHTTP
import com.aslansari.plugins.configureRouting
import com.aslansari.plugins.configureSecurity
import com.aslansari.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
