package com.app.barterbuddy.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.barterbuddy.ViewModelsFactory
import com.app.barterbuddy.adapter.PostAdapter
import com.app.barterbuddy.databinding.FragmentHomeBinding
import com.app.barterbuddy.di.models.Users
import com.app.barterbuddy.ui.NotificationActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var users: Users
    private lateinit var auth: FirebaseUser
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelsFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = Firebase.auth.currentUser!!

        binding.notif.setOnClickListener {
            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        }

        lifecycleScope.launch {
            homeViewModel.getDetailUser(auth.uid).observe(viewLifecycleOwner){
                users = it
                setupAdapter()
            }
        }

        return binding.root
    }

    private fun setupAdapter() {
        val postAdapter = PostAdapter(homeViewModel, lifecycleScope, viewLifecycleOwner, users)

        with(binding){
            rv.layoutManager = LinearLayoutManager(requireActivity())
            rv.adapter = postAdapter
        }

        lifecycleScope.launch {
            homeViewModel.getPost().observe(viewLifecycleOwner){
                postAdapter.submitList(it)
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                lifecycleScope.launch {
                    if (query!!.isNotEmpty()){
                        homeViewModel.getSearchPost(auth.uid, query).observe(viewLifecycleOwner){
                            postAdapter.submitList(it)
                        }
                    } else {
                        homeViewModel.getPost().observe(viewLifecycleOwner){
                            postAdapter.submitList(it)
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}