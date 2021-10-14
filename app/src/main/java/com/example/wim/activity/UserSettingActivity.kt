package com.example.wim.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.wim.R
import com.example.wim.databinding.ActivityUserSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker

import android.view.ViewGroup
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.GetTokenResult


class UserSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_setting)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_setting)

        val user = FirebaseAuth.getInstance().currentUser!!
        val reference = FirebaseDatabase.getInstance().getReference()
        val uid = user.uid

        val builder1 = AlertDialog.Builder(this)
        val builder2 = AlertDialog.Builder(this)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.logoutButton.setOnClickListener {
            builder1.setTitle("로그아웃").setMessage("로그아웃을 하시겠습니까?").setPositiveButton("예"){dialog, which ->
                Firebase.auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }.setNegativeButton("아니오", null).show()
        }

        binding.userDelButton.setOnClickListener {
            builder1.setTitle("회원탈퇴").setMessage("회원탈퇴를 하시겠습니까?\n유저의 정보가 모두 사라집니다").setPositiveButton("예"){dialog, which->
                builder2.setTitle("회원탈퇴").setMessage("정말로 탈퇴하시겠습니까?").setPositiveButton("예"){dialog, which->
                    user.delete().addOnCompleteListener{task->
                        if(task.isSuccessful){
                            Firebase.auth.signOut()
                            reference.setValue(null)
                            Toast.makeText(this, "이용해주셔서 감사합니다", Toast.LENGTH_SHORT).show()
                            finishAffinity()
                        }
                        else
                            Toast.makeText(this, "불러오기 실패", Toast.LENGTH_SHORT).show()
                    }
                }.setNegativeButton("아니오", null).show()
            }.setNegativeButton("아니오", null).show()
        }


    }
}