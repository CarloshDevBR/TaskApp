package com.devmasterteam.tasks.ui.viewholder

import android.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.databinding.RowTaskListBinding
import com.devmasterteam.tasks.service.listener.TaskListener
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.repository.PriorityRepository
import java.text.SimpleDateFormat

class TaskViewHolder(private val itemBinding: RowTaskListBinding, val listener: TaskListener) :
    RecyclerView.ViewHolder(itemBinding.root) {
    fun bindData(task: TaskModel) {
        val date = SimpleDateFormat("yyyy-MM-dd").parse(task.dueDate)

        itemBinding.textDescription.text = task.description
        itemBinding.textPriority.text = task.priorityDescription
        itemBinding.textDueDate.text = SimpleDateFormat("dd/MM/yyyy").format(date)

        if (task.complete) {
            itemBinding.imageTask.setImageResource(R.drawable.ic_done)
        } else {
            itemBinding.imageTask.setImageResource(R.drawable.ic_todo)
        }

        setEvents(task)
    }

    private fun setEvents(task: TaskModel) {
        itemBinding.textDescription.setOnClickListener { listener.onListClick(task.id) }
        itemBinding.imageTask.setOnClickListener {
            when {
                task.complete -> listener.onUndoClick(task.id)
                !task.complete -> listener.onCompleteClick(task.id)
            }
        }
        itemBinding.textDescription.setOnLongClickListener {
            AlertDialog.Builder(itemView.context).setTitle(R.string.remocao_de_tarefa)
                .setMessage(R.string.remover_tarefa)
                .setPositiveButton(R.string.sim) { dialog, which -> listener.onDeleteClick(task.id) }
                .setNeutralButton(R.string.cancelar, null)
                .show()
            true
        }
    }
}