package com.example.locaotalk.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locaotalk.adapter.MessageAdapter
import com.example.locaotalk.databinding.ActivityChatBinding
import com.example.locaotalk.repository.FileRepository
import com.example.locaotalk.repository.MessageRepository
import com.example.locaotalk.viewmodel.MessageViewModel
import com.example.locaotalk.viewmodelfactory.MessageViewModelFactory
import java.io.ByteArrayOutputStream

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    // 메시지 및 파일 전송을 위한 리포지토리 초기화
    private val messageRepository by lazy { MessageRepository() }
    private val fileRepository by lazy { FileRepository() }

    // MessageViewModel 초기화 (메시지 관리 ViewModel)
    private val messageViewModel: MessageViewModel by viewModels {
        MessageViewModelFactory(messageRepository, fileRepository)
    }

    // 이전 액티비티에서 전달된 채팅방 ID와 채팅 상대방 ID
    private lateinit var chatRoomId: String
    private lateinit var chatUserId: String // 추가된 userId



    // 갤러리에서 이미지 선택 결과를 처리하는 Activity Result Launcher
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // 선택한 이미지 URI로부터 비트맵 디코딩
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // 비트맵을 Base64로 변환
                val base64Image = encodeImageToBase64(bitmap)
                if (base64Image != null) {
                    // 이미지를 Firebase에 메시지로 전송
                    messageViewModel.sendFileMessage(chatRoomId, base64Image)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 chatRoomId와 chatUserId 가져오기
        chatRoomId = intent.getStringExtra("chatRoomId") ?: return
        chatUserId = intent.getStringExtra("chatUserId") ?: "unknownUser"

        setupRecyclerView() // 채팅 목록 RecyclerView 설정

        // 채팅방에 대한 실시간 메시지 업데이트 관찰 시작
        messageViewModel.observeMessages(chatRoomId)
        observeMessages() // ViewModel의 메시지 데이터 실시간 업데이트

        // 메시지 전송 버튼 클릭 리스너 설정
        binding.buttonSend.setOnClickListener {
            sendTextMessage()
        }

        // 이미지 추가 버튼 클릭 리스너 설정
        binding.buttonAdd.setOnClickListener {
            openGallery()
        }
        binding.buttonBack.setOnClickListener{
            finish()
        }
    }

    // RecyclerView 초기화 및 어댑터 설정 함수
    private fun setupRecyclerView() {
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
        val chatAdapter = MessageAdapter()
        binding.recyclerViewChat.adapter = chatAdapter
    }

    // ViewModel의 메시지 리스트를 실시간으로 관찰하여 RecyclerView 업데이트
    private fun observeMessages() {
        messageViewModel.messages.observe(this) { messages ->
            (binding.recyclerViewChat.adapter as MessageAdapter).submitList(messages)
        }
    }

    // 텍스트 메시지 전송 함수
    private fun sendTextMessage() {
        val messageText = binding.editTextMessage.text.toString()
        if (messageText.isNotEmpty()) {
            messageViewModel.sendMessage(chatRoomId, messageText) // ViewModel을 통해 텍스트 메시지 전송
            binding.editTextMessage.text.clear() // 입력창 초기화
        }
    }

    // 갤러리를 열어 이미지 선택하는 함수
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    // 비트맵을 Base64 문자열로 인코딩하는 함수
    private fun encodeImageToBase64(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
