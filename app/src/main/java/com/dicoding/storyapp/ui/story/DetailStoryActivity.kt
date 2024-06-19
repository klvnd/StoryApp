package com.dicoding.storyapp.ui.story

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_action_bar)

        val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.action_bar_title)
        titleTextView?.text = "Detail Story"

        val story = intent.getParcelableExtra<ListStoryItem>(STORY_ITEM)
        story?.let { displayStoryDetails(it) }
    }

    private fun displayStoryDetails(story: ListStoryItem) {
        binding.apply {
            title.text = story.name
            description.text = story.description
            Glide.with(this@DetailStoryActivity)
                .load(story.photoUrl)
                .into(ivDetailStoryImage)
        }
    }

    companion object {
        const val STORY_ITEM = "story_item"
    }
}