package com.farrelfeno.substoryappintermediate.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.farrelfeno.substoryappintermediate.injection.Injection
import com.farrelfeno.substoryappintermediate.pref.UserPreference
import com.farrelfeno.substoryappintermediate.repository.StoryRepository
import com.farrelfeno.substoryappintermediate.repository.UserRepository
import com.farrelfeno.substoryappintermediate.ui.model.AddStoryViewModel
import com.farrelfeno.substoryappintermediate.ui.model.LoginViewModel
import com.farrelfeno.substoryappintermediate.ui.model.MainViewModel
import com.farrelfeno.substoryappintermediate.ui.model.MapsViewModel
import com.farrelfeno.substoryappintermediate.ui.model.SignUpViewModel
import com.farrelfeno.substoryappintermediate.ui.model.WelcomeViewModel

class ViewModelFactory (
    private val userRepository: UserRepository,
    private val userPreference: UserPreference,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            WelcomeViewModel(userPreference) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(userRepository, userPreference) as T
        } else if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            SignUpViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(userPreference, storyRepository) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            AddStoryViewModel(storyRepository, userPreference) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            MapsViewModel(storyRepository, userPreference) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context, userPreference: UserPreference): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideUserRepository(),
                    userPreference,
                    Injection.provideStoryRepository(context)
                )
            }
    }
}