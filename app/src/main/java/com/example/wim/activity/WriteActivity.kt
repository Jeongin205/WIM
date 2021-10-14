package com.example.wim.activity

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.wim.R
import com.example.wim.databinding.ActivityWriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class WriteActivity : AppCompatActivity() {
    val db = Firebase.database.reference
    private lateinit var binding: ActivityWriteBinding
    val uid = FirebaseAuth.getInstance().uid
    private lateinit var year :String
    private lateinit var month : String
    private lateinit var day : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write)

        val sharedPreference = getSharedPreferences("time", MODE_PRIVATE)
        val editor = sharedPreference.edit()
        year = sharedPreference.getString("year", "")!!
        month = sharedPreference.getString("month", "")!!
        day = sharedPreference.getString("day", "")!!
        val num = sharedPreference.getInt("num", 1)



        binding.backButton.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            if (binding.moneyEditText.text.toString()
                    .isBlank() && binding.detailEditText.text.toString().isBlank()
            ) {
                Toast.makeText(this, "정보를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (binding.moneyEditText.text.toString().isBlank()) {
                Toast.makeText(this, "금액을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (binding.detailEditText.text.toString().isBlank()) {
                Toast.makeText(this, "지출내역을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                val money = Integer.parseInt(binding.moneyEditText.text.toString())
                var total = 0
                db.child(uid!!).child(year).child(month).child("total").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.getValue(Int::class.java)!=null){
                            val t = snapshot.getValue(Int::class.java)!!
                            if(t==0)total=money
                            else total = t.plus(money)
                            val data = hashMapOf(
                                "money" to makeCommaNumber(money),
                                "detail" to binding.detailEditText.text.toString()
                            )
                            val totalData = hashMapOf(
                                "total" to total
                            )

                            db.child(uid).child(year).child(month).child(day).child(num.toString())
                                .setValue(data)
                            db.child(uid).child(year).child(month).updateChildren(totalData as Map<String, Any>)
                        }
                        else{
                            val data = hashMapOf(
                                "money" to makeCommaNumber(money),
                                "detail" to binding.detailEditText.text.toString()
                            )
                            val totalData = hashMapOf(
                                "total" to money
                            )

                            db.child(uid).child(year).child(month).child(day).child(num.toString())
                                .setValue(data)
                            db.child(uid).child(year).child(month).updateChildren(totalData as Map<String, String>)

                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


                editor.putInt("num", num + 1)
                editor.commit()
                finish()
            }
        }
    }

    fun makeCommaNumber(input: Int): String {
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }
}