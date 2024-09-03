package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.repository.local.TaskDatabase
import com.devmasterteam.tasks.service.repository.remote.PriorityService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class PriorityRepository(context: Context) : BaseRepository(context) {
    private val remote = RetrofitClient.createService(PriorityService::class.java)
    private val database = TaskDatabase.getDatabase(context).priorityDAO()

    companion object {
        private val cache = mutableMapOf<Int, String>()

        fun getDescription(id: Int) = cache[id] ?: ""

        fun setDescription(id: Int, description: String) {
            cache[id] = description
        }
    }

    fun getDescription(id: Int): String {
        val cached = PriorityRepository.getDescription(id)

        if(cached == "") {
            val description = database.getDescription(id)

            PriorityRepository.setDescription(id, description)

            return description
        }

        return cached
    }

    fun list(listener: APIListener<List<PriorityModel>>) {
        val call = remote.list()

        executeCall(call, listener)
    }

    fun listPriorities(): List<PriorityModel> = database.list()

    fun save(list: List<PriorityModel>) {
        database.clear()

        database.save(list)
    }
}