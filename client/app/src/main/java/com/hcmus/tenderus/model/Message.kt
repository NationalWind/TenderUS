package com.hcmus.tenderus.model

import com.google.gson.annotations.SerializedName



data class Message(
    var _id:            String = "",
    var conversationID: String = "",
    var msgID:          Int = 0,
    var sender:         String = "",
    var receiver:       String = "",
    var msgType:        String = "",
    var content:        String = "",
    var createdAt:      String = "",
)