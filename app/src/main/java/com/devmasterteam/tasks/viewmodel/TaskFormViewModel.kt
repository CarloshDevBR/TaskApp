package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.TaskRepository

class TaskFormViewModel(application: Application) : AndroidViewModel(application) {
    private val priorityRepository = PriorityRepository(application)
    private val taskRepository = TaskRepository(application)

    private val _priorityList = MutableLiveData<List<PriorityModel>>()
    val priorityList: LiveData<List<PriorityModel>> = _priorityList

    private val _taskSave = MutableLiveData<ValidationModel>()
    val taskSave: LiveData<ValidationModel> = _taskSave

    private val _taskUpdate = MutableLiveData<ValidationModel>()
    val taskUpdate: LiveData<ValidationModel> = _taskUpdate

    private val _task = MutableLiveData<TaskModel>()
    val task: LiveData<TaskModel> = _task

    private val _taskLoad = MutableLiveData<ValidationModel>()
    val taskLoad: LiveData<ValidationModel> = _taskLoad

    fun save(task: TaskModel) {
        taskRepository.create(task, object : APIListener<Boolean> {
            override fun onSuccess(response: Boolean) {
                _taskSave.value = ValidationModel()
            }

            override fun onFailure(message: String) {
                _taskSave.value = ValidationModel(message)
            }
        })
    }

    fun update(task: TaskModel) {
        taskRepository.update(task, object : APIListener<Boolean> {
            override fun onSuccess(response: Boolean) {
                _taskUpdate.value = ValidationModel()
            }

            override fun onFailure(message: String) {
                _taskUpdate.value = ValidationModel(message)
            }
        })
    }

    fun loadPriorities() {
        _priorityList.value = priorityRepository.listPriorities()
    }

    fun load(id: Int) {
        taskRepository.load(id, object : APIListener<TaskModel> {
            override fun onSuccess(response: TaskModel) {
                _task.value = response
            }

            override fun onFailure(message: String) {
                _taskLoad.value = ValidationModel(message)
            }
        })
    }
}