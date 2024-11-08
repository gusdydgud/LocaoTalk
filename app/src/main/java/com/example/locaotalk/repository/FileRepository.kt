package com.example.locaotalk.repository

import com.example.locaotalk.BuildConfig
import com.example.locaotalk.model.File
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.FileInputStream
import java.io.InputStream
import java.util.Properties

class FileRepository {
    private val database: DatabaseReference = FirebaseDatabase
        .getInstance(BuildConfig.FIREBASE_DATABASE_URL)
        .getReference("files")
    // 파일 추가
    fun addFile(file: File) {
        database.child(file.fileId).setValue(file)
    }

    // 파일 가져오기
    fun getFile(fileId: String, callback: (File?) -> Unit) {
        database.child(fileId).get().addOnSuccessListener {
            callback(it.getValue(File::class.java))
        }
    }
}
