package com.example.locaotalk.repository

import com.example.locaotalk.BuildConfig
import com.example.locaotalk.model.UserChatRoom
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.FileInputStream
import java.io.InputStream
import java.util.Properties

class UserChatRoomRepository {
    private val database: DatabaseReference = FirebaseDatabase
        .getInstance(BuildConfig.FIREBASE_DATABASE_URL)
        .getReference("user_chat_rooms")


    // 특정 유저를 특정 채팅방에 추가
    fun addUserToChatRoom(userChatRoom: UserChatRoom) {
        database.child(userChatRoom.roomId).child(userChatRoom.userId).setValue(userChatRoom)
    }

    // 특정 유저가 특정 채팅방에 있는지 확인
    fun getUserChatRoom(userId: String, roomId: String, callback: (UserChatRoom?) -> Unit) {
        database.child(roomId).child(userId).get().addOnSuccessListener { snapshot ->
            val userChatRoom = snapshot.getValue(UserChatRoom::class.java)
            callback(userChatRoom)
        }.addOnFailureListener {
            callback(null) // 실패 시 null 반환
        }
    }

    // 특정 유저가 참여 중인 모든 채팅방 가져오기
    fun getUserChatRooms(userId: String, callback: (List<UserChatRoom>) -> Unit) {
        database.orderByChild("userId").equalTo(userId).get().addOnSuccessListener {
            val chatRooms = it.children.mapNotNull { snap -> snap.getValue(UserChatRoom::class.java) }
            callback(chatRooms)
        }
    }
}
