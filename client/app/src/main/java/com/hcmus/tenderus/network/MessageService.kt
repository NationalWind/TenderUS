package com.hcmus.tenderus.network

import com.hcmus.tenderus.model.Match
import com.hcmus.tenderus.model.Message
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header

import retrofit2.http.POST
data class OKResponse (private val message: String)
data class MessageSendingRequest(
    val receiver: String,
    val msgType: String,
    val content: String
)
interface GetMatch {
    @GET("api/message/matches")
    suspend fun getMatches(@Header("Authorization") authorization: String): List<Match>
}

interface MessagePolling {
    @GET("api/message/polling")
    suspend fun getNewMessage(@Header("Authorization") authorization: String): Message
}

interface MessageSending {
    @POST("api/message")
    suspend fun sendMessage(@Header("Authorization") authorization: String, @Body req: MessageSendingRequest): Message
}


interface HaveReadMessage {
    @POST("api/message")
    suspend fun update(@Header("Authorization") authorization: String, @Body conversationID: String): OKResponse
}