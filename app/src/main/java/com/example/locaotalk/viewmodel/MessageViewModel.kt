package com.example.locaotalk.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locaotalk.model.Message
import com.example.locaotalk.model.File
import com.example.locaotalk.repository.MessageRepository
import com.example.locaotalk.repository.FileRepository
import com.google.firebase.auth.FirebaseAuth

class MessageViewModel(
    private val messageRepository: MessageRepository,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "unknownUser"

    // 실시간으로 채팅방 메시지를 관찰하여 업데이트하는 함수
    fun observeMessages(roomId: String) {
        messageRepository.getRealTimeMessages(roomId) { messages ->
            _messages.value = messages
        }
    }

    // 텍스트 메시지 전송 함수
    fun sendMessage(roomId: String, content: String) {
        val message = Message(
            roomId = roomId,
            senderId = currentUserId,
            content = content,
            contentType = "text",
            sentAt = System.currentTimeMillis().toString()
        )
        messageRepository.addMessage(roomId, message)
    }

    // 파일 메시지 전송 함수 (사진 전송)
    fun sendFileMessage(roomId: String, base64ImageData: String) {
        val fileMessage = File(
            fileId = "file_${System.currentTimeMillis()}",
            messageId = "",
            fileData = base64ImageData,
            fileType = "image",
            uploadedAt = System.currentTimeMillis().toString()
        )

        fileRepository.addFile(fileMessage)

        val messageWithImage = Message(
            roomId = roomId,
            senderId = currentUserId,
            content = "",
            contentType = "image",
            fileUrl = fileMessage.fileData,
            sentAt = System.currentTimeMillis().toString()
        )
        messageRepository.addMessage(roomId, messageWithImage)
    }
}
