package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient
import com.devmasterteam.tasks.service.repository.remote.TaskService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskRepository(val context: Context) {
    private val remote = RetrofitClient.createService(TaskService::class.java)

    fun create(task: TaskModel, listener: APIListener<Boolean>) {
        val call = remote.create(
            0,
            task.priorityId,
            task.description,
            task.dueDate,
            task.complete
        )

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.code() != TaskConstants.HTTP.SUCCESS) {
                    return listener.onFailure(failResponse(response.errorBody()!!.string()))
                }

                response.body()?.let { listener.onSuccess(it) }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }
        })
    }

    private fun failResponse(str: String) = Gson().fromJson(str, String::class.java)
}