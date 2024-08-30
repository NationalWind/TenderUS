package com.hcmus.tenderus.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.hcmus.tenderus.TenderUsApplication
import com.hcmus.tenderus.data.TokenManager

import com.hcmus.tenderus.model.Match
import com.hcmus.tenderus.model.Message
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.network.ApiClient.DiscoverService
import com.hcmus.tenderus.network.ApiClient.ExploreService
import com.hcmus.tenderus.network.ApiClient.GetActivityStatusApi
import com.hcmus.tenderus.network.ApiClient.GetMatchesApi
import com.hcmus.tenderus.network.ApiClient.HaveReadMessageApi
import com.hcmus.tenderus.network.ApiClient.MatchPollingApi
import com.hcmus.tenderus.network.ApiClient.MessageLoadingApi
import com.hcmus.tenderus.network.ApiClient.MessagePollingApi
import com.hcmus.tenderus.network.ApiClient.MessageSendingApi
import com.hcmus.tenderus.network.HaveReadMessageRequest
import com.hcmus.tenderus.network.MessageSendingRequest
import com.hcmus.tenderus.utils.firebase.StorageUtil.Companion.uploadToStorage
import com.hcmus.tenderus.utils.getCurrentDateTimeIso
import com.hcmus.tenderus.utils.subtractInMinutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class MatchState(
    val username: String = "",
    val avatarIcon: String = "",
    val displayName: String = "",
    val createdAt: String = getCurrentDateTimeIso(),
    var isActive: Boolean = false,
    var isRead: Boolean = false,
    val messageArr: SnapshotStateList<Message> = mutableStateListOf<Message>()
)


class MatchListVM: ViewModel() {
    enum class MessageStatus {
        LOADING,
        SUCCESS,
        FAILED
    }

    val matches = mutableStateListOf<MatchState>()
    var curReceiver by mutableStateOf("")
    var curProfile by mutableStateOf<Profile?>(null)

    var uiState by mutableStateOf(MessageStatus.LOADING)

    init {
//        if (TokenManager.getRole() == "USER") {
            try {
                getMatches()
                uiState = MessageStatus.SUCCESS
            } catch (e: Exception) {
                uiState = MessageStatus.FAILED
            }

            viewModelScope.launch {
                while (true) {
                    try {
                        val token = TokenManager.getToken() ?: break
                        val newMsg =
                            MessagePollingApi.getNewMessage("Bearer $token")
                        val idx = matches.indexOfFirst { it.username == newMsg.sender }
                        if (idx == -1) continue//
                        if (idx == 0) {
                            matches[idx].messageArr.add(0, newMsg)
                        } else {
                            val match = matches[idx]
                            matches.removeAt(idx)
                            match.messageArr.add(0, newMsg)
                            matches.add(0, match)
                        }
                        matches[0].isRead = false
                        matches[0].isActive = true

                    } catch (e: Exception) {
                        Log.d("MsgPolling", e.toString())
                    }
                    delay(10000)
                }
            }

            viewModelScope.launch {
                while (true) {
                    try {
                        val token = TokenManager.getToken() ?: break
                        val newMatch = MatchPollingApi.getNewMatch("Bearer $token")
                        val idx = matches.indexOfFirst { (it.username == newMatch.username) }
                        if (idx == -1) {
                            matches.add(
                                0,
                                MatchState(
                                    username = newMatch.username,
                                    avatarIcon = newMatch.avatarIcon,
                                    displayName = newMatch.displayName,
                                    isActive = newMatch.isActive
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.d("MatchPolling", e.toString())
                    }
                    delay(10000)
//                }
            }

            viewModelScope.launch {
                while (true) {
                    try {
                        val token = TokenManager.getToken()?:break
                        for (match in matches) {
                            match.isActive =
                                GetActivityStatusApi.get("Bearer $token", match.username).isActive
                            delay(10000)
                        }

                    } catch (e: Exception) {
                        Log.d("Active Status", e.toString())
                    }
                }
            }
        }

    }

    fun getMatches() {
        viewModelScope.launch {
            try {
                uiState = MessageStatus.LOADING
                matches.clear()
                TokenManager.getToken()?.let { token ->
                    val matchesList =
                        GetMatchesApi.getMatches("Bearer $token")
                            .toMutableList()
                    matchesList.sortWith { a, b ->
                        if (a.messageArr.isEmpty() && b.messageArr.isNotEmpty()) {
                            -1
                        } else if (a.messageArr.isNotEmpty() && b.messageArr.isEmpty()) {
                            1
                        } else if (a.messageArr.isNotEmpty() && subtractInMinutes(
                                a.messageArr.first().createdAt,
                                b.messageArr.first().createdAt
                            ) != 0L
                        ) {
                            subtractInMinutes(
                                a.messageArr.first().createdAt,
                                b.messageArr.first().createdAt
                            ).toInt()
                        } else {
                            subtractInMinutes(a.createdAt, b.createdAt).toInt()
                        }

                    }

                    for (m in matchesList) {
                        matches.add(
                            MatchState(
                                m.username,
                                m.avatarIcon,
                                m.displayName,
                                m.createdAt,
                                m.isActive,
                                m.isRead
                            )
                        )
                        for (msg in m.messageArr) {
                            matches.last().messageArr.add(msg)
                        }
                    }
                    uiState = MessageStatus.SUCCESS
                }
            } catch (e: Exception) {
                uiState = MessageStatus.FAILED
                Log.d("GetMatches", e.toString())
            }
        }
    }

    fun sendMessage(req: MessageSendingRequest) {
        viewModelScope.launch {
            try {
                val msg = MessageSendingApi.sendMessage("Bearer " + TokenManager.getToken()!!, req)
                val idx = matches.indexOfFirst { it.username == msg.receiver }
                if (idx == 0) {
                    matches[idx].messageArr.add(0, msg)
                } else {
                    val match = matches[idx]
                    matches.removeAt(idx)
                    match.messageArr.add(0, msg)
                    matches.add(0, match)
                }
                uiState = MessageStatus.SUCCESS

            } catch (e: Exception) {
                Log.d("MsgSending", e.toString())
                uiState = MessageStatus.FAILED
            }
        }

    }

    fun loadMessage() {
        viewModelScope.launch {
            try {
                uiState = MessageStatus.LOADING
                matches.first { it.username == curReceiver }.let {
                    val messages = MessageLoadingApi.loadMessage("Bearer " + TokenManager.getToken()!!, curReceiver, 20, it.messageArr.last().msgID)
                    it.messageArr.addAll(messages)
                }
                uiState = MessageStatus.SUCCESS
            } catch (e: Exception) {
                uiState = MessageStatus.FAILED
                Log.d("MsgLoading", e.toString())
            }
        }

    }

    fun haveReadMessage(conversationID: String) {
        viewModelScope.launch {
            try {
                HaveReadMessageApi.update("Bearer " + TokenManager.getToken()!!, HaveReadMessageRequest(conversationID))
            } catch (e: Exception) {
                Log.d("MsgBeRead", e.toString())
            }
        }
    }

    fun getMatchProfile() {
        viewModelScope.launch {
            try {
                uiState = MessageStatus.LOADING
                curProfile = GetMatchesApi.getMatchProfile("Bearer " + TokenManager.getToken()!!, curReceiver)
                uiState = MessageStatus.SUCCESS
            } catch (e: Exception) {
                uiState = MessageStatus.FAILED
                Log.d("GetProfile", e.toString())
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MatchListVM()
            }
        }
    }

}