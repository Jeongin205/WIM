package com.example.wim.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.wim.R
import com.example.wim.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val TAG: String = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (email.isBlank() && password.isBlank()) Toast.makeText(
                this,
                "빈칸을 채워주세요",
                Toast.LENGTH_SHORT
            )
                .show()
            else if (email.isBlank()) Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT)
                .show()
            else if (password.isBlank()) Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT)
                .show()
            else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            moveMainPage(auth.currentUser)
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "사용자 정보를 확인해 주세요", Toast.LENGTH_SHORT).show()

                        }
                    }
            }

        }
    }

    public override fun onStart() {
        super.onStart()
        moveMainPage(auth.currentUser)
    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}