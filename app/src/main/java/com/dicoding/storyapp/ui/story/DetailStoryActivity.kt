package com.dicoding.storyapp.ui.story

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.ui.viewmodel.UserViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.runBlocking

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel: UserViewModel by viewModels {
        ViewModelFactory(runBlocking { Injection.provideRepository(this@DetailStoryActivity) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("STORY_ID")

        viewModel.getStoryDetail(storyId.toString()).observe(this) { storyDetail ->
            binding.title.text = storyDetail.name
            binding.description.text = storyDetail.description
        }
    }
}
