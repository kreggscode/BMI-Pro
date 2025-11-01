package com.kreggscode.bmi.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kreggscode.bmi.data.model.BMIRecord
import com.kreggscode.bmi.data.model.MealLog

@Database(
    entities = [
        BMIRecord::class, 
        MealLog::class, 
        com.kreggscode.bmi.data.model.UserProfile::class,
        com.kreggscode.bmi.data.model.Habit::class,
        com.kreggscode.bmi.data.model.HabitCompletion::class,
        com.kreggscode.bmi.data.model.TodoItem::class,
        com.kreggscode.bmi.data.model.Affirmation::class,
        com.kreggscode.bmi.data.model.DailyTracking::class
    ],
    version = 6,
    exportSchema = false
)
abstract class BMIDatabase : RoomDatabase() {
    abstract fun bmiDao(): BMIDao
    abstract fun mealLogDao(): MealLogDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun habitDao(): HabitDao
    abstract fun todoDao(): TodoDao
    abstract fun affirmationDao(): AffirmationDao
    abstract fun dailyTrackingDao(): DailyTrackingDao

    companion object {
        @Volatile
        private var INSTANCE: BMIDatabase? = null

        fun getDatabase(context: Context): BMIDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BMIDatabase::class.java,
                    "bmi_database"
                )
                    .fallbackToDestructiveMigration() // For development - handle migrations properly in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

