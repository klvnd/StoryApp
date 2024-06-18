package com.dicoding.storyapp.ui.story

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.ui.viewmodel.UserViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.runBlocking

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var userViewModel: UserViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_action_bar)

        val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.action_bar_title)
        titleTextView?.text = "Detail Story"

        val repository = runBlocking { Injection.provideRepository(this@DetailStoryActivity) }
        userViewModel = ViewModelProvider(this, ViewModelFactory(repository))[UserViewModel::class.java]

        val storyId = intent.getStringExtra(STORY_ID)
        userViewModel.getStoryDetail(storyId.toString()).observe(this) { storyDetail ->
            binding.apply {
                title.text = storyDetail.name
                description.text = storyDetail.description
                Glide.with(this@DetailStoryActivity)
                    .load(storyDetail.photoUrl)
                    .into(ivDetailStoryImage)
            }
        }
    }

    companion object {
        const val STORY_ID = "story_id"
    }
}