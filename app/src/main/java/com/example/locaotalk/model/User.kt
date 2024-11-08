package com.example.locaotalk.model

data class User(
    val userId: String = "",          // 사용자 고유 ID
    val username: String = "",         // 사용자 이름
    val profileImageData: String = "", // Base64로 인코딩된 프로필 이미지 데이터
    val status: String = "offline",    // 사용자 상태 (예: online, offline)
    val lastActive: String = ""        // 마지막 활동 시간
)