package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.service.repository.remote.PersonService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class PersonRepository(context: Context) : BaseRepository(context) {
    private val remote = RetrofitClient.createService(PersonService::class.java)

    fun login(email: String, password: String, listener: APIListener<PersonModel>) {
        val call = remote.login(email, password)

        executeCall(call, listener)
    }
}