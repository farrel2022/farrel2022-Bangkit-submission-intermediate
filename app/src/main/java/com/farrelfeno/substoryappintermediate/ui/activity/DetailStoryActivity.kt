package com.farrelfeno.substoryappintermediate.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.farrelfeno.substoryappintermediate.R
import com.farrelfeno.substoryappintermediate.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val name = intent.getStringExtra(EXTRA_NAME)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val imgUrl = intent.getStringExtra(EXTRA_IMAGE_URL)
        with(binding) {
            tvUsername.text = name
            tvStory.text = description
            Glide.with(this@DetailStoryActivity)
                .load(imgUrl)
                .into(imageView2)
        }
    }
    companion object {
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        const val EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL"
    }
}