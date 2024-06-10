package com.dicoding.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreManager = DataStoreManager(this)

        setupAction()
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                dataStoreManager.clearToken()
                startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                finish()
            }
        }
    }
}
