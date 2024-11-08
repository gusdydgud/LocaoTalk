package com.example.locaotalk.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locaotalk.model.User
import com.example.locaotalk.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun loadUser(userId: String) {
        userRepository.getUser(userId) { fetchedUser ->
            _user.value = fetchedUser
        }
    }

    fun updateUser(user: User) {
        userRepository.updateUser(user)
    }
}
