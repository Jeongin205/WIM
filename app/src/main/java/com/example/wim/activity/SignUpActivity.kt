package com.example.wim.activity

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.example.wim.R
import com.example.wim.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    var isEmail: Boolean = false
    var isPassword: Boolean = false
    val TAG = "SignUpActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        val sharedPreference = getSharedPreferences("Login", MODE_PRIVATE)
        val editor = sharedPreference.edit()

        binding.backButton.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(p0.toString())
                        .matches() || p0!!.isBlank()
                ) {
                    binding.layoutEmail.isErrorEnabled = false
                    binding.layoutEmail.error = null
                    if (!p0!!.isBlank()) isEmail = true
                } else {
                    binding.layoutEmail.isErrorEnabled = true
                    binding.layoutEmail.error = "이메일 형식에 맞춰주세요"
                    isEmail = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (Pattern.matches("^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#$%^&*])(?=.*[0-9!@#$%^&*]).{8,15}$", p0) || p0!!.isBlank()) {
                    binding.layoutPassword.isErrorEnabled = false
                    binding.layoutPassword.error = null
                    if (!p0!!.isBlank()) {
                        binding.passwordCheckEditText.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                if (p0.toString() == binding.passwordEditText.text.toString() || p0!!.isBlank()) {
                                    if (!p0!!.isBlank()) isPassword = true
                                    binding.layoutPasswordCheck.isErrorEnabled = false
                                    binding.layoutPasswordCheck.error = null
                                } else {
                                    isPassword = false
                                    binding.layoutPasswordCheck.isErrorEnabled = true
                                    binding.layoutPasswordCheck.error = "비밀번호가 일치하지 않습니다."
                                }
                            }
                            override fun afterTextChanged(p0: Editable?) {
                            }

                        })
                    }
                } else {
                    binding.layoutPassword.isErrorEnabled = true
                    binding.layoutPassword.error = "8자이상, 숫자, 문자, 특수문자중 2가지를 포함하세요"
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        binding.signUpButton.setOnClickListener {
            if (isEmail && isPassword) {
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordCheckEditText.text.toString()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            editor.putString("email", email)
                            editor.putString("password", password)
                            editor.commit()
                            Log.d(TAG, "signInWithCustomToken:success")
                            intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.w(TAG, "signInWithCustomToken:failure", task.exception)
                            Toast.makeText(
                                this, "이미 존재하는 유저이거나\n회원가입 오류입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "입력하신 정보를 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }
}