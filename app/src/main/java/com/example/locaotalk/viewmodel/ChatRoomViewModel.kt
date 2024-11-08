package com.example.locaotalk.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locaotalk.model.ChatRoom
import com.example.locaotalk.model.Message
import com.example.locaotalk.repository.ChatRoomRepository

class ChatRoomViewModel(private val chatRoomRepository: ChatRoomRepository) : ViewModel() {

    // 모든 채팅방 목록을 관리하는 LiveData
    private val _chatRooms = MutableLiveData<List<ChatRoom>>()
    val chatRooms: LiveData<List<ChatRoom>> get() = _chatRooms

    // 특정 채팅방의 메시지 목록을 관리하는 LiveData
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    // 모든 채팅방을 불러와서 _chatRooms LiveData 업데이트
    fun loadChatRooms() {
        chatRoomRepository.getChatRooms { rooms ->
            _chatRooms.value = rooms
        }
    }

    // 채팅방이 없으면 생성하는 함수
    fun addChatRoomIfNotExists(roomId: String, roomName: String) {
        chatRoomRepository.addChatRoomIfNotExists(roomId, roomName) { chatRoom ->
            chatRoom?.let {
                loadChatRooms() // 새 채팅방이 추가되었으면 목록 업데이트
            }
        }
    }

    // 채팅방에 사용자를 추가하는 함수
    fun addUserToChatRoom(roomId: String, userId: String) {
        chatRoomRepository.addUserToChatRoom(roomId, userId)
    }

    // 특정 채팅방의 메시지를 실시간으로 불러오는 함수
    fun observeMessages(roomId: String) {
        chatRoomRepository.getMessages(roomId) { messages ->
            _messages.value = messages
        }
    }

    // 특정 채팅방에 메시지를 추가하는 함수
    fun sendMessageToChatRoom(roomId: String, message: Message) {
        chatRoomRepository.addMessageToChatRoom(roomId, message)
    }
}
