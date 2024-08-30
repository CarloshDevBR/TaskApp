package com.devmasterteam.tasks.service.repository

import com.devmasterteam.tasks.service.repository.remote.PersonService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class PersonRepository {
    val service = RetrofitClient.createService(PersonService::class.java)

    fun login(email: String, password: String) {

    }
}