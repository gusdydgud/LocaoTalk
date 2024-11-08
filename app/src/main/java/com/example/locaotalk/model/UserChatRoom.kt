package com.example.locaotalk.model

data class UserChatRoom(
    val userId: String = "",        // 사용자 ID
    val roomId: String = "",        // 채팅방 ID
    val joinedAt: String = ""       // 참여 시간
)