package com.kreggscode.bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreggscode.bmi.data.api.PollinationsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DietViewModel : ViewModel() {

    private val pollinationsService = PollinationsService()

    private val _dietPlanState = MutableStateFlow<DietPlanState>(DietPlanState.Idle)
    val dietPlanState: StateFlow<DietPlanState> = _dietPlanState

    sealed class DietPlanState {
        object Idle : DietPlanState()
        object Loading : DietPlanState()
        data class Success(val plan: String) : DietPlanState()
        data class Error(val message: String) : DietPlanState()
    }

    fun generateDietPlan(bmi: Float, goal: String, preferences: String = "") {
        viewModelScope.launch {
            _dietPlanState.value = DietPlanState.Loading
            try {
                val result = pollinationsService.generateDietPlan(bmi, goal, preferences)
                result.onSuccess { plan ->
                    _dietPlanState.value = DietPlanState.Success(plan)
                }.onFailure { error ->
                    _dietPlanState.value = DietPlanState.Error(error.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _dietPlanState.value = DietPlanState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

