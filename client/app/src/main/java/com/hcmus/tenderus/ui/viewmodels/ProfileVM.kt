package com.hcmus.tenderus.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hcmus.tenderus.TenderUsApplication
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.model.Preference
import com.hcmus.tenderus.network.ApiClient.ProcessProfile
import com.hcmus.tenderus.network.ProfileService
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface ProfileUiState {
    data class Success(val profile: Profile) : ProfileUiState
    data class PreferencesSuccess(val preferences: Preference) : ProfileUiState
    data object Error : ProfileUiState
    data object Loading : ProfileUiState
}

class ProfileVM(private val profileService: ProfileService) : ViewModel() {
    var profileUiState by mutableStateOf<ProfileUiState>(ProfileUiState.Loading)
        private set

    var updateProfileState by mutableStateOf<ProfileUiState>(ProfileUiState.Loading)
        private set

    fun getCurrentUserPreferences(token: String) {
        viewModelScope.launch {
            profileUiState = ProfileUiState.Loading
            try {
                val preference = profileService.getCurrentUserPreferences("Bearer $token")
                profileUiState = ProfileUiState.PreferencesSuccess(preference)
            } catch (e: IOException) {
                profileUiState = ProfileUiState.Error
            } catch (e: HttpException) {
                profileUiState = ProfileUiState.Error
            }
        }
    }

    fun getCurrentUserProfile(token: String) {
        viewModelScope.launch {
            profileUiState = ProfileUiState.Loading
            try {
                val profile = profileService.getCurrentUserProfile("Bearer $token")
                profileUiState = ProfileUiState.Success(profile)
            } catch (e: IOException) {
                profileUiState = ProfileUiState.Error
            } catch (e: HttpException) {
                profileUiState = ProfileUiState.Error
            }
        }
    }

    fun upsertUserPreferences(token: String, preference: Preference) {
        viewModelScope.launch {
            updateProfileState = ProfileUiState.Loading
            try {
                profileService.upsertUserPreferences("Bearer $token", preference)
                updateProfileState = ProfileUiState.PreferencesSuccess(preference)
                getCurrentUserPreferences(token)
            } catch (e: IOException) {
                updateProfileState = ProfileUiState.Error
            } catch (e: HttpException) {
                updateProfileState = ProfileUiState.Error
            }
        }
    }

    fun upsertUserProfile(token: String = TokenManager.getToken()!!, profile: Profile) {
        viewModelScope.launch {
            updateProfileState = ProfileUiState.Loading
            try {
                profileService.upsertUserProfile("Bearer $token", profile)
                updateProfileState = ProfileUiState.Success(profile)
                getCurrentUserProfile(token)
            } catch (e: IOException) {
                updateProfileState = ProfileUiState.Error
            } catch (e: HttpException) {
                updateProfileState = ProfileUiState.Error
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TenderUsApplication)
                val processProfile = ProcessProfile
                ProfileVM(processProfile)
            }
        }
    }
}
