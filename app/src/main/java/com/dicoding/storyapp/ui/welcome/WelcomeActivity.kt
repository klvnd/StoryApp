package com.dicoding.storyapp.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.databinding.ActivityWelcomeBinding
import com.dicoding.storyapp.ui.auth.login.LoginActivity
import com.dicoding.storyapp.ui.main.MainActivity
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreManager = DataStoreManager(this)

        lifecycleScope.launch {
            dataStoreManager.tokenFlow.collect { token ->
                if (token.isNullOrEmpty()) {
                    // No token, navigate to LoginActivity
                    startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
                } else {
                    // Token exists, navigate to MainActivity
                    startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                }
                finish()
            }
        }
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, register)
        }
        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }
}