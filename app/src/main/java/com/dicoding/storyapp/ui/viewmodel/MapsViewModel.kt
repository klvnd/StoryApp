package com.dicoding.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.Repository
import com.dicoding.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: Repository) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    fun fetchStoriesWithLocation(location: Int = 1) {
        viewModelScope.launch {
            try {
                val response = repository.getStoriesWithLocation(location)
                _stories.value = response.listStory?.filterNotNull()
            } catch (e: Exception) {
                // Handle the error
            }
        }
    }
}