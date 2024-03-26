package com.aslansari.dao

import com.aslansari.model.Task
import com.aslansari.model.TaskStatus
import com.aslansari.model.Tasks
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class TaskDao {
    private fun rowToTask(row: ResultRow): Task {
        return Task(
            id = row[Tasks.id],
            title = row[Tasks.title],
            details = row[Tasks.details],
            status = row[Tasks.status],
            createdDate = row[Tasks.createdDate]
        )
    }

    suspend fun getTasks() = query {
        Tasks.selectAll().map { rowToTask(it) }
    }

    suspend fun getTask(id: Int) = query {
        Tasks
            .select { Tasks.id eq id }
            .map { rowToTask(it) }
            .singleOrNull()
    }

    suspend fun createTask(title: String, details: String) = query {
        Tasks.insert {
            it[this.title] = title
            it[this.details] = details
            it[this.status] = TaskStatus.PENDING
            it[this.createdDate] = System.currentTimeMillis()
        }
    }
}