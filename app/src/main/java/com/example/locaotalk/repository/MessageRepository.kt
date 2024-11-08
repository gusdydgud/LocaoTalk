package com.example.locaotalk.repository

import android.util.Log
import com.example.locaotalk.BuildConfig
import com.example.locaotalk.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.FileInputStream
import java.io.InputStream
import java.util.Properties

class MessageRepository {

    // Firebase Realtime Database의 "messages" 경로 참조
    private val database: DatabaseReference = FirebaseDatabase
        .getInstance(BuildConfig.FIREBASE_DATABASE_URL)
        .getReference("messages")

    // 메시지를 지정된 채팅방(roomId)에 추가하는 함수
    fun addMessage(roomId: String, message: Message) {
        database.child(roomId).push().setValue(message)
    }

    // 실시간으로 채팅방(roomId) 내 메시지 업데이트를 수신하는 함수
    fun getRealTimeMessages(roomId: String, callback: (List<Message>) -> Unit) {
        // Firebase 데이터베이스에서 roomId 하위의 메시지 목록을 실시간으로 관찰
        database.child(roomId).addValueEventListener(object : ValueEventListener {

            // 데이터가 변경될 때마다 호출되어 최신 메시지 목록을 가져옴
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()

                // snapshot을 통해 각 메시지를 Message 객체로 변환하여 리스트에 추가
                for (messageSnapshot in snapshot.children) {
                    messageSnapshot.getValue(Message::class.java)?.let { messages.add(it) }
                }

                // 업데이트된 메시지 리스트를 콜백을 통해 전달
                callback(messages)
            }

            // 데이터 로딩 실패 시 호출되며 로그를 통해 에러 메시지 출력
            override fun onCancelled(error: DatabaseError) {
                Log.e("MessageRepository", "Failed to load messages: ${error.message}")
            }
        })
    }
}
