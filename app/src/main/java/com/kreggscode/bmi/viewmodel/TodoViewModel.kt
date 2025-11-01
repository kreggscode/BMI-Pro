package com.kreggscode.bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kreggscode.bmi.data.database.TodoDao
import com.kreggscode.bmi.data.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TodoViewModel(
    private val todoDao: TodoDao
) : ViewModel() {

    val activeTodos: Flow<List<TodoItem>> = todoDao.getActiveTodos()
    val completedTodos: Flow<List<TodoItem>> = todoDao.getCompletedTodos()
    val allTodos: Flow<List<TodoItem>> = todoDao.getAllTodos()

    fun addTodo(todo: TodoItem) {
        viewModelScope.launch {
            todoDao.insertTodo(todo)
        }
    }

    fun updateTodo(todo: TodoItem) {
        viewModelScope.launch {
            todoDao.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            todoDao.deleteTodo(todo)
        }
    }

    fun toggleTodoCompletion(todo: TodoItem) {
        viewModelScope.launch {
            val newStatus = !todo.isCompleted
            val completedTime = if (newStatus) System.currentTimeMillis() else null
            todoDao.toggleTodoCompletion(todo.id, newStatus, completedTime)
        }
    }

    fun deleteAllCompleted() {
        viewModelScope.launch {
            todoDao.deleteAllCompleted()
        }
    }
}

class TodoViewModelFactory(
    private val todoDao: TodoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(todoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

