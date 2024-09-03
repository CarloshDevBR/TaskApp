package com.devmasterteam.tasks.service.repository

import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.google.gson.Gson
import retrofit2.Response

open class BaseRepository {
    private fun failResponse(str: String): String = Gson().fromJson(str, String::class.java)

    protected fun <T> handleResponse(response: Response<T>, listener: APIListener<T>) {
        if (response.code() != TaskConstants.HTTP.SUCCESS) {
            return listener.onFailure(failResponse(response.errorBody()!!.string()))
        }

        response.body()?.let { listener.onSuccess(it) }
    }
}