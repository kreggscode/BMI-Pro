package com.kreggscode.bmi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_tracking")
data class DailyTracking(
    @PrimaryKey
    val date: String, // Format: yyyy-MM-dd
    val waterGlasses: Int = 0,
    val sleepHours: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

