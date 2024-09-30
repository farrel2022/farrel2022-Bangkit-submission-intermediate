package com.farrelfeno.substoryappintermediate.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.farrelfeno.substoryappintermediate.pagging.StoryPagingSource
import com.farrelfeno.substoryappintermediate.response.AddStoryResponse
import com.farrelfeno.substoryappintermediate.response.ErrorResponse
import com.farrelfeno.substoryappintermediate.response.ListStoryItem
import com.farrelfeno.substoryappintermediate.retrofit.ApiService
import com.farrelfeno.substoryappintermediate.result.Result
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.lang.Exception

class StoryRepository
private constructor(
    private val apiService: ApiService
){
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {

        return Pager(
            config = PagingConfig(pageSize = 4),

            pagingSourceFactory = {
                StoryPagingSource(apiService, token )
            }
        ).liveData
    }

    companion object {

        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(
            apiService: ApiService
        ): StoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(apiService)
            }.also { INSTANCE = it }
    }

    fun postStory(
                token: String,
                imageFile: File,
                description: String
            ): LiveData<Result<AddStoryResponse>> = liveData {
                emit(Result.Loading)

                val textPlainMediaType = "text/plain".toMediaType()
                val imageMediaType = "image/jpeg".toMediaTypeOrNull()
                val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", imageFile.name, imageFile.asRequestBody(imageMediaType))
                val descriptionRequestBody = description.toRequestBody(textPlainMediaType)

                try {
                    val response = apiService.postStory(
                        token,
                        imageMultiPart,
                        descriptionRequestBody
                    )

                    if (response.error) {
                        val errorResponse = ErrorResponse(response.error, response.message)
                        emit(Result.Error(errorResponse.message ?: "error"))
                    } else {
                        emit(Result.Success(response))
                    }
                } catch (e: Exception) {
                    emit(Result.Error(e.message ?: "error"))
                }
            }

    fun getLocationStory(token: String): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation(token)
            if (response.error) {
                val errorResponse = ErrorResponse(response.error, response.message)
                emit(Result.Error(errorResponse.message ?: "error"))
            } else {
                val stories = response.listStory
                emit(Result.Success(stories))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "error"))
        }
    }

}