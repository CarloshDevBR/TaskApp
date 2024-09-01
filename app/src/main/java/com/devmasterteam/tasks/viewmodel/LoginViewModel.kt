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

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val personRepository = PersonRepository(application)
    private val securityPreferences = SecurityPreferences(application)

    private val _login = MutableLiveData<ValidationModel>()
    val login: LiveData<ValidationModel> = _login

    private val _loggedUser = MutableLiveData<Boolean>()
    val loggedUser: LiveData<Boolean> = _loggedUser

    fun doLogin(email: String, password: String) {
        personRepository.login(email, password, object : APIListener<PersonModel> {
            override fun onSuccess(response: PersonModel) {
                savePerson(response)

                addHeaders(response.token, response.personKey)

                _login.value = ValidationModel()
            }

            override fun onFailure(message: String) {
                _login.value = ValidationModel(message)
            }
        })
    }

    fun verifyLoggedUser() {
        val token = securityPreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val personKey = securityPreferences.get(TaskConstants.SHARED.PERSON_KEY)

        if (token != "" && personKey != "") {
            addHeaders(token, personKey)

            _loggedUser.value = true
        }
    }

    fun savePerson(person: PersonModel) {
        securityPreferences.store(TaskConstants.SHARED.TOKEN_KEY, person.token)
        securityPreferences.store(TaskConstants.SHARED.PERSON_KEY, person.personKey)
        securityPreferences.store(TaskConstants.SHARED.PERSON_NAME, person.name)
    }

    fun addHeaders(token: String, personKey: String) = RetrofitClient.addHeaders(token, personKey)
}