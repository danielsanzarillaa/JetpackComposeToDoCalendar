package com.example.composetodo.presenter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetodo.data.local.TaskDatabase
import com.example.composetodo.model.Priority
import com.example.composetodo.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TaskPresenter(application: Application) : AndroidViewModel(application) {
    private val taskDao = TaskDatabase.getDatabase(application).taskDao()

    val tasks: Flow<List<Task>> = taskDao.getAllTasks()

    fun addTask(
        title: String,
        description: String = "",
        priority: Priority = Priority.MEDIA
    ) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                priority = priority,
                date = LocalDateTime.now()
            )
            taskDao.addTask(task)
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            taskDao.deleteTaskById(taskId)
        }
    }

    fun undoDeleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.addTask(task.copy(id = 0))
        }
    }

    fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            taskDao.updateTaskStatus(taskId, isCompleted)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task)
        }
    }
} 