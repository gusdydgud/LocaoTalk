package com.example.locaotalk.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.locaotalk.repository.MessageRepository
import com.example.locaotalk.repository.FileRepository
import com.example.locaotalk.viewmodel.MessageViewModel

class MessageViewModelFactory(
    private val messageRepository: MessageRepository,
    private val fileRepository: FileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(messageRepository, fileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
