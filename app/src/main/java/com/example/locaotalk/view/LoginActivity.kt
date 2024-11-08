package com.example.locaotalk.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.locaotalk.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이미 로그인된 상태라면 바로 MapActivity로 이동
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 로그인 버튼 클릭 시 익명 로그인 처리
        binding.buttonLogin.setOnClickListener {
            signInAnonymously()
        }
    }

    private fun signInAnonymously() {
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공: MapActivity로 이동
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                    finish() // 로그인 화면 종료
                } else {
                    // 로그인 실패 처리
                    Toast.makeText(this, "로그인 실패. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
