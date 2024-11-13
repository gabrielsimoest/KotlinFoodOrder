package com.example.kotlinfoodorder.authManager.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlinfoodorder.databinding.ActivityLoginBinding
import com.example.kotlinfoodorder.authManager.ui.forgotpassword.ForgotPasswordActivity
import com.example.kotlinfoodorder.menuManager.ui.menu.MenuActivity
import com.example.kotlinfoodorder.authManager.ui.register.RegisterActivity
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFormObserver()
        initButtonListeners()
    }

    private fun initFormObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentUser.collect { user ->
                    user?.let {
                        binding.emailEditText.error = if (!it.email.isNullOrEmpty()) null else "Por favor, insira um email válido."
                        binding.passwordEditText.error = if (!it.password.isNullOrEmpty()) null else "Por favor, insira uma senha válida."

                        binding.emailEditText.setText(it.email)
                        binding.passwordEditText.setText(it.password)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is LoginViewModel.LoginState.Loading -> {
                        }

                        is LoginViewModel.LoginState.Success -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login bem-sucedido",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@LoginActivity, MenuActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        is LoginViewModel.LoginState.Error -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "Erro: ${state.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this@LoginActivity,
                                "Usuário não encontrado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun initButtonListeners() {
        initLoginButtonListener()
        initRegistryButtonListener()
        initForgotPasswordButtonListener()
    }

    private fun initLoginButtonListener() {
        binding.login.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                if (email.isEmpty()) {
                    binding.emailEditText.error = "Por favor, insira um email."
                }
                if (password.isEmpty()) {
                    binding.passwordEditText.error = "Por favor, insira uma senha."
                }
            }
        }
    }

    private fun initRegistryButtonListener() {
        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initForgotPasswordButtonListener() {
        binding.forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}