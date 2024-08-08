package com.hcmus.tenderus.ui.viewmodels

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hcmus.tenderus.TenderUsApplication
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.network.ApiClient
import com.hcmus.tenderus.network.GetProfile
import com.hcmus.tenderus.network.ApiClient.GetProfile
import kotlinx.coroutines.launch
import retrofit2.http.Header
import retrofit2.http.Query
import java.io.IOException

data class ProfileResponse(
    val profiles: List<Profile>
)

sealed interface DiscoverUiState {
    data class Success(val profiles: List<Profile>) : DiscoverUiState
    data object Error : DiscoverUiState
    data object Loading : DiscoverUiState
}

class DiscoverVM(private val getProfile: GetProfile) : ViewModel() {
    var discoverUiState by mutableStateOf<DiscoverUiState>(DiscoverUiState.Loading)
        private set

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun getProfiles(token: String, limit: String) {
        viewModelScope.launch {
            discoverUiState = DiscoverUiState.Loading
            try {
                val profileResponse = getProfile.getProfiles("Bearer $token", limit)
                discoverUiState = DiscoverUiState.Success(profileResponse.profiles)
            } catch (e: IOException) {
                discoverUiState = DiscoverUiState.Error
            } catch (e: HttpException) {
                discoverUiState = DiscoverUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TenderUsApplication)
                val getProfile = GetProfile
                DiscoverVM(getProfile)
            }
        }
    }
}
