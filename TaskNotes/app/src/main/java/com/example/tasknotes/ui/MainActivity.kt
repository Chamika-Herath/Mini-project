package com.example.tasknotes.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasknotes.R
import com.example.tasknotes.data.Task
import com.example.tasknotes.viewmodel.TaskViewModel
import com.example.tasknotes.viewmodel.TaskViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    companion object {
        const val EXTRA_TASK_ID = "com.example.tasknotes.EXTRA_TASK_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the toolbar title
        supportActionBar?.title = getString(R.string.app_name)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewTasks)
        emptyView = findViewById(R.id.textViewEmpty)
        val fab: FloatingActionButton = findViewById(R.id.fabAddTask)

        // Set up RecyclerView with adapter
        taskAdapter = TaskAdapter(
            onItemClick = { task ->
                // Navigate to AddEditTaskActivity for editing
                val intent = Intent(this, AddEditTaskActivity::class.java)
                intent.putExtra(EXTRA_TASK_ID, task.id)
                startActivity(intent)
            },
            onDeleteClick = { task ->
                // Show confirmation dialog before deleting
                showDeleteConfirmation(task)
            },
            onCheckChanged = { task ->
                // Toggle task completion status
                taskViewModel.toggleComplete(task)
            }
        )

        recyclerView.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        // Initialize ViewModel using the factory pattern
        val factory = TaskViewModelFactory(application)
        taskViewModel = ViewModelProvider(this, factory).get(TaskViewModel::class.java)

        // Observe the task list and update UI when data changes
        taskViewModel.allTasks.observe(this) { tasks ->
            tasks?.let {
                taskAdapter.submitList(it)
                // Show empty state when no tasks exist
                if (it.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                }
            }
        }

        // FAB click -> navigate to add new task
        fab.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Shows a confirmation dialog before deleting a task.

     */
    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_task_title))
            .setMessage(getString(R.string.delete_task_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                taskViewModel.delete(task)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}
