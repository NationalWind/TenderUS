package com.hcmus.tenderus.network

import com.hcmus.tenderus.model.Match
import com.hcmus.tenderus.model.Message
import com.hcmus.tenderus.model.Profile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header

import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class OKResponse (private val message: String)
data class ActivityOKResponse (val isActive: Boolean)
data class MessageSendingRequest(
    val receiver: String,
    val msgType: String,
    val content: String
)
data class HaveReadMessageRequest(
    val conversationID: String
)

interface GetMatch {
    @GET("api/message/matches")
    suspend fun getMatches(@Header("Authorization") authorization: String): List<Match>

    @GET("api/message/match-profile/{username}")
    suspend fun getMatchProfile(@Header("Authorization") authorization: String, @Path("username") username: String): Profile

}

interface MatchPolling {
    @GET("api/swipe/polling")
    suspend fun getNewMatch(@Header("Authorization") authorization: String): Match
}

interface MessagePolling {
    @GET("api/message/polling")
    suspend fun getNewMessage(@Header("Authorization") authorization: String): Message
}

interface MessageSending {
    @POST("api/message")
    suspend fun sendMessage(@Header("Authorization") authorization: String, @Body req: MessageSendingRequest): Message
}

interface MessageLoading {
    @GET("api/message")
    suspend fun loadMessage(@Header("Authorization") authorization: String, @Query("receiver") receiver: String, @Query("page_size") pageSize: Int, @Query("msgID") msgID: Int ) : List<Message>
}


interface HaveReadMessage {
    @POST("api/message/read")
    suspend fun update(@Header("Authorization") authorization: String, @Body req: HaveReadMessageRequest): OKResponse
}

interface GetActivityStatus {
    @POST("api/message/activity/{matched}")
    suspend fun get(@Header("Authorization") authorization: String, @Path("matched") matched: String): ActivityOKResponse
}