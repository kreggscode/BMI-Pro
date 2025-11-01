package com.kreggscode.bmi.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kreggscode.bmi.data.api.PollinationsService
import com.kreggscode.bmi.data.database.MealLogDao
import com.kreggscode.bmi.data.model.MealLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val mealLogDao: MealLogDao
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

    fun analyzeFood(bitmap: Bitmap) {
        viewModelScope.launch {
            _analysisState.value = AnalysisState.Loading
            try {
                val result = pollinationsService.analyzeFoodImage(bitmap)
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

    fun saveMealLog(
        foodName: String,
        calories: Float,
        protein: Float,
        carbs: Float,
        fat: Float,
        mealType: String,
        imageUri: String?
    ) {
        viewModelScope.launch {
            try {
                val mealLog = MealLog(
                    foodName = foodName,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    mealType = mealType,
                    imageUri = imageUri
                )
                mealLogDao.insertMealLog(mealLog)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun resetAnalysis() {
        _analysisState.value = AnalysisState.Idle
    }
}

class ScannerViewModelFactory(
    private val mealLogDao: MealLogDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScannerViewModel(mealLogDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

