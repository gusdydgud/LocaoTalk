package com.example.locaotalk.repository

import com.example.locaotalk.BuildConfig
import com.example.locaotalk.model.Location
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.FileInputStream
import java.io.InputStream
import java.util.Properties

class LocationRepository {
    private val database: DatabaseReference = FirebaseDatabase
        .getInstance(BuildConfig.FIREBASE_DATABASE_URL)
        .getReference("locations")

    // 위치 정보 저장 또는 업데이트
    fun updateLocation(location: Location) {
        database.child(location.userId).setValue(location)
    }

    // 위치 정보 구독 (다른 사용자들의 위치 업데이트 실시간 수신)
    fun observeLocations(callback: (List<Location>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val locations = mutableListOf<Location>()
                for (locationSnapshot in snapshot.children) {
                    locationSnapshot.getValue(Location::class.java)?.let { locations.add(it) }
                }
                callback(locations)
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })
    }
}
