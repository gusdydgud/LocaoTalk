package com.example.locaotalk.model

data class File(
    val fileId: String = "",         // 파일 ID
    val messageId: String = "",      // 연결된 메시지 ID
    val fileData: String = "",       // Base64로 인코딩된 파일 데이터
    val fileType: String = "",       // 파일 타입 (예: image, video)
    val uploadedAt: String = ""      // 업로드 시간
)