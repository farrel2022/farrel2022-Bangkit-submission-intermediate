package com.farrelfeno.substoryappintermediate.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.farrelfeno.substoryappintermediate.pref.UserPreference
import com.farrelfeno.substoryappintermediate.repository.StoryRepository

class MapsViewModel(
private val storyRepository: StoryRepository,
private val userPreference: UserPreference
) : ViewModel() {
    fun getStory(token: String) = storyRepository.getLocationStory(token)

    fun checkTokenAvailable(): LiveData<String> {
        return userPreference.getToken().asLiveData()
    }
}