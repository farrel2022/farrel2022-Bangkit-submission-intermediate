package com.farrelfeno.substoryappintermediate.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.farrelfeno.substoryappintermediate.databinding.ActivityLoginBinding
import com.farrelfeno.substoryappintermediate.factory.ViewModelFactory
import com.farrelfeno.substoryappintermediate.pref.UserPreference
import com.farrelfeno.substoryappintermediate.pref.dataStore
import com.farrelfeno.substoryappintermediate.ui.model.LoginViewModel
import com.farrelfeno.substoryappintermediate.result.Result

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(
            applicationContext,
            UserPreference.getInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAction()
        playAnimation()
        setUpUi()
    }
    override fun onResume() {
        super.onResume()
        initialCheck()
    }


    private fun setupAction() {
        binding.apply {
            loginButton.setOnClickListener {
                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()
                viewModel.getUserLogin(email, password).observe(this@LoginActivity) {
                    when (it) {
                        is Result.Error -> {
                            showLoading(false)
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Sorry")
                                setMessage("Fill In Your Email and Password Correctly ")
                                setPositiveButton("OK") { _, _ ->
                                }
                                create()
                                show()
                            }
                        }
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            showLoading(false)
                            val data = it.data
                            viewModel.saveToken(data.loginResult.token)
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Login Success")
                                setMessage("Ready?")
                                setPositiveButton("Next") { _, _ ->
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
    private fun initialCheck() {
        viewModel.setSession().observe(this) {
            if (it) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
    private fun setUpUi() {
        window.insetsController?.hide(WindowInsets.Type.statusBars())
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 2f).setDuration(150)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 2f).setDuration(120)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 2f).setDuration(250)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 2f).setDuration(200)
        val btnlogin = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 2f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, btnlogin
            )
            startDelay = 100
        }.start()
    }
}