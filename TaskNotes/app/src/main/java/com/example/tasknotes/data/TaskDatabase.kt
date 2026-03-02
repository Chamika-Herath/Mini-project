package com.example.tasknotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        // Volatile ensures the value is always read from main memory,
        // preventing caching issues in multi-threaded environments
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        /**
         * Returns the singleton instance of TaskDatabase.
         * Thread-safe implementation using double-checked locking.
         */
        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_notes_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
