package com.example.inventoryapp.view

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.inventoryapp.R
import com.example.inventoryapp.databinding.ActivitySigninBinding

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPasswordField()
        setupButtonState()   // Activa/desactiva el botón Login
    }

    // ACTIVAR O DESACTIVAR BOTÓN LOGIN
    private fun setupButtonState() {

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateButton()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etEmail.addTextChangedListener(watcher)
        binding.etPassword.addTextChangedListener(watcher)
    }

    private fun validateButton() {
        val emailFilled = binding.etEmail.text?.isNotEmpty() == true
        val passwordFilled = binding.etPassword.text?.isNotEmpty() == true

        val isEnabled = emailFilled && passwordFilled

        binding.btnLogin.isEnabled = isEnabled
        binding.btnLogin.alpha = if (isEnabled) 1f else 0.5f
    }

    // VALIDACIÓN Y VISIBILIDAD DE CONTRASEÑA
    private fun setupPasswordField() {
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.passwordInputLayout.setEndIconOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < 6 && password.isNotEmpty()) {
            binding.passwordInputLayout.error = getString(R.string.error_min_digits)
            binding.passwordInputLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
        } else {
            binding.passwordInputLayout.error = null
            binding.passwordInputLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.white)
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            binding.etPassword.inputType = InputType.TYPE_CLASS_NUMBER
            binding.passwordInputLayout.endIconDrawable =
                ContextCompat.getDrawable(this, R.drawable.ic_eye_closed)
        } else {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            binding.passwordInputLayout.endIconDrawable =
                ContextCompat.getDrawable(this, R.drawable.ic_eye_open)
        }

        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
    }
}
