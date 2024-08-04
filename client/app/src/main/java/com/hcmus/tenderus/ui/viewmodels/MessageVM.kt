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
import com.hcmus.tenderus.network.ApiClient.MessagePollingApi
import com.hcmus.tenderus.network.ApiClient.MessageSendingApi
import com.hcmus.tenderus.network.MessageSendingRequest
import com.hcmus.tenderus.utils.subtractInMinutes
import kotlinx.coroutines.launch

data class MatchState(
    val username: String = "",
    val avatarIcon: String = "",
    val displayName: String = "",
    val createdAt: String = "",
    val isActive: Boolean = false,
    val messageArr: SnapshotStateList<Message> = mutableStateListOf<Message>()
)


class MatchListVM: ViewModel() {
    val matches = mutableStateListOf<MatchState>()

    init {
        getMatches()
        viewModelScope.launch {
            while (true) {
                try {
                    val newMsg =
                        MessagePollingApi.getNewMessage("Bearer " + TokenManager.getToken()!!)
                    val idx = matches.indexOfFirst { it.username == newMsg.sender }
                    matches[idx].messageArr.add(0, newMsg)
                } catch (e: Exception) {
                    Log.d("MsgPolling", e.toString())
                }
            }
        }

    }

    fun getMatches() {
        viewModelScope.launch {
            val matchesList = GetMatchesApi.getMatches("Bearer " + TokenManager.getToken()!!).toMutableList()
            matchesList.sortWith { a, b ->
                if (a.messageArr.isNotEmpty() && b.messageArr.isNotEmpty() && subtractInMinutes(
                        a.messageArr.last().createdAt,
                        b.messageArr.last().createdAt
                    ) != 0L
                ) {
                    subtractInMinutes(
                        a.messageArr.last().createdAt,
                        b.messageArr.last().createdAt
                    ).toInt()
                } else {
                    subtractInMinutes(a.createdAt, b.createdAt).toInt()
                }

            }
            for (m in matchesList) {
                matches.add(MatchState(m.username, m.avatarIcon, m.displayName, m.createdAt, m.isActive))
                for (msg in m.messageArr) {
                    matches.last().messageArr.add(msg)
                }
            }
        }
    }

    fun sendMessage(req: MessageSendingRequest) {
        viewModelScope.launch {
            val msg = MessageSendingApi.sendMessage("Bearer " + TokenManager.getToken()!!, req)
            matches.first { it.username == req.receiver }.messageArr.add(0, msg)
        }

    }

}