package com.kreggscode.bmi.data.database

import androidx.room.*
import com.kreggscode.bmi.data.model.DailyTracking
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTrackingDao {
    @Query("SELECT * FROM daily_tracking WHERE date = :date")
    fun getTrackingForDate(date: String): Flow<DailyTracking?>
    
    @Query("SELECT * FROM daily_tracking ORDER BY date DESC LIMIT 30")
    fun getLastMonthTracking(): Flow<List<DailyTracking>>
    
    @Query("SELECT * FROM daily_tracking WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getTrackingBetweenDates(startDate: String, endDate: String): Flow<List<DailyTracking>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(tracking: DailyTracking)
    
    @Query("UPDATE daily_tracking SET waterGlasses = :glasses WHERE date = :date")
    suspend fun updateWaterGlasses(date: String, glasses: Int)
    
    @Query("UPDATE daily_tracking SET sleepHours = :hours WHERE date = :date")
    suspend fun updateSleepHours(date: String, hours: Float)
    
    @Delete
    suspend fun delete(tracking: DailyTracking)
}

