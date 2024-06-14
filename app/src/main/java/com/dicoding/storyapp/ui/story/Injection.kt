package com.dicoding.storyapp.ui.story

import android.content.Context
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.data.Repository
import com.dicoding.storyapp.data.api.ApiConfig
import kotlinx.coroutines.flow.first

object Injection {
    suspend fun provideRepository(context: Context): Repository {
        val dataStoreManager = DataStoreManager(context)
        val token = dataStoreManager.tokenFlow.first() ?: ""
        val apiService = ApiConfig.getApiService(token)
        return Repository(apiService)
    }
}