package com.example.tasknotes.data

import androidx.lifecycle.LiveData


class TaskRepository(private val taskDao: TaskDao) {

    /**
     * LiveData list of all tasks. The UI observes this and
     * automatically updates when the underlying data changes.
     */
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    /**
     * Inserts a new task. Called from a coroutine scope in the ViewModel.
     */
    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    /**
     * Updates an existing task. Called from a coroutine scope in the ViewModel.
     */
    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    /**
     * Deletes a task. Called from a coroutine scope in the ViewModel.
     */
    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    /**
     * Retrieves a single task by ID. Used when editing an existing task.
     */
    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)
    }
}
