package com.hfad.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {
    var newTaskName = ""
    val tasks = taskDao.getAll()
    private val _navigateToTask = MutableLiveData<Long?>()
    val navigateToTask: LiveData<Long?> get() = _navigateToTask

    fun onTaskClicked(taskId: Long) {
        _navigateToTask.value = taskId
    }

    fun onTaskNavigated() {
        _navigateToTask.value = null
    }

    fun insertTask() {
        viewModelScope.launch {
            if (newTaskName.isNotEmpty()) {
                val task = Task()
                task.name = newTaskName
                taskDao.insert(task)
            }
        }
    }
}