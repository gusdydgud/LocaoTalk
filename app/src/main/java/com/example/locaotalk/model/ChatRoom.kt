package com.example.locaotalk.model

data class ChatRoom(
    val roomId: String = "",         // 채팅방 ID
    val roomName: String = "",       // 채팅방 이름
    val createdAt: String = "",      // 생성 시간
    val updatedAt: String = ""       // 마지막 업데이트 시간
)