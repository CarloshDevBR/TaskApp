package com.devmasterteam.tasks.service.listener

interface APIListener<T> {
    fun onSuccess(response: T)

    fun onFailure(message: String)
}