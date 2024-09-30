package com.farrelfeno.substoryappintermediate.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.farrelfeno.substoryappintermediate.pref.UserPreference
import com.farrelfeno.substoryappintermediate.repository.StoryRepository
import com.farrelfeno.substoryappintermediate.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel (
    private val userPreference: UserPreference,
    private val storyRepository: StoryRepository
): ViewModel(){
    fun setToken(): LiveData<String?> {
        return userPreference.getToken().asLiveData()
    }
    fun clearToken() {
        viewModelScope.launch {
            userPreference.clearToken()
        }
    }
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> = storyRepository.getStory(token).cachedIn(viewModelScope)
}