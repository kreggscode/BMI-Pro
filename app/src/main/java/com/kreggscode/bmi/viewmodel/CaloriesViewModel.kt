package com.kreggscode.bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kreggscode.bmi.data.database.MealLogDao
import com.kreggscode.bmi.data.model.MealLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

class CaloriesViewModel(
    private val mealLogDao: MealLogDao
) : ViewModel() {

    val todayMealLogs: Flow<List<MealLog>>
        get() {
            val startOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            return mealLogDao.getMealLogsBetween(startOfDay, endOfDay)
        }

    fun addMealLog(
        foodName: String,
        calories: Float,
        protein: Float,
        carbs: Float,
        fat: Float,
        mealType: String
    ) {
        viewModelScope.launch {
            val mealLog = MealLog(
                foodName = foodName,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat,
                mealType = mealType,
                imageUri = null
            )
            mealLogDao.insertMealLog(mealLog)
        }
    }

    fun deleteMealLog(mealLog: MealLog) {
        viewModelScope.launch {
            mealLogDao.deleteMealLog(mealLog)
        }
    }
}

class CaloriesViewModelFactory(
    private val mealLogDao: MealLogDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaloriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CaloriesViewModel(mealLogDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

