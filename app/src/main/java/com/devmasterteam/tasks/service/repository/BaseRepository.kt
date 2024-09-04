package com.devmasterteam.tasks.service.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseRepository(val context: Context) {
    private fun failResponse(str: String): String = Gson().fromJson(str, String::class.java)

    protected fun <T> handleResponse(response: Response<T>, listener: APIListener<T>) {
        if (response.code() != TaskConstants.HTTP.SUCCESS) {
            return listener.onFailure(failResponse(response.errorBody()!!.string()))
        }

        response.body()?.let { listener.onSuccess(it) }
    }

    protected fun <T> executeCall(call: Call<T>, listener: APIListener<T>) {
        if (!isConnectionAvailable()) {
            return listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
        }

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                handleResponse(response, listener)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }
        })
    }

    private fun isConnectionAvailable(): Boolean {
        var isConnection = false

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNet = cm.activeNetwork ?: return false

            val netWorkCapabilities = cm.getNetworkCapabilities(activeNet) ?: return false

            isConnection = when {
                netWorkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                netWorkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            if (cm.activeNetworkInfo == null) return false

            isConnection = when(cm.activeNetworkInfo!!.type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                else -> false
            }
        }

        return isConnection
    }
}