package com.dicoding.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.api.ApiConfig
import com.dicoding.storyapp.data.Repository
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.ui.viewmodel.UserViewModel
import com.dicoding.storyapp.ui.viewmodel.ViewModelFactory
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEmailValidation()
        setupPasswordValidation()
        playAnimation()

        val apiService = ApiConfig.getApiService()
        val repository = Repository(apiService)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                AlertDialog.Builder(this).apply {
                    setTitle("Registration Failed")
                    setMessage("Please fill in all fields")
                    setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            } else {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.register(name, email, password)
            }
        }

        viewModel.registerResponse.observe(this) { response ->
            binding.progressBar.visibility = View.GONE
            if (response.error == false) {
                val email = binding.emailEditText.text.toString()
                AlertDialog.Builder(this).apply {
                    setTitle("Yeah!")
                    setMessage("The account with $email is ready. Come on, log in and share your story")
                    setPositiveButton("Log In") { _, _ ->
                        finish()
                    }
                    create()
                    show()
                }
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle("Registration Failed")
                    setMessage(response.message ?: "Unknown error occurred")
                    setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }
        }
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

        val register = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameInput = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordInput = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)

        val togetherName = AnimatorSet().apply {
            playTogether(name, nameInput)
        }

        val togetherEmail = AnimatorSet().apply {
            playTogether(email, emailInput)
        }

        val togetherPassword = AnimatorSet().apply {
            playTogether(password, passwordInput)
        }

        AnimatorSet().apply {
            playSequentially(title, togetherName, togetherEmail, togetherPassword, register)
            start()
        }
    }
}
