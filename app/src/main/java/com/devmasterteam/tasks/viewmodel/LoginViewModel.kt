package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.helper.BiometricHelper
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.AccessUserModel
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PersonRepository
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.SecurityPreferences
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val personRepository = PersonRepository(application)
    private val priorityRepository = PriorityRepository(application)
    private val securityPreferences = SecurityPreferences(application)

    private val _login = MutableLiveData<ValidationModel>()
    val login: LiveData<ValidationModel> = _login

    private val _loggedUser = MutableLiveData<Boolean>()

    private val _accessUser = MutableLiveData<AccessUserModel>()
    val accessUser: LiveData<AccessUserModel> = _accessUser

    private val _bioAuth = MutableLiveData<Boolean>()
    val bioAuth: LiveData<Boolean> = _bioAuth

    fun doLogin(email: String, password: String) {
        personRepository.login(email, password, object : APIListener<PersonModel> {
            override fun onSuccess(response: PersonModel) {
                savePerson(response)

                saveAccess(email, password)

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

        addHeaders(token, personKey)

        val logged = (token != "" && personKey != "")

        if (!logged) {
            priorityRepository.list(object : APIListener<List<PriorityModel>> {
                override fun onSuccess(response: List<PriorityModel>) {
                    priorityRepository.save(response)
                }

                override fun onFailure(message: String) {
                    val s = ""
                }
            })
        }

        _loggedUser.value = (logged && BiometricHelper.isBiometricAvailable(getApplication()))
    }

    fun verifyAccessUser() {
        if (_loggedUser.value == true) {
            _bioAuth.value = true
            return
        }

        val email = securityPreferences.get(TaskConstants.ACCESS_USER.USER_EMAIL)
        val password = securityPreferences.get(TaskConstants.ACCESS_USER.USER_PASSWORD)

        if (email == "" && password == "") return

        _accessUser.value = AccessUserModel(email, password)
    }

    fun savePerson(person: PersonModel) {
        securityPreferences.store(TaskConstants.SHARED.TOKEN_KEY, person.token)
        securityPreferences.store(TaskConstants.SHARED.PERSON_KEY, person.personKey)
        securityPreferences.store(TaskConstants.SHARED.PERSON_NAME, person.name)
    }

    fun saveAccess(email: String, password: String) {
        securityPreferences.store(TaskConstants.ACCESS_USER.USER_EMAIL, email)
        securityPreferences.store(TaskConstants.ACCESS_USER.USER_PASSWORD, password)
    }

    fun addHeaders(token: String, personKey: String) = RetrofitClient.addHeaders(token, personKey)
}