package com.farrelfeno.substoryappintermediate.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.farrelfeno.substoryappintermediate.response.ErrorResponse
import com.farrelfeno.substoryappintermediate.response.LoginResponse
import com.farrelfeno.substoryappintermediate.response.RegisterResponse
import com.farrelfeno.substoryappintermediate.retrofit.ApiService
import com.farrelfeno.substoryappintermediate.result.Result
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


class UserRepository private constructor(private val apiService: ApiService) {
    private val loginLiveData = MediatorLiveData<Result<LoginResponse>>()
    private val registerLiveData = MediatorLiveData<Result<RegisterResponse>>()

    fun saveRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> {
        registerLiveData.value = Result.Loading
        val client = apiService.setRegister(name, email, password)

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                try {
                    if (response.isSuccessful) {
                        val register = response.body()
                        if (register != null) {
                            registerLiveData.value = Result.Success(register)
                        } else {
                            registerLiveData.value = Result.Error(ERROR_MESSAGE)
                            Log.e(TAG, "Failed: Register Info is null")
                        }
                    } else {
                        val jsonInString = response.errorBody()?.string()
                        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                        val errorMessage = errorBody.message
                        Log.d(TAG, "Login Error: $errorMessage")
                        registerLiveData.value = Result.Error("An error occurred")
                    }
                } catch (e: Exception) {
                    registerLiveData.value = Result.Error(ERROR_MESSAGE)
                    Log.e(TAG, "Failed: ${e.message}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerLiveData.value = Result.Error(ERROR_MESSAGE)
                Log.e(TAG, "Failed: Response Failure - ${t.message.toString()}")
            }
        })

        return registerLiveData
    }

    private fun handleErrorResponse(response: Response<LoginResponse>) {
        try {
            val jsonInString = response.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Log.d(TAG, "Login Error: $errorMessage")
            loginLiveData.value = Result.Error("An error occurred")
        } catch (e: HttpException) {
            loginLiveData.value = Result.Error(ERROR_MESSAGE)
            Log.e(TAG, "Failed: Response Failure - ${e.message()}")
        }
    }
    fun getUserLogin(
        email: String,
        password: String
    ): LiveData<Result<LoginResponse>> {
        loginLiveData.value = Result.Loading
        val client = apiService.setLogin(
            email,
            password
        )

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginInfo = response.body()
                    if (loginInfo != null) {
                        loginLiveData.value = Result.Success(loginInfo)
                    } else {
                        handleErrorResponse(response)
                        loginLiveData.value = Result.Error(ERROR_MESSAGE)
                        Log.e(TAG, "Failed: Login Info is null")
                    }
                } else {
                    handleErrorResponse(response)
                    loginLiveData.value = Result.Error(ERROR_MESSAGE)
                    Log.e(TAG, "Failed: Response Unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginLiveData.value = Result.Error(ERROR_MESSAGE)
                Log.e(TAG, "Failed: Response Failure - ${t.message.toString()}")
            }

        })
        return loginLiveData
    }
    companion object {
        private val TAG = UserRepository::class.java.simpleName
        private const val ERROR_MESSAGE = "Failed, please try again."
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(apiService: ApiService) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(apiService)
            }.also { INSTANCE = it }
    }
}



