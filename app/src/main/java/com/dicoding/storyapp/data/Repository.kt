package com.dicoding.storyapp.data

import com.dicoding.storyapp.data.api.ApiService
import com.dicoding.storyapp.data.response.ErrorResponse
import com.dicoding.storyapp.data.response.LoginResponse
import com.dicoding.storyapp.data.response.RegisterResponse
import com.google.gson.Gson
import retrofit2.HttpException

class Repository(private val apiService: ApiService) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return try {
            apiService.register(name, email, password)
        } catch (e: HttpException) {
            if (e.code() == 400) {
                val errorResponse = parseErrorResponse(e)
                RegisterResponse(error = true, message = errorResponse.message)
            } else {
                RegisterResponse(error = true, message = e.message())
            }
        } catch (e: Exception) {
            RegisterResponse(error = true, message = e.message)
        }
    }

    private fun parseErrorResponse(exception: HttpException): ErrorResponse {
        return try {
            val errorBody = exception.response()?.errorBody()?.string()
            Gson().fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            ErrorResponse(error = true, message = "Unknown error occurred")
        }
    }

    // Add the login method if necessary
    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            apiService.login(email, password)
        } catch (e: Exception) {
            LoginResponse(error = true, message = e.message)
        }
    }
}