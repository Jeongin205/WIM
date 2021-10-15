package com.example.wim.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wim.adapter.MainAdapter
import com.example.wim.data.SpendingData
import com.example.wim.activity.WriteActivity
import com.example.wim.databinding.FragmentHomeBinding
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

    var list: ArrayList<SpendingData> = ArrayList()
    val uid = FirebaseAuth.getInstance().uid!!
    private lateinit var total: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
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
        val year = sharedPreference?.getString("year", "")!!
        val month = sharedPreference.getString("month", "")!!

        val db = Firebase.database.reference.child(uid).child(year).child(month)
        val dbTotal = Firebase.database.reference.child(uid).child(year)

        val everMonth = Integer.parseInt(month) - 1


        binding.monthTextView.text = "$month 월 소비현황"

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(activity, WriteActivity::class.java))
        }


        db.child("total")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.getValue(Int::class.java) != null) {
                        val t = makeCommaNumber(snapshot.getValue(Int::class.java)!!)
                        total = snapshot.getValue(Int::class.java).toString()
                        binding.totalTextView.text = "$t 원"
                    } else {
                        binding.totalTextView.text = "0원"
                    }
                    dbTotal.child(everMonth.toString()).child("total")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.getValue(Int::class.java) != null) {
                                    if (snapshot.getValue(Int::class.java)!! < Integer.parseInt(
                                            total
                                        )
                                    ) {
                                        val ever = makeCommaNumber(
                                            Integer.parseInt(total) - snapshot.getValue(Int::class.java)!!
                                        )
                                        binding.everTotalTextView.text = "저번달보다 $ever" + "원 더 썼어요"
                                    } else if (snapshot.getValue(Int::class.java)!! > Integer.parseInt(
                                            total
                                        )
                                    ) {
                                        val ever = makeCommaNumber(
                                            snapshot.getValue(Int::class.java)!! - Integer.parseInt(
                                                total
                                            )
                                        )
                                        binding.everTotalTextView.text = "저번달보다 $ever" + "원 덜 썼어요"
                                    } else if (snapshot.getValue(Int::class.java)!! == Integer.parseInt(
                                            total
                                        )
                                    ) {
                                        binding.everTotalTextView.text = "저번달과 지출이 똑같아요"
                                    }
                                } else {
                                    binding.everTotalTextView.text = "저번달 기록이 없어요"
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                            }


                        })
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })




        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (fileSnapshot1 in snapshot.children) {
                    db.child(fileSnapshot1.key.toString())
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (fileSnapshot2 in snapshot.children) {
                                    val detail =
                                        fileSnapshot2.child("detail").getValue(String::class.java)!!
                                    val money =
                                        fileSnapshot2.child("money")
                                            .getValue(String::class.java)!! + "원"
                                    val date = month + "." + "${fileSnapshot1.key}"
                                    if (!list.contains(SpendingData(date, detail, money))) {
                                        list.add(SpendingData(date, detail, money))
                                    }
                                    binding.mainRecyclerView.adapter = MainAdapter(list)
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })

                }
                binding.mainRecyclerView.adapter?.notifyItemRemoved(list.size - 1)


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