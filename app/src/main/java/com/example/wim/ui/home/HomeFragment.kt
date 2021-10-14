package com.example.wim.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wim.MainAdapter
import com.example.wim.SpendingData
import com.example.wim.activity.MainActivity
import com.example.wim.activity.WriteActivity
import com.example.wim.databinding.FragmentHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    val uid = FirebaseAuth.getInstance().uid!!

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreference =
            this.activity?.getSharedPreferences("time", AppCompatActivity.MODE_PRIVATE)
        val month = sharedPreference?.getString("month", "")!!
        val year = sharedPreference.getString("year", "")!!

        val db = Firebase.database.reference.child(uid).child(year).child(month)

        binding.monthTextView.text = "$month 월 소비현황"

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(activity, WriteActivity::class.java))
        }


        db.child(uid).child(year).child(month).child("total")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.getValue(Int::class.java) != null) {
                        val t = makeCommaNumber(snapshot.getValue(Int::class.java)!!)
                        binding.totalTextView.text = "$t 원"
                    } else {
                        binding.totalTextView.text = "0원"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        val list: ArrayList<SpendingData> = ArrayList()
        binding.mainRecyclerView.adapter = MainAdapter(list)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue(String::class.java) != null) {
                        for (fileSnapshot1 in snapshot.children) {
                            db.child(fileSnapshot1.toString()).addValueEventListener(object  :ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for(fileSnapshot2 in snapshot.children){
                                        val detail = fileSnapshot2.child("detail").getValue(String::class.java)!!
                                        val money = fileSnapshot2.child("money").getValue(String::class.java)!!
                                        list.add(SpendingData("1", detail, money))
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                }
                            })

                        }
                        binding.mainRecyclerView.adapter?.notifyItemRemoved(list.size - 1)
                    }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun makeCommaNumber(input: Int): String {
        val formatter = DecimalFormat("###,###")
        return formatter.format(input)
    }
}