package com.example.tasknotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Task entity representing a single task/note in the database.
 *
 * SECURE CODING PRACTICE #1 - Data Storage Security:
 * Room stores data in the app's private internal storage directory
 * (/data/data/com.example.tasknotes/databases/). This location is sandboxed
 * by Android's file system permissions, meaning other apps cannot access the data.
 * No sensitive data is stored in external storage or shared preferences in plaintext.
 *
 * SECURE CODING PRACTICE #2 - No Hardcoded Sensitive Data:
 * This entity does not contain any hardcoded API keys, passwords, or secrets.
 * All data is user-generated and stored securely in the local Room database.
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val description: String = "",

    val isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)
