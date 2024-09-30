package com.farrelfeno.substoryappintermediate.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.farrelfeno.substoryappintermediate.injection.Injection
import com.farrelfeno.substoryappintermediate.pref.UserPreference
import com.farrelfeno.substoryappintermediate.repository.StoryRepository
import com.farrelfeno.substoryappintermediate.repository.UserRepository
import java.io.File

class AddStoryViewModel (
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
): ViewModel() {
    class AddStoryViewModelFactory private constructor(
        private val userRepository: UserRepository
    ): ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                return SignUpViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: AddStoryViewModelFactory? = null

            fun getInstance(instance: UserPreference): AddStoryViewModelFactory =
                this.instance ?: synchronized(this) {
                    this.instance ?: AddStoryViewModelFactory(
                        Injection.provideUserRepository()
                    )
                }
        }
    }
    fun tokenAvailable(): LiveData<String?> {
        return userPreference.getToken().asLiveData()
    }
    fun postStory(token: String, imageFile: File, description: String) =
        storyRepository.postStory(token, imageFile, description)
}