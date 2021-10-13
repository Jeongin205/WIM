package com.example.wim.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.wim.R
import com.example.wim.databinding.ActivityWriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class WriteActivity : AppCompatActivity() {
    val db = Firebase.database
    private lateinit var binding: ActivityWriteBinding
    private lateinit var mmoney : Editable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write)

        val sharedPreference = getSharedPreferences("time", MODE_PRIVATE)
        val editor = sharedPreference.edit()
        val year = sharedPreference.getString("year", "")
        val month = sharedPreference.getString("month", "")
        val day = sharedPreference.getString("day", "")
        val num = sharedPreference.getInt("num", 1)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            if(binding.moneyEditText.text.toString().isBlank()&&binding.detailEditText.text.toString().isBlank()){
                Toast.makeText(this, "정보를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(binding.moneyEditText.text.toString().isBlank()){
                Toast.makeText(this, "금액을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if(binding.detailEditText.text.toString().isBlank()){
                Toast.makeText(this, "지출내역을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                val money = makeCommaNumber(Integer.parseInt(binding.moneyEditText.text.toString()))
                val uid = FirebaseAuth.getInstance().uid
                val data = hashMapOf(
                    "money" to money,
                    "detail" to binding.detailEditText.text.toString()
                )
                db.reference.child(uid!!).child(year!!).child(month!!).child(day!!).child(num.toString()).setValue(data)
                editor.putInt("num", num+1)
                editor.commit()
                finish()
            }
        }
    }
    fun makeCommaNumber(input: Int): String{
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }
}