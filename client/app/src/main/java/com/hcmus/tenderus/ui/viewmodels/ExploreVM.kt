package com.hcmus.tenderus.ui.viewmodels

import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hcmus.tenderus.TenderUsApplication
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.network.ApiClient.DiscoverService
import com.hcmus.tenderus.network.ApiClient.ExploreService
import com.hcmus.tenderus.network.DiscoverService
import com.hcmus.tenderus.network.ExploreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException


class ExploreVM(private val exploreService: ExploreService, discoverService: DiscoverService) : DiscoverVM(discoverService) {
    var group by mutableStateOf<String?>(null)



    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun join(group: String) {
        viewModelScope.launch {
            try {
                exploreService.join("Bearer " + TokenManager.getToken()!!, group)
                this@ExploreVM.group = group
            } catch (e: Exception) {
                Log.d("Join", e.toString())
            }
        }
    }

    fun getJoinStatus(group: String, scope: CoroutineScope, onNotJoin: () -> Unit) {
        scope.launch {
            try {
                val joinStatus =
                    exploreService.getJoinStatus("Bearer " + TokenManager.getToken()!!, group)
                if (joinStatus.joined == true) {
                    this@ExploreVM.group = group
                }
                if (!joinStatus.joined) {
                    onNotJoin()
                }
            } catch (e: Exception) {
                Log.d("Join", e.toString())
            }
        }

    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun getProfiles(token: String, limit: String) {
        viewModelScope.launch {
            discoverUiState = DiscoverUiState.Loading
            try {
                group?.let {
                    val profileResponse = exploreService.getProfiles("Bearer $token", limit, it)
                    discoverUiState = DiscoverUiState.Success(profileResponse.profiles)
                }
            } catch (e: IOException) {
                Log.d("GetProfiles", e.message.toString())
                discoverUiState = DiscoverUiState.Error
            } catch (e: HttpException) {
                Log.d("GetProfiles", e.message.toString())
                discoverUiState = DiscoverUiState.Error
            } catch (e: Exception) {
                Log.d("GetProfiles", e.message.toString())
                discoverUiState = DiscoverUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TenderUsApplication)
                val discoverService = DiscoverService
                val exploreService = ExploreService
                ExploreVM(exploreService, discoverService)
            }
        }
    }
}