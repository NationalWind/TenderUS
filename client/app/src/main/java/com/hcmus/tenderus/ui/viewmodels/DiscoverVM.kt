package com.hcmus.tenderus.ui.viewmodels

import android.net.http.HttpException
import android.os.Build
import android.util.Log
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
import com.hcmus.tenderus.data.TenderUsRepository
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.ReportData
import com.hcmus.tenderus.network.ApiClient
import com.hcmus.tenderus.network.ApiClient.DiscoverService
import com.hcmus.tenderus.network.DiscoverService
import com.hcmus.tenderus.network.LikeRequest
import com.hcmus.tenderus.network.PassRequest
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

sealed interface SwipeUiState {
    data class LikeSuccess(var match: Boolean) : SwipeUiState
    data object PassSuccess : SwipeUiState
    data object Error : SwipeUiState
    data object Loading : SwipeUiState
}

class DiscoverVM(
    private val discoverService: DiscoverService,
    private val tenderUsRepository: TenderUsRepository
) : ViewModel() {
    var discoverUiState by mutableStateOf<DiscoverUiState>(DiscoverUiState.Loading)
        private set

    var swipeUiState by mutableStateOf<SwipeUiState>(SwipeUiState.Loading)
        private set

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun getProfiles(token: String, limit: String) {
        viewModelScope.launch {
            discoverUiState = DiscoverUiState.Loading
            try {
                val profileResponse = discoverService.getProfiles("Bearer $token", limit)
                discoverUiState = DiscoverUiState.Success(profileResponse.profiles)
            } catch (e: IOException) {
                discoverUiState = DiscoverUiState.Error
            } catch (e: HttpException) {
                discoverUiState = DiscoverUiState.Error
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun likeProfile(token: String, likeRequest: LikeRequest) {
        viewModelScope.launch {
            swipeUiState = SwipeUiState.Loading
            try {
                val likeResponse = discoverService.likeProfile("Bearer $token", likeRequest)
                swipeUiState = SwipeUiState.LikeSuccess(likeResponse.match)
            } catch (e: IOException) {
                swipeUiState = SwipeUiState.Error
            } catch (e: HttpException) {
                swipeUiState = SwipeUiState.Error
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun passProfile(token: String, passRequest: PassRequest) {
        viewModelScope.launch {
            swipeUiState = SwipeUiState.Loading
            try {
                discoverService.passProfile("Bearer $token", passRequest)
                swipeUiState = SwipeUiState.PassSuccess
            } catch (e: IOException) {
                swipeUiState = SwipeUiState.Error
            } catch (e: HttpException) {
                swipeUiState = SwipeUiState.Error
            }
        }
    }

    fun postReport(report: ReportData) {
        viewModelScope.launch {
            try {
                tenderUsRepository.postReport(report)
            } catch (e: IOException) {
                Log.d("DiscoverPostReport", e.message.toString())
            } catch (e: retrofit2.HttpException) {
                Log.d("DiscoverPostReport", e.message.toString())
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TenderUsApplication)
                val discoverService = DiscoverService
                val tenderUsRepository = application.container.tenderUsRepository
                // Cho nay hoi do, tai co 2 cai service lan =))
                DiscoverVM(discoverService, tenderUsRepository)
            }
        }
    }
}

