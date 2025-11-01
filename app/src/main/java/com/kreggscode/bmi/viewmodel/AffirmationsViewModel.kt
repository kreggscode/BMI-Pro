package com.kreggscode.bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kreggscode.bmi.data.database.AffirmationDao
import com.kreggscode.bmi.data.model.Affirmation
import com.kreggscode.bmi.data.model.AffirmationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AffirmationsViewModel(
    private val affirmationDao: AffirmationDao
) : ViewModel() {

    val affirmations: Flow<List<Affirmation>> = affirmationDao.getAllAffirmations()
    val favoriteAffirmations: Flow<List<Affirmation>> = affirmationDao.getFavoriteAffirmations()

    private val _dailyAffirmation = MutableStateFlow<Affirmation?>(null)
    val dailyAffirmation: StateFlow<Affirmation?> = _dailyAffirmation

    init {
        // Initialize with default affirmations if database is empty
        viewModelScope.launch {
            val count = affirmationDao.getCount()
            if (count == 0) {
                affirmationDao.insertAll(AffirmationData.defaultAffirmations)
            }
            loadDailyAffirmation()
        }
    }

    private fun loadDailyAffirmation() {
        viewModelScope.launch {
            _dailyAffirmation.value = affirmationDao.getRandomAffirmation()
        }
    }

    fun toggleFavorite(affirmation: Affirmation) {
        viewModelScope.launch {
            affirmationDao.toggleFavorite(affirmation.id, !affirmation.isFavorite)
        }
    }

    fun addAffirmation(affirmation: Affirmation) {
        viewModelScope.launch {
            affirmationDao.insertAffirmation(affirmation)
        }
    }

    fun deleteAffirmation(affirmation: Affirmation) {
        viewModelScope.launch {
            affirmationDao.deleteAffirmation(affirmation)
        }
    }

    fun refreshDailyAffirmation() {
        loadDailyAffirmation()
    }
}

class AffirmationsViewModelFactory(
    private val affirmationDao: AffirmationDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AffirmationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AffirmationsViewModel(affirmationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

