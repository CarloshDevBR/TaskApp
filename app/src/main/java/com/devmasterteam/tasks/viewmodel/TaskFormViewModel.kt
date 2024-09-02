package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.TaskRepository

class TaskFormViewModel(application: Application) : AndroidViewModel(application) {
    private val priorityRepository = PriorityRepository(application)
    private val taskRepository = TaskRepository(application)

    private val _priorityList = MutableLiveData<List<PriorityModel>>()
    val priorityList: LiveData<List<PriorityModel>> = _priorityList

    fun save(task: TaskModel) {
        taskRepository.create(task, object : APIListener<Boolean> {
            override fun onSuccess(response: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onFailure(message: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun loadPriorities() {
        _priorityList.value = priorityRepository.listPriorities()
    }
}