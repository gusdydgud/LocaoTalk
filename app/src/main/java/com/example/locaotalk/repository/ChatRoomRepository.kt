package com.example.locaotalk.repository
import com.example.locaotalk.BuildConfig
import android.util.Log
import com.example.locaotalk.model.ChatRoom
import com.example.locaotalk.model.Message
import com.google.firebase.database.*


class ChatRoomRepository {

    // Firebase Realtime Database에서 "chat_rooms" 경로를 참조
    private val database: DatabaseReference = FirebaseDatabase
        .getInstance(BuildConfig.FIREBASE_DATABASE_URL)
        .getReference("chat_rooms")
    // 채팅방이 없으면 새로 생성하고, 있으면 기존 채팅방을 반환하는 함수
    fun addChatRoomIfNotExists(roomId: String, roomName: String, callback: (ChatRoom?) -> Unit) {
        database.child(roomId).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // 채팅방이 없을 경우 새로 생성
                val chatRoom = ChatRoom(
                    roomId = roomId,
                    roomName = roomName,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = System.currentTimeMillis().toString()
                )
                // Firebase에 새 채팅방 추가
                database.child(roomId).setValue(chatRoom).addOnSuccessListener {
                    callback(chatRoom) // 새로 생성된 채팅방 반환
                }.addOnFailureListener {
                    callback(null) // 실패 시 null 반환
                }
            } else {
                // 이미 존재하는 채팅방을 반환
                snapshot.getValue(ChatRoom::class.java)?.let { existingChatRoom ->
                    callback(existingChatRoom)
                }
            }
        }.addOnFailureListener {
            callback(null) // 데이터베이스 접근 실패 시 null 반환
        }
    }

    // 특정 채팅방에 유저를 추가하는 함수
    fun addUserToChatRoom(roomId: String, userId: String) {
        // 채팅방 내 "participants" 경로에 해당 유저 ID를 저장
        database.child(roomId).child("participants").child(userId).setValue(true)
            .addOnSuccessListener {
                Log.d("ChatRoomRepository", "User $userId added to chat room $roomId")
            }
            .addOnFailureListener { e ->
                Log.e("ChatRoomRepository", "Failed to add user $userId to chat room $roomId", e)
            }
    }

    // 특정 채팅방의 모든 메시지를 실시간으로 가져오는 함수
    fun getMessages(roomId: String, callback: (List<Message>) -> Unit) {
        database.child(roomId).child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                // 각 메시지를 Message 객체로 변환하여 리스트에 추가
                for (messageSnapshot in snapshot.children) {
                    messageSnapshot.getValue(Message::class.java)?.let { messages.add(it) }
                }
                callback(messages) // 업데이트된 메시지 리스트 반환
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatRoomRepository", "Failed to load messages for room $roomId: ${error.message}")
            }
        })
    }

    // 특정 채팅방에 메시지를 추가하는 함수
    fun addMessageToChatRoom(roomId: String, message: Message) {
        // 메시지를 Firebase에 추가
        database.child(roomId).child("messages").push().setValue(message)
            .addOnSuccessListener {
                Log.d("ChatRoomRepository", "Message added to chat room $roomId")
            }
            .addOnFailureListener { e ->
                Log.e("ChatRoomRepository", "Failed to add message to chat room $roomId", e)
            }
    }

    // 모든 채팅방의 목록을 가져오는 함수
    fun getChatRooms(callback: (List<ChatRoom>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 각 채팅방 데이터를 ChatRoom 객체로 변환하여 리스트에 추가
                val chatRooms = snapshot.children.mapNotNull { it.getValue(ChatRoom::class.java) }
                callback(chatRooms) // 채팅방 리스트 반환
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatRoomRepository", "Failed to load chat rooms: ${error.message}")
            }
        })
    }
}
