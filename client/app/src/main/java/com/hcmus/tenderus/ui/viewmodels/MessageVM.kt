package com.hcmus.tenderus.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hcmus.tenderus.data.TokenManager

import com.hcmus.tenderus.model.Match
import com.hcmus.tenderus.model.Message
import com.hcmus.tenderus.network.ApiClient.GetMatchesApi
import com.hcmus.tenderus.network.ApiClient.HaveReadMessageApi
import com.hcmus.tenderus.network.ApiClient.MessagePollingApi
import com.hcmus.tenderus.network.ApiClient.MessageSendingApi
import com.hcmus.tenderus.network.HaveReadMessageRequest
import com.hcmus.tenderus.network.MessageSendingRequest
import com.hcmus.tenderus.utils.subtractInMinutes
import kotlinx.coroutines.launch

data class MatchState(
    val username: String = "",
    val avatarIcon: String = "",
    val displayName: String = "",
    val createdAt: String = "",
    var isActive: Boolean = false,
    var isRead: Boolean = false,
    val messageArr: SnapshotStateList<Message> = mutableStateListOf<Message>()
)


class MatchListVM: ViewModel() {
    val matches = mutableStateListOf<MatchState>()
    var curReceiver by mutableStateOf("")
    var isInit = false

    fun init() {
        if (isInit) return
        isInit = true
        getMatches()
        viewModelScope.launch {
            while (true) {
                try {
                    TokenManager.getToken()?.let { token ->
                        val newMsg =
                            MessagePollingApi.getNewMessage("Bearer $token")
                        val idx = matches.indexOfFirst { it.username == newMsg.sender }
                        if (idx == 0) {
                            matches[idx].messageArr.add(0, newMsg)
                        } else {
                            val match = matches[idx]
                            matches.removeAt(idx)
                            match.messageArr.add(0, newMsg)
                            matches.add(0, match)
                        }
                        matches[0].isRead = false
                    }

                } catch (e: Exception) {
                    Log.d("MsgPolling", e.toString())
                }
            }
        }

    }

    fun getMatches() {
        viewModelScope.launch {
            try {
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
                }
            } catch (e: Exception) {
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
            } catch (e: Exception) {
                Log.d("MsgSending", e.toString())
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

}