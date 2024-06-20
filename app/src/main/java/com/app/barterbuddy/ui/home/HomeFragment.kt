package com.app.barterbuddy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.barterbuddy.ViewModelsFactory
import com.app.barterbuddy.adapter.PostAdapter
import com.app.barterbuddy.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val postAdapter = PostAdapter()
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelsFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        with(binding){
            rv.layoutManager = LinearLayoutManager(requireActivity())
            rv.adapter = postAdapter
        }

        lifecycleScope.launch {
            homeViewModel.getPost().observe(viewLifecycleOwner){
                postAdapter.submitList(it)
            }
        }

        return binding.root
    }
}