package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.repository.local.TaskDatabase
import com.devmasterteam.tasks.service.repository.remote.PriorityService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PriorityRepository(val context: Context) {
    private val remote = RetrofitClient.createService(PriorityService::class.java)
    private val database = TaskDatabase.getDatabase(context).priorityDAO()

    fun list(listener: APIListener<List<PriorityModel>>) {
        val call = remote.list()

        call.enqueue(object : Callback<List<PriorityModel>> {
            override fun onResponse(
                call: Call<List<PriorityModel>>,
                response: Response<List<PriorityModel>>
            ) {
                if (response.code() != TaskConstants.HTTP.SUCCESS) {
                    return listener.onFailure(failResponse(response.errorBody()!!.string()))
                }

                response.body()?.let { listener.onSuccess(it) }
            }

            override fun onFailure(call: Call<List<PriorityModel>>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }
        })
    }

    fun listPriorities(): List<PriorityModel> = database.list()

    fun save(list: List<PriorityModel>) {
        database.clear()

        database.save(list)
    }

    private fun failResponse(str: String) = Gson().fromJson(str, String::class.java)
}