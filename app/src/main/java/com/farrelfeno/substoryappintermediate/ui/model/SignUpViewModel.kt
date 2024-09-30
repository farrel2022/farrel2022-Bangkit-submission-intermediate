package com.farrelfeno.substoryappintermediate.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.farrelfeno.substoryappintermediate.injection.Injection

import com.farrelfeno.substoryappintermediate.repository.UserRepository

class SignUpViewModel (
    private val userRepository: UserRepository,
) : ViewModel(){
    fun saveRegister(name: String, email: String, password: String) =
        userRepository.saveRegister(name, email, password)

    class SignUpViewModelFactory private constructor(
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
            private var instance: SignUpViewModelFactory? = null

            fun getInstance(): SignUpViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: SignUpViewModelFactory(
                        Injection.provideUserRepository()
                    )
                }
        }
    }
}