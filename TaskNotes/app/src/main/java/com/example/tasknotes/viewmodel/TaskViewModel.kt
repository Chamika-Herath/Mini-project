package com.example.tasknotes.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.tasknotes.data.Task
import com.example.tasknotes.data.TaskDatabase
import com.example.tasknotes.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>

    init {
        // Initialize the database and repository
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
    }

    /**
     * Inserts a new task into the database on a background thread.
     * Using viewModelScope ensures the coroutine is cancelled
     */
    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(task)
    }

    /**
     * Updates an existing task in the database.
     */
    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(task)
    }

    /**
     * Deletes a task from the database.
     */
    fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(task)
    }

    /**
     * Retrieves a single task by ID. Returns LiveData for observation.
     */
    fun getTaskById(id: Int): LiveData<Task?> {
        val result = MutableLiveData<Task?>()
        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(repository.getTaskById(id))
        }
        return result
    }

    /**
     * Toggles the completion status of a task.
     */
    fun toggleComplete(task: Task) {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        update(updatedTask)
    }
}

/**
 * Factory class for creating TaskViewModel instances.
 * Required because TaskViewModel takes an Application parameter.
 */
class TaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
