package com.kreggscode.bmi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bmi_records")
data class BMIRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weight: Float,
    val height: Float,
    val bmi: Float,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)

