// Location.kt
package com.example.locaotalk.model

data class Location(
    val userId: String = "",       // 사용자 ID
    val latitude: Double = 0.0,    // 위도
    val longitude: Double = 0.0,   // 경도
    val updatedAt: String = ""     // 위치 업데이트 시간
)
