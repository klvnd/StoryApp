package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.Repository
import com.dicoding.storyapp.data.response.LoginResponse
import com.dicoding.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel (private val repository: Repository) : ViewModel(){

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val response = repository.register(name, email, password)
            _registerResponse.postValue(response)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val response = repository.login(email, password)
            _loginResponse.postValue(response)
        }
    }

    fun getStories() = liveData(Dispatchers.IO) {
        emit(repository.getStories())
    }

    fun getStoryDetail(id: String) = liveData(Dispatchers.IO) {
        emit(repository.getStoryDetail(id))
    }
}