package com.example.locaotalk.viewmodel
import com.example.locaotalk.BuildConfig
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locaotalk.model.Location // Location 모델 필요
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LocationViewModel : ViewModel() {
    private val database: DatabaseReference = FirebaseDatabase
        .getInstance(BuildConfig.FIREBASE_DATABASE_URL)
        .getReference("locations")
    private val _userLocations = MutableLiveData<Map<String, Location>>()
    val userLocations: LiveData<Map<String, Location>> get() = _userLocations

    // 위치 관찰 시작
    fun observeUserLocations() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val locations = mutableMapOf<String, Location>()
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key ?: continue
                    val latitude = userSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = userSnapshot.child("longitude").getValue(Double::class.java)
                    val status = userSnapshot.child("status").getValue(String::class.java) ?: "offline"
                    if (latitude != null && longitude != null && status == "online") {
                        locations[userId] = Location(userId, latitude, longitude)
                    }
                }
                _userLocations.value = locations
                Log.d("LocationViewModel", "Locations updated: $locations")
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
            }
        })
    }
}
