package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.Repository
import com.dicoding.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch

class UserViewModel (private val repository: Repository) : ViewModel(){

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val response = repository.register(name, email, password)
            _registerResponse.postValue(response)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val response = repository.login(email, password)
            // Handle response
        }
    }

}