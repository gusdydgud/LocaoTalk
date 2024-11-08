package com.example.locaotalk.model

data class Message(
    val messageId: String = "",      // 메시지 ID
    val roomId: String = "",         // 채팅방 ID
    val senderId: String = "",       // 보낸 사람 ID
    val content: String = "",        // 메시지 내용
    val contentType: String = "text", // 메시지 타입 (예: text, image)
    val sentAt: String = "",         // 전송 시간
    val fileUrl: String? = null      // 파일 URL (선택 사항)
)