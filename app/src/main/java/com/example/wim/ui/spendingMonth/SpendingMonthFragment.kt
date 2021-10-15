package com.example.wim.ui.spendingMonth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wim.adapter.MonthAdapter
import com.example.wim.data.SpendingMonthData
import com.example.wim.databinding.FragmentSpendingMonthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class SpendingMonthFragment : Fragment() {

    private lateinit var galleryViewModel: SpendingMonthViewModel
    private var _binding: FragmentSpendingMonthBinding? = null
    val uid = FirebaseAuth.getInstance().uid!!

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        galleryViewModel =
            ViewModelProvider(this).get(SpendingMonthViewModel::class.java)

        galleryViewModel.text

        _binding = FragmentSpendingMonthBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreference =
            this.activity?.getSharedPreferences("time", AppCompatActivity.MODE_PRIVATE)
        val year = sharedPreference?.getString("year", "")!!

        val db = Firebase.database.reference.child(uid).child(year)
        val list: ArrayList<SpendingMonthData> = ArrayList()
        binding.monthSpendingRecyclerView.adapter = MonthAdapter(list)

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (fileSnapshot in snapshot.children) {
                    val total =
                        makeCommaNumber(fileSnapshot.child("total").getValue(Int::class.java)!!)
                    list.add(SpendingMonthData("${fileSnapshot.key.toString()} 월", "$total 원"))
                }
                binding.monthSpendingRecyclerView.adapter?.notifyItemRemoved(list.size)
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