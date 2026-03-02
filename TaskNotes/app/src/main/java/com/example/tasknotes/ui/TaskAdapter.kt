package com.example.tasknotes.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tasknotes.R
import com.example.tasknotes.data.Task

/**
 * RecyclerView Adapter for displaying tasks in a list.
 * Uses ListAdapter with DiffUtil for efficient list updates,
 * only re-rendering items that have actually changed.
 *
 * @param onItemClick Callback when a task item is tapped (for editing)
 * @param onDeleteClick Callback when the delete button is tapped
 * @param onCheckChanged Callback when the completion checkbox is toggled
 */
class TaskAdapter(
    private val onItemClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onCheckChanged: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxComplete)
        private val titleText: TextView = itemView.findViewById(R.id.textViewTitle)
        private val descriptionText: TextView = itemView.findViewById(R.id.textViewDescription)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(task: Task) {
            titleText.text = task.title
            descriptionText.text = task.description

            // Show or hide description based on content
            descriptionText.visibility = if (task.description.isBlank()) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Set checkbox state without triggering listener
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = task.isCompleted

            // Apply strikethrough effect for completed tasks
            if (task.isCompleted) {
                titleText.paintFlags = titleText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                titleText.alpha = 0.5f
                descriptionText.alpha = 0.5f
            } else {
                titleText.paintFlags = titleText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                titleText.alpha = 1.0f
                descriptionText.alpha = 1.0f
            }

            // Set click listeners
            checkBox.setOnCheckedChangeListener { _, _ ->
                onCheckChanged(task)
            }

            itemView.setOnClickListener {
                onItemClick(task)
            }

            deleteButton.setOnClickListener {
                onDeleteClick(task)
            }
        }
    }

    /**
     * DiffUtil callback for efficient RecyclerView updates.
     * Compares tasks by ID and content to minimize re-draws.
     */
    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
