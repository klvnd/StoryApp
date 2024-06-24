package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.Repository
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.data.response.LoginResponse
import com.dicoding.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch

class UserViewModel(private val repository: Repository) : ViewModel() {

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse
    val loginResponse: LiveData<LoginResponse> = _loginResponse

//    val getStories: LiveData<PagingData<ListStoryItem>> =
//        repository.getStories().cachedIn(viewModelScope)

    val getStories: LiveData<PagingData<ListStoryItem>> by lazy {
        repository.getStories().cachedIn(viewModelScope)
    }

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
}