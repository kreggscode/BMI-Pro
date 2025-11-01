package com.kreggscode.bmi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val category: String, // Health, Fitness, Nutrition, Wellness
    val icon: String, // Icon name as string
    val color: String, // Hex color
    val frequency: String, // Daily, Weekly, Custom
    val targetDays: Int = 7, // Days per week to complete
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_completions")
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String, // Format: YYYY-MM-DD
    val completedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

