package com.dicoding.storyapp.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEmailValidation()
        setupPasswordValidation()
        playAnimation()
        setupAction()
    }

    private fun validateEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun setupEmailValidation() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    val isValid = validateEmail(it.toString())
                    if (!isValid) {
                        binding.emailEditText.error = "Invalid email format"
                        binding.emailEditTextLayout.endIconMode = TextInputLayout.END_ICON_NONE
                    } else {
                        binding.emailEditText.error = null
                        binding.emailEditTextLayout.endIconMode = TextInputLayout.END_ICON_NONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupPasswordValidation() {
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.length < 8) {
                        binding.passwordEditText.error = "Password must not be less than 8 characters"
                        binding.passwordEditTextLayout.endIconMode = TextInputLayout.END_ICON_NONE
                    } else {
                        binding.passwordEditText.error = null
                        binding.passwordEditTextLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordInput = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)

        val togetherEmail = AnimatorSet().apply {
            playTogether(email, emailInput)
        }

        val togetherPassword = AnimatorSet().apply {
            playTogether(password, passwordInput)
        }

        AnimatorSet().apply {
            playSequentially(title, togetherEmail, togetherPassword, login)
            start()
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this, "Fungsi Login belum dibuat", Toast.LENGTH_SHORT).show()
        }
    }
}