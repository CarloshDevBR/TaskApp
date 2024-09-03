package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient
import com.devmasterteam.tasks.service.repository.remote.TaskService
import retrofit2.Call

class TaskRepository(context: Context) : BaseRepository(context) {
    private val remote = RetrofitClient.createService(TaskService::class.java)

    fun list(listener: APIListener<List<TaskModel>>) {
        val call = remote.list()

        list(call, listener)
    }

    fun listNext(listener: APIListener<List<TaskModel>>) {
        val call = remote.listNext()

        list(call, listener)
    }

    fun listOverdue(listener: APIListener<List<TaskModel>>) {
        val call = remote.listOverdue()

        list(call, listener)
    }

    private fun list(call: Call<List<TaskModel>>, listener: APIListener<List<TaskModel>>) {
        executeCall(call, listener)
    }

    fun create(task: TaskModel, listener: APIListener<Boolean>) {
        val call = remote.create(
            0,
            task.priorityId,
            task.description,
            task.dueDate,
            task.complete
        )

        executeCall(call, listener)
    }

    fun complete(id: Int, listener: APIListener<Boolean>) {
        val call = remote.complete(id)

        executeCall(call, listener)
    }

    fun undo(id: Int, listener: APIListener<Boolean>) {
        val call = remote.undo(id)

        executeCall(call, listener)
    }

    fun delete(id: Int, listener: APIListener<Boolean>) {
        val call = remote.remove(id)

        executeCall(call, listener)
    }
}