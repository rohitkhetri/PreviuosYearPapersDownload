package com.example.previuosyearpapers.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.previuosyearpapers.adapter.PapersAdapter
import com.example.previuosyearpapers.Papers
import com.example.previuosyearpapers.databinding.FragmentHomeBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue

class Home_Fragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var recyclerview: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()

        setUpRecyclerView()
        fetchPapers()

        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.papersRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun fetchPapers() {
        database.reference.child("papers").get().addOnSuccessListener { result ->
            val papers = result.children.map { it.getValue<Papers>()!! }
            binding.papersRecyclerView.adapter = PapersAdapter(ArrayList(papers))
        }
    }
}
