package com.dicoding.storyapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.ui.story.AddStoryActivity
import com.dicoding.storyapp.ui.story.DetailStoryActivity
import com.dicoding.storyapp.ui.story.Injection
import com.dicoding.storyapp.ui.story.StoryAdapter
import com.dicoding.storyapp.ui.viewmodel.UserViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import com.dicoding.storyapp.ui.welcome.WelcomeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var storyAdapter: StoryAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_action_bar)

        val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.action_bar_title)
        titleTextView?.text = "Story App"

        dataStoreManager = DataStoreManager(this)

        storyAdapter = StoryAdapter(emptyList(), object : StoryAdapter.OnItemClickListener {
            override fun onItemClick(story: ListStoryItem) {
                showDetailActivity(story)
            }
        })
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
        }

        val viewModel: UserViewModel by viewModels {
            ViewModelFactory(runBlocking { Injection.provideRepository(this@MainActivity) })
        }

        viewModel.getStories().observe(this) { storyResponse ->
            storyResponse?.listStory?.let { stories ->
                storyAdapter = StoryAdapter(stories, object : StoryAdapter.OnItemClickListener {
                    override fun onItemClick(story: ListStoryItem) {
                        showDetailActivity(story)
                    }
                })
                binding.rvStory.adapter = storyAdapter
            }
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }



    private fun showDetailActivity(story: ListStoryItem) {
        val intent = Intent(this, DetailStoryActivity::class.java)
        intent.putExtra(DetailStoryActivity.STORY_ITEM, story)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Logout")
                builder.setMessage("Are you sure you want to logout?")
                builder.setPositiveButton("Yes") { dialog, _ ->
                    lifecycleScope.launch {
                        dataStoreManager.clearToken()
                        startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                        Toast.makeText(this@MainActivity, "You have logged out", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    dialog.dismiss()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}