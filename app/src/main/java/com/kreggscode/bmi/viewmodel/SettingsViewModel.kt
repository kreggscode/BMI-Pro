package com.kreggscode.bmi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kreggscode.bmi.data.database.UserProfileDao
import com.kreggscode.bmi.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userProfileDao: UserProfileDao
) : ViewModel() {

    val profiles: Flow<List<UserProfile>> = userProfileDao.getAllProfiles()
    val activeProfile: Flow<UserProfile?> = userProfileDao.getActiveProfile()

    fun addProfile(profile: UserProfile) {
        viewModelScope.launch {
            val profileId = userProfileDao.insertProfile(profile)
            // Set as active if it's the first profile
            val allProfiles = userProfileDao.getAllProfiles()
            // Note: This is simplified - in production, check if there are no profiles first
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            userProfileDao.updateProfile(profile)
        }
    }

    fun deleteProfile(profile: UserProfile) {
        viewModelScope.launch {
            userProfileDao.deleteProfile(profile)
        }
    }

    fun setActiveProfile(profileId: Long) {
        viewModelScope.launch {
            userProfileDao.deactivateAllProfiles()
            userProfileDao.setActiveProfile(profileId)
        }
    }
}

class SettingsViewModelFactory(
    private val userProfileDao: UserProfileDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(userProfileDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

