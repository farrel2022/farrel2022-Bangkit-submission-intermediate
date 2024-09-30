package com.farrelfeno.substoryappintermediate.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.farrelfeno.substoryappintermediate.pref.UserPreference
import com.farrelfeno.substoryappintermediate.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
private val userRepository: UserRepository,
private val userPreference: UserPreference,
) : ViewModel()
{
    fun getUserLogin(email: String, password: String) =
        userRepository.getUserLogin(email, password)

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreference.saveToken(token)
        }
    }

    fun setSession(): LiveData<Boolean> {
        return userPreference.isFirstTime().asLiveData()
    }
}