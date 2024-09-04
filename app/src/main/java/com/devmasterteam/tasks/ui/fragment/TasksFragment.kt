package com.devmasterteam.tasks.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devmasterteam.tasks.databinding.FragmentTasksBinding
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.TaskListener
import com.devmasterteam.tasks.ui.adapter.TaskAdapter
import com.devmasterteam.tasks.ui.view.TaskFormActivity
import com.devmasterteam.tasks.viewmodel.TaskListViewModel

class TasksFragment : Fragment() {
    private lateinit var viewModel: TaskListViewModel
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val adapter = TaskAdapter()
    private var taskFilter = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, b: Bundle?): View {
        viewModel = ViewModelProvider(this).get(TaskListViewModel::class.java)
        _binding = FragmentTasksBinding.inflate(inflater, container, false)

        binding.recyclerTasks.layoutManager = LinearLayoutManager(context)

        binding.recyclerTasks.adapter = adapter

        taskFilter = requireArguments().getInt(TaskConstants.BUNDLE.TASKFILTER, 0)

        val listener = object : TaskListener {
            override fun onListClick(id: Int) {
                val intent = Intent(context, TaskFormActivity::class.java)

                val bundle = Bundle()

                bundle.putInt(TaskConstants.BUNDLE.TASKID, id)

                intent.putExtras(bundle)

                startActivity(intent)
            }

            override fun onDeleteClick(id: Int) {
                viewModel.delete(id, taskFilter)
            }

            override fun onCompleteClick(id: Int) {
                viewModel.complete(id, taskFilter)
            }

            override fun onUndoClick(id: Int) {
                viewModel.undo(id, taskFilter)
            }
        }

        adapter.attachListener(listener)

        observe()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.list(taskFilter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observe() {
        viewModel.tasks.observe(viewLifecycleOwner) {
            adapter.updateTasks(it)
        }

        viewModel.status.observe(viewLifecycleOwner) {
            if (!it.status()) {
                toast(it.message())
            }
        }

        viewModel.delete.observe(viewLifecycleOwner) {
            if (!it.status()) {
                toast(it.message())
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}