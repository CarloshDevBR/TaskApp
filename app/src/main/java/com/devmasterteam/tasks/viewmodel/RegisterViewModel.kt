package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PersonRepository
import com.devmasterteam.tasks.service.repository.SecurityPreferences
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val securityPreferences = SecurityPreferences(application)
    private val remote = PersonRepository(application)

    private val _created = MutableLiveData<ValidationModel>()
    val created: LiveData<ValidationModel> = _created

    fun create(name: String, email: String, password: String) {
        remote.register(name, email, password, object : APIListener<PersonModel> {
            override fun onSuccess(response: PersonModel) {
                savePerson(response)

                addHeaders(response.token, response.personKey)

                _created.value = ValidationModel()
            }

            override fun onFailure(message: String) {
                _created.value = ValidationModel(message)
            }
        })
    }

    fun savePerson(person: PersonModel) {
        securityPreferences.store(TaskConstants.SHARED.TOKEN_KEY, person.token)
        securityPreferences.store(TaskConstants.SHARED.PERSON_KEY, person.personKey)
        securityPreferences.store(TaskConstants.SHARED.PERSON_NAME, person.name)
    }

    fun addHeaders(token: String, personKey: String) = RetrofitClient.addHeaders(token, personKey)
}