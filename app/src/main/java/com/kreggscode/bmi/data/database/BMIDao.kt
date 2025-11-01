package com.kreggscode.bmi.data.database

import androidx.room.*
import com.kreggscode.bmi.data.model.BMIRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BMIDao {
    @Query("SELECT * FROM bmi_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<BMIRecord>>

    @Query("SELECT * FROM bmi_records WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getRecordsSince(startTime: Long): Flow<List<BMIRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BMIRecord)

    @Delete
    suspend fun deleteRecord(record: BMIRecord)

    @Query("DELETE FROM bmi_records")
    suspend fun deleteAllRecords()
}

