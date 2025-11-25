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
    }

    private fun setupPasswordField() {
        // Validación en tiempo real (Criterio 5)
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Toggle visibilidad de contraseña (Criterio 6)
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
            // Mostrar contraseña
            binding.etPassword.inputType = InputType.TYPE_CLASS_NUMBER
            binding.passwordInputLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_eye_closed)
        } else {
            // Ocultar contraseña
            binding.etPassword.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            binding.passwordInputLayout.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_eye_open)
        }

        // Mover cursor al final
        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
    }
}