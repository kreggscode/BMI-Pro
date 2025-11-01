package com.kreggscode.bmi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val foodName: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val mealType: String, // breakfast, lunch, dinner, snack
    val imageUri: String?,
    val timestamp: Long = System.currentTimeMillis()
)

