package com.aslansari

import com.aslansari.dao.TaskDao
import com.aslansari.model.Task
import com.aslansari.model.TaskDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.task(
    taskDao: TaskDao
) {
    route("/api/v1/tasks") {
        get {
            call.respond(taskDao.getTasks())
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Parameter id is missing")
            val task = taskDao.getTask(id)
            if (task == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(task)
            }
        }
        post("/create") {
            val task = call.receive<TaskDto>()
            taskDao.createTask(task.title.orEmpty(), task.details.orEmpty())
            call.respond(HttpStatusCode.Created)
        }
    }
}
