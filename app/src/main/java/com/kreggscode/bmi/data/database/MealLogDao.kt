package com.kreggscode.bmi.data.database

import androidx.room.*
import com.kreggscode.bmi.data.model.MealLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLogDao {
    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMealLogs(): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getMealLogsBetween(startTime: Long, endTime: Long): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(mealLog: MealLog)

    @Delete
    suspend fun deleteMealLog(mealLog: MealLog)

    @Query("DELETE FROM meal_logs")
    suspend fun deleteAllMealLogs()
}

