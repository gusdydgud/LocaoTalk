package com.example.locaotalk.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.locaotalk.BuildConfig
import com.example.locaotalk.R
import com.example.locaotalk.databinding.ActivityMainBinding
import com.example.locaotalk.repository.ChatRoomRepository
import com.example.locaotalk.repository.UserChatRoomRepository
import com.example.locaotalk.viewmodel.ChatRoomViewModel
import com.example.locaotalk.viewmodel.LocationViewModel
import com.example.locaotalk.viewmodel.UserChatRoomViewModel
import com.example.locaotalk.viewmodelfactory.ChatRoomViewModelFactory
import com.example.locaotalk.viewmodelfactory.UserChatRoomViewModelFactory
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationViewModel: LocationViewModel by viewModels() // 위치 ViewModel 인스턴스
    // 위치가 처음 업데이트된 경우에만 화면 이동을 위한 플래그
    private var isFirstLocationUpdate = true
    // ChatRoomViewModel 인스턴스 생성 (채팅방 관리 ViewModel)
    private val chatRoomViewModel: ChatRoomViewModel by viewModels {
        ChatRoomViewModelFactory(ChatRoomRepository())
    }

    // UserChatRoomRepository와 UserChatRoomViewModel 인스턴스 생성 (사용자 채팅방 관리 ViewModel)
    private val userChatRoomRepository by lazy { UserChatRoomRepository() }
    private val userChatRoomViewModel: UserChatRoomViewModel by viewModels {
        UserChatRoomViewModelFactory(userChatRoomRepository)
    }

    // Firebase Database 참조 경로 설정
    private val database: DatabaseReference = FirebaseDatabase
        .getInstance(BuildConfig.FIREBASE_DATABASE_URL)
        .getReference("locations")
    // 위치 업데이트 콜백 (위치 업데이트 시 호출됨)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                val currentLocation = LatLng(it.latitude, it.longitude)
                if (!currentLocation.latitude.isNaN() && !currentLocation.longitude.isNaN()) {
                    updateMyLocation(currentLocation)
                    if (isFirstLocationUpdate) {
                        moveToMyLocation(currentLocation) // 처음에만 화면 이동
                        isFirstLocationUpdate = false // 이후에는 화면 이동하지 않도록 플래그를 변경
                    }
                }
            }
        }
    }

    // 내 위치를 표시할 마커
    private val myLocationMarker = Marker()

    // 다른 사용자들의 위치를 저장하는 마커 Map
    private val userMarkers = mutableMapOf<String, Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 위치 제공자 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // 지도 뷰 초기화
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this) // 지도 준비되면 콜백 호출

        // 내 위치로 이동 버튼 클릭 시 위치 가져오기
        binding.buttonMyLocation.setOnClickListener {
            getCurrentLocation()
        }

        // 위치 권한 확인 후 위치 업데이트 시작
        if (checkLocationPermission()) {
            startLocationUpdates()
        } else {
            requestLocationPermission() // 권한이 없으면 요청
        }

        // ViewModel에서 다른 사용자 위치 실시간 관찰
        locationViewModel.observeUserLocations()
    }

    // 지도 준비 완료 시 호출되는 콜백
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = false
        observeUserLocations()
    }

    // 위치 권한이 있는지 확인하는 함수
    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // 위치 권한을 요청하는 함수
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    // 위치 업데이트 시작 함수
    private fun startLocationUpdates() {
        if (!checkLocationPermission()) return

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).setMinUpdateIntervalMillis(5000).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    // 현재 위치를 가져오는 함수
    private fun getCurrentLocation() {
        if (!checkLocationPermission()) return // 권한이 없으면 실행하지 않음

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLocation = LatLng(it.latitude, it.longitude)
                if (!currentLocation.latitude.isNaN() && !currentLocation.longitude.isNaN()) {
                    moveToMyLocation(currentLocation) // 카메라를 내 위치로 이동
                    updateMyLocation(currentLocation) // Firebase에 내 위치 업데이트
                }
            }
        }
    }

    // Firebase에 내 위치를 업데이트하는 함수
    private fun updateMyLocation(location: LatLng) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknownUser"
        if (!location.latitude.isNaN() && !location.longitude.isNaN()) {
            // 사용자의 위치와 상태 정보
            val locationData = mapOf(
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "status" to "online" // 온라인 상태
            )
            database.child(userId).setValue(locationData)
        }
    }

    // 다른 사용자들의 위치를 ViewModel로부터 실시간 관찰
    private fun observeUserLocations() {
        locationViewModel.userLocations.observe(this) { locations ->
            // 기존 마커 초기화
            userMarkers.values.forEach { it.map = null }
            userMarkers.clear()

            // ViewModel로부터 가져온 위치 데이터를 기반으로 지도에 마커 표시
            locations.forEach { (userId, location) ->
                val position = LatLng(location.latitude, location.longitude)
                val marker = addMarker(position, "User: $userId", userId)
                userMarkers[userId] = marker
            }
        }
    }

    // 지도에 마커를 추가하고 마커 클릭 시 ChatActivity로 이동하는 리스너를 설정합니다.
    private fun addMarker(position: LatLng, title: String, userId: String): Marker {
        val marker = Marker()
        marker.position = position
        marker.map = naverMap
        marker.captionText = title

        marker.setOnClickListener {
            val chatRoomId = "chat_room_${userId}" // 각 사용자에게 고유한 채팅방 ID 생성
            Log.d("MapActivity", "Marker clicked for userId: $userId")

            // 채팅방이 없으면 생성하고 사용자를 추가
            chatRoomViewModel.addChatRoomIfNotExists(chatRoomId, title) // 채팅방이 없으면 생성
            userChatRoomViewModel.addUserToChatRoomIfNotExists(FirebaseAuth.getInstance().currentUser?.uid ?: "unknownUser", chatRoomId) // 사용자를 추가

            // 채팅 액티비티로 고유한 채팅방 ID를 전달하여 시작
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("chatRoomId", chatRoomId)
                putExtra("chatUserId", userId)
            }
            startActivity(intent)
            true
        }
        return marker
    }

    // 내 위치로 카메라를 이동하는 함수
    private fun moveToMyLocation(location: LatLng) {
        if (!location.latitude.isNaN() && !location.longitude.isNaN()) {
            val cameraUpdate = CameraUpdate.scrollTo(location)
            naverMap.moveCamera(cameraUpdate)

            myLocationMarker.position = location
            myLocationMarker.map = naverMap
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates() // 권한이 허용되면 위치 업데이트 시작
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    // 사용자 상태를 업데이트하는 함수
    private fun updateStatus(status: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknownUser"
        database.child(userId).child("status").setValue(status)
    }

    // MapView 생명주기 콜백 함수
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online") // 앱이 활성화되었을 때 온라인 상태로 업데이트
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline") // 앱이 백그라운드로 갈 때 오프라인 상태로 업데이트
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
