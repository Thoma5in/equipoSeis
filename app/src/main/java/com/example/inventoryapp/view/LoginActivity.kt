package com.example.inventoryapp.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.inventoryapp.R
import com.example.inventoryapp.databinding.ActivitySigninBinding
import com.example.inventoryapp.viewmodel.LoginViewModel
import com.example.inventoryapp.viewmodel.LoginViewModel.AuthEvent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private val viewModel: LoginViewModel by viewModels()

    private val MIN_PASSWORD_LENGTH = 6
    private val MAX_PASSWORD_LENGTH = 10
    private var fromWidget: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fromWidget = intent.getBooleanExtra("from_widget", false)

        if (FirebaseAuth.getInstance().currentUser != null && !fromWidget) {
            goToMainActivity()
            return
        }

        binding.tvRegisterLink.setOnClickListener {
            if (binding.tvRegisterLink.isEnabled) {
                onRegisterClick()
            }
        }

        binding.btnLogin.setOnClickListener {
            onLoginClick()
        }

        setupFieldValidation()
        observeAuthEvents()
    }

    private fun setupFieldValidation() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePasswordCriteria()
                checkFieldsForEnablement()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etEmail.addTextChangedListener(watcher)
        binding.etPassword.addTextChangedListener(watcher)

        validatePasswordCriteria()
        checkFieldsForEnablement()
    }

    private fun validatePasswordCriteria(): Boolean {
        val password = binding.etPassword.text.toString()
        val length = password.length

        val isValid = length >= MIN_PASSWORD_LENGTH && length <= MAX_PASSWORD_LENGTH

        if (length > 0 && length < MIN_PASSWORD_LENGTH) {

            binding.passwordInputLayout.error = "Mínimo ${MIN_PASSWORD_LENGTH} dígitos"
        } else {
            binding.passwordInputLayout.error = null
        }

        return isValid
    }

    private fun checkFieldsForEnablement() {
        val emailFilled = binding.etEmail.text?.isNotBlank() == true
        val passwordFilled = binding.etPassword.text?.isNotBlank() == true

        val isPasswordValid = validatePasswordCriteria()

        val isEnabled = emailFilled && passwordFilled && isPasswordValid

        binding.btnLogin.isEnabled = isEnabled
        binding.btnLogin.alpha = if (isEnabled) 1.0f else 0.5f

        val loginTintColor = if (isEnabled) R.color.orange else R.color.gray
        binding.btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, loginTintColor)

        binding.tvRegisterLink.isEnabled = isEnabled

        val registerLinkColor = if (isEnabled) {
            ContextCompat.getColor(this, R.color.white)
        } else {
            ContextCompat.getColor(this, R.color.gray_register)
        }
        binding.tvRegisterLink.setTextColor(registerLinkColor)
        binding.tvRegisterLink.alpha = if (isEnabled) 1.0f else 0.5f
    }

    private fun onLoginClick() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        viewModel.login(email, password)
    }

    private fun onRegisterClick() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        viewModel.register(email, password)
    }

    private fun observeAuthEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.authEvents.collect { event ->
                when (event) {
                    is AuthEvent.LoginSuccess -> handleSuccess()

                    is AuthEvent.LoginError -> Toast.makeText(this@LoginActivity, event.message, Toast.LENGTH_SHORT).show()

                    is AuthEvent.SignUpSuccess -> {
                        Toast.makeText(this@LoginActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        handleSuccess()
                    }

                    is AuthEvent.SignUpError -> Toast.makeText(
                        this@LoginActivity,
                        event.message,
                        Toast.LENGTH_LONG
                    ).show()

                    null -> { /* Ignorar el valor inicial o estados de carga */ }
                }
            }
        }
    }

    private fun handleSuccess() {
        if (fromWidget) {
            val intent = Intent("com.example.inventoryapp.ACTION_UPDATE_WIDGET").apply {
                setPackage("com.example.inventoryapp")
            }
            sendBroadcast(intent)
            finish()
        } else {
            goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
