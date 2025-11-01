package com.kreggscode.bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kreggscode.bmi.data.database.HabitDao
import com.kreggscode.bmi.data.model.Habit
import com.kreggscode.bmi.data.model.HabitCompletion
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HabitsViewModel(
    private val habitDao: HabitDao
) : ViewModel() {

    val habits: Flow<List<Habit>> = habitDao.getAllActiveHabits()

    private val _todayCompletions = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val todayCompletions: StateFlow<Map<Long, Boolean>> = _todayCompletions

    private val _streaks = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val streaks: StateFlow<Map<Long, Int>> = _streaks

    init {
        loadTodayCompletions()
        calculateStreaks()
    }

    private fun loadTodayCompletions() {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            habits.collect { habitList ->
                val completions = mutableMapOf<Long, Boolean>()
                habitList.forEach { habit ->
                    val completion = habitDao.getCompletionForDate(habit.id, today)
                    completions[habit.id] = completion != null
                }
                _todayCompletions.value = completions
            }
        }
    }

    private fun calculateStreaks() {
        viewModelScope.launch {
            habits.collect { habitList ->
                val streaks = mutableMapOf<Long, Int>()
                habitList.forEach { habit ->
                    streaks[habit.id] = calculateHabitStreak(habit.id)
                }
                _streaks.value = streaks
            }
        }
    }

    private suspend fun calculateHabitStreak(habitId: Long): Int {
        var streak = 0
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        // Check backwards from yesterday (today doesn't count for streak until tomorrow)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        
        while (true) {
            val date = dateFormat.format(calendar.time)
            val completion = habitDao.getCompletionForDate(habitId, date)
            
            if (completion != null) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
            
            // Safety limit
            if (streak > 365) break
        }
        
        return streak
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            habitDao.insertHabit(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            habitDao.updateHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitDao.deleteHabit(habit)
        }
    }

    fun toggleHabitCompletion(habitId: Long, date: String) {
        viewModelScope.launch {
            val existing = habitDao.getCompletionForDate(habitId, date)
            
            if (existing != null) {
                // Uncomplete
                habitDao.deleteCompletion(existing)
            } else {
                // Complete
                val completion = HabitCompletion(
                    habitId = habitId,
                    date = date
                )
                habitDao.insertCompletion(completion)
            }
            
            // Reload completions and streaks
            loadTodayCompletions()
            calculateStreaks()
        }
    }
}

class HabitsViewModelFactory(
    private val habitDao: HabitDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitsViewModel(habitDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

