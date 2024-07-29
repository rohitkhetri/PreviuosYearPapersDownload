package com.example.previuosyearpapers.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.previuosyearpapers.adapter.PapersAdapter
import com.example.previuosyearpapers.dataClass.Papers
import com.example.previuosyearpapers.R
import com.example.previuosyearpapers.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue

class Home_Fragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseauth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()
        firebaseauth = FirebaseAuth.getInstance()

        if (firebaseauth.currentUser == null) {
            redirectToLogin()
        } else {
            setUpRecyclerView()
            fetchPapers()
        }
        return binding.root
    }

    private fun redirectToLogin() {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }



    private fun setUpRecyclerView() {
        binding.papersRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun fetchPapers() {
        database.reference.child("papers").get().addOnSuccessListener { result ->
            val papers = result.children.map { it.getValue<Papers>()!! }
            binding.papersRecyclerView.adapter = PapersAdapter(ArrayList(papers), firebaseauth)
        }
    }
}
