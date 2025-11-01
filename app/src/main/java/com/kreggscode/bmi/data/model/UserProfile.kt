package com.kreggscode.bmi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val gender: String, // Male, Female, Other
    val height: Float,
    val targetWeight: Float,
    val goal: String, // Weight Loss, Muscle Gain, Maintain, General Health
    val activityLevel: String, // Sedentary, Light, Moderate, Active, Very Active
    val avatarColor: String, // Hex color for profile avatar
    val isActive: Boolean = false, // Currently selected profile
    val createdAt: Long = System.currentTimeMillis()
)

