package com.example.tasknotes.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tasknotes.R
import com.example.tasknotes.data.Task
import com.example.tasknotes.viewmodel.TaskViewModel
import com.example.tasknotes.viewmodel.TaskViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var editTextTitle: TextInputEditText
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var buttonSave: MaterialButton

    private var taskId: Int = -1
    private var existingTask: Task? = null

    // Keys for saving instance state during rotation
    companion object {
        private const val KEY_TITLE = "save_title"
        private const val KEY_DESCRIPTION = "save_description"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonSave = findViewById(R.id.buttonSave)

        // Initialize ViewModel
        val factory = TaskViewModelFactory(application)
        taskViewModel = ViewModelProvider(this, factory).get(TaskViewModel::class.java)

        // Check if we're editing an existing task
        taskId = intent.getIntExtra(MainActivity.EXTRA_TASK_ID, -1)

        if (taskId != -1) {
            // Editing mode - update toolbar and load existing data
            supportActionBar?.title = getString(R.string.edit_task)

            taskViewModel.getTaskById(taskId).observe(this) { task ->
                task?.let {
                    existingTask = it
                    // Only populate fields if there's no saved instance state
                    // (i.e., this is not a rotation restore)
                    if (savedInstanceState == null) {
                        editTextTitle.setText(it.title)
                        editTextDescription.setText(it.description)
                    }
                }
            }
        } else {
            // Adding mode
            supportActionBar?.title = getString(R.string.add_task)
        }

        // Restore state after rotation if available
        if (savedInstanceState != null) {
            editTextTitle.setText(savedInstanceState.getString(KEY_TITLE, ""))
            editTextDescription.setText(savedInstanceState.getString(KEY_DESCRIPTION, ""))
        }

        // Save button click handler
        buttonSave.setOnClickListener {
            saveTask()
        }

        // Enable back navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Saves the task after validating input.
     *
     * SECURE CODING PRACTICE #7 - Input Validation:
     * User input is validated before being saved to the database.
     * The title field is required and trimmed to prevent empty or
     * whitespace-only entries. This prevents storing invalid data
     * and protects the integrity of the local database.
     */
    private fun saveTask() {
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        // Validate input - title is required
        if (title.isEmpty()) {
            editTextTitle.error = getString(R.string.error_title_required)
            editTextTitle.requestFocus()
            return
        }

        // Validate title length to prevent excessively long entries
        if (title.length > 200) {
            editTextTitle.error = getString(R.string.error_title_too_long)
            editTextTitle.requestFocus()
            return
        }

        if (taskId != -1 && existingTask != null) {
            // Update existing task, preserving its completion status and creation date
            val updatedTask = existingTask!!.copy(
                title = title,
                description = description
            )
            taskViewModel.update(updatedTask)
            Toast.makeText(this, getString(R.string.task_updated), Toast.LENGTH_SHORT).show()
        } else {
            // Create new task
            val newTask = Task(title = title, description = description)
            taskViewModel.insert(newTask)
            Toast.makeText(this, getString(R.string.task_saved), Toast.LENGTH_SHORT).show()
        }

        finish() // Return to main screen
    }

    /**
     * Save UI state during configuration changes (e.g., screen rotation).
     * This preserves any unsaved text the user has typed.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TITLE, editTextTitle.text.toString())
        outState.putString(KEY_DESCRIPTION, editTextDescription.text.toString())
    }

    /**
     * Handle the back button in the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
