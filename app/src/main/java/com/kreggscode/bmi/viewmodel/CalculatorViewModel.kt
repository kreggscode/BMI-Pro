package com.kreggscode.bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kreggscode.bmi.data.api.PollinationsService
import com.kreggscode.bmi.data.database.BMIDao
import com.kreggscode.bmi.data.model.BMIRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val bmiDao: BMIDao
) : ViewModel() {

    private val pollinationsService = PollinationsService()

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState

    sealed class AnalysisState {
        object Idle : AnalysisState()
        object Loading : AnalysisState()
        data class Success(val analysis: String) : AnalysisState()
        data class Error(val message: String) : AnalysisState()
    }

    fun saveBMIRecord(weight: Float, height: Float, bmi: Float, category: String) {
        viewModelScope.launch {
            try {
                val record = BMIRecord(
                    weight = weight,
                    height = height,
                    bmi = bmi,
                    category = category
                )
                bmiDao.insertRecord(record)
            } catch (e: Exception) {
                // Handle error silently or log
            }
        }
    }

    fun analyzeBMI(bmi: Float, weight: Float, height: Float, category: String) {
        viewModelScope.launch {
            _analysisState.value = AnalysisState.Loading
            try {
                val result = pollinationsService.analyzeBMI(bmi, weight, height, category)
                result.onSuccess { analysis ->
                    _analysisState.value = AnalysisState.Success(analysis)
                }.onFailure { error ->
                    _analysisState.value = AnalysisState.Error(error.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

class CalculatorViewModelFactory(
    private val bmiDao: BMIDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(bmiDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

