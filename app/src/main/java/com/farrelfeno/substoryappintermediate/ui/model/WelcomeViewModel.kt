package com.farrelfeno.substoryappintermediate.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farrelfeno.substoryappintermediate.pref.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeViewModel (
    private val userPreference: UserPreference,
): ViewModel(){
    fun setSession(firstTime: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreference.setSession(firstTime)
        }
    }
}