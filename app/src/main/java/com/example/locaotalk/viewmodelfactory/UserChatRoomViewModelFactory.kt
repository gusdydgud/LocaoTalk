package com.example.locaotalk.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.locaotalk.repository.UserChatRoomRepository
import com.example.locaotalk.viewmodel.UserChatRoomViewModel

class UserChatRoomViewModelFactory(
    private val userChatRoomRepository: UserChatRoomRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserChatRoomViewModel::class.java)) {
            return UserChatRoomViewModel(userChatRoomRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
