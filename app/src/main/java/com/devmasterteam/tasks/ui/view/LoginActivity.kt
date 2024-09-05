package com.devmasterteam.tasks.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.databinding.ActivityLoginBinding
import com.devmasterteam.tasks.service.listener.BIOListener
import com.devmasterteam.tasks.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        supportActionBar?.hide()

        binding.buttonLogin.setOnClickListener(this)
        binding.textRegister.setOnClickListener(this)

        viewModel.verifyLoggedUser()
        viewModel.verifyAccessUser()

        observe()
    }

    override fun onClick(v: View) {
        when {
            (v.id == R.id.button_login) -> handleLogin()
            (v.id == R.id.text_register) -> handleRegister()
        }
    }

    private fun observe() {
        viewModel.login.observe(this) {
            if (it.status()) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, it.message(), Toast.LENGTH_LONG).show()
            }
        }

        viewModel.accessUser.observe(this) {
            biometricAuth(object : BIOListener {
                override fun onSuccessAuth() {
                    viewModel.doLogin(it.email, it.password)
                }
            })
        }

        viewModel.bioAuth.observe(this) {
            biometricAuth(object : BIOListener {
                override fun onSuccessAuth() {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            })
        }
    }

    private fun biometricAuth(listener: BIOListener) {
        val executor = ContextCompat.getMainExecutor(this)

        val bio =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    listener.onSuccessAuth()
                }
            })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.title_bio))
            .setSubtitle(getString(R.string.sub_title_bio))
            .setDescription(getString(R.string.description_bio))
            .setNegativeButtonText(getString(R.string.negative_button_bio)).build()

        bio.authenticate(info)
    }

    private fun handleLogin() {
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()

        viewModel.doLogin(email, password)
    }

    private fun handleRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}