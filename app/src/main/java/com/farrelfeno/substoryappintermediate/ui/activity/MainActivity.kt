package com.farrelfeno.substoryappintermediate.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.farrelfeno.substoryappintermediate.R
import com.farrelfeno.substoryappintermediate.adapter.LoadingStateAdapter
import com.farrelfeno.substoryappintermediate.adapter.MainAdapter
import com.farrelfeno.substoryappintermediate.databinding.ActivityMainBinding
import com.farrelfeno.substoryappintermediate.factory.ViewModelFactory
import com.farrelfeno.substoryappintermediate.pref.UserPreference
import com.farrelfeno.substoryappintermediate.pref.dataStore
import com.farrelfeno.substoryappintermediate.response.ListStoryItem
import com.farrelfeno.substoryappintermediate.ui.model.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(
            applicationContext,
            UserPreference.getInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val btnMap = findViewById<FloatingActionButton>(R.id.btnMap)
        btnMap.setOnClickListener {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
        }

        checkSessionValid()
        setupAction()
        setUpStory()
       setUpUi()
    }

    private fun checkSessionValid() {
        viewModel.setToken().observe(this) {
            if (it == "null") {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                setupView("Bearer $it")
            }
        }
    }

    private fun setupView(token: String) {
        val storyAdapter = MainAdapter(object : MainAdapter.OnItemClickCallBack{
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_NAME, data.name)
                intent.putExtra(DetailStoryActivity.EXTRA_DESCRIPTION, data.description)
                intent.putExtra(DetailStoryActivity.EXTRA_IMAGE_URL, data.photoUrl)

                binding.rvStory.context.startActivity(
                    intent, ActivityOptionsCompat.makeSceneTransitionAnimation(binding.rvStory.context as Activity).toBundle()
                )
            }
        })

        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        viewModel.getStory(token).observe(this) { data ->
            binding.progressBar.visibility = View.INVISIBLE
            storyAdapter.submitData(lifecycle, data)
        }
    }
    private fun setUpStory(){
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setUpUi() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this@MainActivity).apply {
                setTitle("Thank You For Visiting Us")
                setMessage("Are You Sure Want To Exit?")
                setPositiveButton("Ya") { _, _ ->
                    viewModel.clearToken()

                    val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                setNegativeButton("Tidak") { _, _ ->
                }
                create()
                show()
            }
        }
    }
}