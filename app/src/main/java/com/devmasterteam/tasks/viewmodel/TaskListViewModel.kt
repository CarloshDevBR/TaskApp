package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.TaskRepository

class TaskListViewModel(application: Application) : AndroidViewModel(application) {
    private val remote = TaskRepository(application)

    private val taskRepository = TaskRepository(application)
    private val priorityRepository = PriorityRepository(application)

    private val _tasks = MutableLiveData<List<TaskModel>>()
    val tasks: LiveData<List<TaskModel>> = _tasks

    private val _list = MutableLiveData<ValidationModel>()
    val list: LiveData<ValidationModel> = _list

    private val _status = MutableLiveData<ValidationModel>()
    val status: LiveData<ValidationModel> = _status

    private val _delete = MutableLiveData<ValidationModel>()
    val delete: LiveData<ValidationModel> = _delete

    fun list(filterId: Int) {
        val listener = object : APIListener<List<TaskModel>> {
            override fun onSuccess(response: List<TaskModel>) {
                response.map {
                    it.priorityDescription = priorityRepository.getDescription(it.priorityId)
                }

                _tasks.value = response
            }

            override fun onFailure(message: String) {
                _list.value = ValidationModel(message)
            }
        }

        when (filterId) {
            TaskConstants.FILTER.ALL -> taskRepository.list(listener)
            TaskConstants.FILTER.NEXT -> taskRepository.listNext(listener)
            TaskConstants.FILTER.EXPIRED -> taskRepository.listOverdue(listener)
        }
    }

    fun complete(id: Int, filterId: Int) {
        remote.complete(id, object : APIListener<Boolean> {
            override fun onSuccess(response: Boolean) {
                list(filterId)
            }

            override fun onFailure(message: String) {
                _status.value = ValidationModel(message)
            }
        })
    }

    fun undo(id: Int, filterId: Int) {
        remote.undo(id, object : APIListener<Boolean> {
            override fun onSuccess(response: Boolean) {
                list(filterId)
            }

            override fun onFailure(message: String) {
                _status.value = ValidationModel(message)
            }
        })
    }

    fun delete(id: Int, filterId: Int) {
        remote.delete(id, object : APIListener<Boolean> {
            override fun onSuccess(response: Boolean) {
                list(filterId)
            }

            override fun onFailure(message: String) {
                _delete.value = ValidationModel(message)
            }
        })
    }
}