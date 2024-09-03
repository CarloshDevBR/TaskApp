package com.devmasterteam.tasks.ui.view

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.databinding.ActivityTaskFormBinding
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.viewmodel.TaskFormViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class TaskFormActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: TaskFormViewModel
    private lateinit var binding: ActivityTaskFormBinding
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    private var listPriority: List<PriorityModel> = mutableListOf()
    private var taskId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(TaskFormViewModel::class.java)
        binding = ActivityTaskFormBinding.inflate(layoutInflater)

        binding.buttonSave.setOnClickListener(this)
        binding.buttonDate.setOnClickListener(this)

        viewModel.loadPriorities()

        loadDataFromActivity()

        changesUi()

        observer()

        setContentView(binding.root)
    }

    override fun onClick(v: View) {
        when {
            (v.id == R.id.button_date) -> handleDate()
            (v.id == R.id.button_save) -> handleSave()
        }
    }

    override fun onDateSet(v: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()

        calendar.set(year, month, dayOfMonth)

        val dueDate = dateFormat.format(calendar.time)

        binding.buttonDate.text = dueDate
    }

    private fun handleDate() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun handleSave() {
        val task = TaskModel().apply {
            this.id = taskId
            this.description = binding.editDescription.text.toString()
            this.complete = binding.checkComplete.isChecked
            this.dueDate = binding.buttonDate.text.toString()

            val index = binding.spinnerPriority.selectedItemPosition
            this.priorityId = listPriority[index].id
        }

        when {
            taskId == 0 -> viewModel.save(task)
            else -> viewModel.update(task)
        }
    }

    private fun observer() {
        viewModel.priorityList.observe(this) {
            listPriority = it

            val list = mutableListOf<String>()

            for (priority in it) {
                list.add(priority.description)
            }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                list
            )

            binding.spinnerPriority.adapter = adapter
        }

        viewModel.taskSave.observe(this) {
            if (it.status()) {
                toast("Sucesso")
                finish()
            } else {
                toast(it.message())
            }
        }

        viewModel.taskUpdate.observe(this) {
            if (it.status()) {
                toast("Salvo")
                finish()
            } else {
                toast(it.message())
            }
        }

        viewModel.task.observe(this) {
            setValues(it)
        }

        viewModel.taskLoad.observe(this) {
            if (!it.status()) {
                toast(it.message())
            }
        }
    }

    private fun getIndex(priorityId: Int): Int = listPriority.indexOfFirst { it.id == priorityId }

    private fun setValues(task: TaskModel) {
        val date = SimpleDateFormat("yyyy-MM-dd").parse(task.dueDate)

        binding.editDescription.setText(task.description)
        binding.spinnerPriority.setSelection(getIndex(task.priorityId))
        binding.checkComplete.isChecked = task.complete
        binding.buttonDate.text = SimpleDateFormat("dd/MM/yyyy").format(date)
    }

    private fun changesUi() {
        if (taskId > 0) {
            binding.buttonSave.text = "Salvar"
        }
    }

    private fun loadDataFromActivity() {
        val bundle = intent.extras

        if (bundle != null) {
            taskId= bundle.getInt(TaskConstants.BUNDLE.TASKID)

            viewModel.load(taskId)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }
}