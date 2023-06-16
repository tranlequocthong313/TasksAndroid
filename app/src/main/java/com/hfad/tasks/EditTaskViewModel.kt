package com.hfad.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EditTaskViewModel(private val dao: TaskDao, private val id: Long) : ViewModel() {
    private val _navigateToList = MutableLiveData(false)
    val navigateToList: LiveData<Boolean> get() = _navigateToList
    val task = dao.get(id)

    fun updateTask() {
        viewModelScope.launch {
            task.value?.let { dao.update(it) }
            _navigateToList.value = true
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            task.value?.let { dao.delete(it) }
            _navigateToList.value = true
        }
    }

    fun onNavigatedToList() {
        _navigateToList.value = false
    }
}