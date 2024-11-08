package com.example.locaotalk.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.locaotalk.repository.ChatRoomRepository
import com.example.locaotalk.viewmodel.ChatRoomViewModel

class ChatRoomViewModelFactory(
    private val chatRoomRepository: ChatRoomRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
            return ChatRoomViewModel(chatRoomRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
