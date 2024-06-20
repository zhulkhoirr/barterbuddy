package com.app.barterbuddy.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.app.barterbuddy.R
import com.app.barterbuddy.ViewModelsFactory
import com.app.barterbuddy.databinding.ActivitySignUpBinding
import com.app.barterbuddy.di.models.RegisterRequest
import com.app.barterbuddy.ui.login.LoginActivity
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val registerViewModel by viewModels<SignUpViewModel> {
        ViewModelsFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.register.setOnClickListener {
            val dataUsername = binding.username.text.toString()
            val dataEmail = binding.email.text.toString()
            val dataPassword = binding.password.text.toString()
            val dataCPassword = binding.cPassword.text.toString()
            val dataCity = binding.city.text.toString()

            when {
                dataUsername.isEmpty() -> messageToast("Username masih kosong")
                dataEmail.isEmpty() -> messageToast("Email masih kosong")
                dataPassword.isEmpty() -> messageToast("Passwrd masih kosong")
                dataCPassword.isEmpty() -> messageToast("Konfirmasi Password masih kosng")
                dataCity.isEmpty() -> messageToast("Kota Masih kosong")
                else -> {
                    val register = RegisterRequest(
                        username = dataUsername,
                        email = dataEmail,
                        password = dataPassword,
                        confirmPassword = dataCPassword,
                        city = dataCity
                    )
                    lifecycleScope.launch {
                        try {
                            val response = registerViewModel.postRegister(register)
                            response.observe(this@SignUpActivity){
                                if (it.success){
                                    messageToast("Register Berhasil")
                                    startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                                } else {
                                    messageToast("Register Gagal: ${it.error}")
                                }
                            }
                        } catch (e: Exception){
                            messageToast("Register Gagal: ${e.message.toString()}")
                        }
                    }
                }
            }
        }
    }

    private fun messageToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}