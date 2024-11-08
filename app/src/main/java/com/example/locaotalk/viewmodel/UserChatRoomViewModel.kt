package com.example.locaotalk.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locaotalk.model.UserChatRoom
import com.example.locaotalk.repository.UserChatRoomRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserChatRoomViewModel(private val userChatRoomRepository: UserChatRoomRepository) : ViewModel() {

    private val _userChatRooms = MutableLiveData<List<UserChatRoom>>()
    val userChatRooms: LiveData<List<UserChatRoom>> get() = _userChatRooms

    // 유저가 참여 중인 채팅방 목록 불러오기
    fun loadUserChatRooms(userId: String) {
        userChatRoomRepository.getUserChatRooms(userId) { rooms ->
            _userChatRooms.value = rooms
        }
    }

    // 특정 채팅방에 유저를 추가하는 메서드
    fun addUserToChatRoom(userChatRoom: UserChatRoom) {
        userChatRoomRepository.addUserToChatRoom(userChatRoom)
    }

    // 유저가 이미 채팅방에 있는지 확인 후 추가하는 메서드
    fun addUserToChatRoomIfNotExists(userId: String, chatRoomId: String) {
        userChatRoomRepository.getUserChatRoom(userId, chatRoomId) { existingUserChatRoom ->
            if (existingUserChatRoom == null) {
                val joinedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val newUserChatRoom = UserChatRoom(userId = userId, roomId = chatRoomId, joinedAt = joinedAt)
                userChatRoomRepository.addUserToChatRoom(newUserChatRoom)
            }
        }
    }
}
