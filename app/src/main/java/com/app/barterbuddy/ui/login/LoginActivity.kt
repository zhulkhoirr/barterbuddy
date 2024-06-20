package com.app.barterbuddy.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.barterbuddy.R
import com.app.barterbuddy.databinding.ActivityLoginBinding
import com.app.barterbuddy.ui.MainActivity
import com.app.barterbuddy.ui.register.SignUpActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        val current = auth.currentUser
        if (current != null){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        binding.btnRegister.setOnClickListener { startActivity(Intent(this@LoginActivity, SignUpActivity::class.java)) }

        binding.btnLogin.setOnClickListener {
            val dataEmail = binding.email.text.toString()
            val dataPassword = binding.password.text.toString()

            auth.signInWithEmailAndPassword(dataEmail, dataPassword)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        messageToast("Login Berhasil")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        messageToast("Login Gagal")
                    }
                }
        }

    }

    private fun messageToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
}