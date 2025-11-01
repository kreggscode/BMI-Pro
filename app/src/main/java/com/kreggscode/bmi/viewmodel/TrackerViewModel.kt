package com.kreggscode.bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kreggscode.bmi.data.database.BMIDao
import com.kreggscode.bmi.data.model.BMIRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class TrackerViewModel(
    private val bmiDao: BMIDao
) : ViewModel() {

    private val _records = MutableStateFlow<List<BMIRecord>>(emptyList())
    val records: StateFlow<List<BMIRecord>> = _records

    init {
        loadAllRecords()
    }

    private fun loadAllRecords() {
        viewModelScope.launch {
            bmiDao.getAllRecords().collect { recordList ->
                _records.value = recordList
            }
        }
    }

    fun filterRecords(period: String) {
        viewModelScope.launch {
            when (period) {
                "Week" -> {
                    val weekAgo = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -7)
                    }.timeInMillis
                    bmiDao.getRecordsSince(weekAgo).collect { recordList ->
                        _records.value = recordList
                    }
                }
                "Month" -> {
                    val monthAgo = Calendar.getInstance().apply {
                        add(Calendar.MONTH, -1)
                    }.timeInMillis
                    bmiDao.getRecordsSince(monthAgo).collect { recordList ->
                        _records.value = recordList
                    }
                }
                "Year" -> {
                    val yearAgo = Calendar.getInstance().apply {
                        add(Calendar.YEAR, -1)
                    }.timeInMillis
                    bmiDao.getRecordsSince(yearAgo).collect { recordList ->
                        _records.value = recordList
                    }
                }
                else -> loadAllRecords()
            }
        }
    }

    fun deleteRecord(record: BMIRecord) {
        viewModelScope.launch {
            bmiDao.deleteRecord(record)
        }
    }
}

class TrackerViewModelFactory(
    private val bmiDao: BMIDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackerViewModel(bmiDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

