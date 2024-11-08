package com.example.locaotalk.repository

import com.example.locaotalk.BuildConfig
import com.example.locaotalk.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.FileInputStream
import java.io.InputStream
import java.util.Properties

class UserRepository {
    private val database: DatabaseReference = FirebaseDatabase
        .getInstance(BuildConfig.FIREBASE_DATABASE_URL)
        .getReference("users")


    // 사용자 추가
    fun addUser(user: User) {
        database.child(user.userId).setValue(user)
    }

    // 사용자 정보 업데이트
    fun updateUser(user: User) {
        database.child(user.userId).setValue(user)
    }

    // 사용자 정보 불러오기
    fun getUser(userId: String, callback: (User?) -> Unit) {
        database.child(userId).get().addOnSuccessListener {
            callback(it.getValue(User::class.java))
        }
    }
}
