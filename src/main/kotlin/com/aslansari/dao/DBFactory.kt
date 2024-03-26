package com.aslansari.dao

import com.aslansari.model.Tasks
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DBFactory {
    fun init(environment: ApplicationEnvironment) {
        val dbUrl = environment.config.property("ktor.db.host").getString()
        val dbUser = environment.config.property("ktor.db.user").getString()
        val dbPassword = environment.config.property("ktor.db.password").getString()
        val dbPool = environment.config.property("ktor.db.pool").getString().toInt()

        val dataSource = hikari(dbUrl, dbUser, dbPassword, dbPool)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(Tasks)
        }
    }
}

suspend fun <T> query(block: () -> T): T  = withContext(Dispatchers.IO){
    transaction {
        block()
    }
}

fun hikari(
    dbUrl: String,
    dbUser: String,
    dbPassword: String,
    dbPool: Int,
): HikariDataSource {
    val config = HikariConfig().apply {
        jdbcUrl = dbUrl
        username = dbUser
        password = dbPassword
        maximumPoolSize = dbPool
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        driverClassName = "org.postgresql.Driver"
    }
    config.validate()
    return HikariDataSource(config)
}