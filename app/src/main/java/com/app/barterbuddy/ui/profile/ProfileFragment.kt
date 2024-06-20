package com.app.barterbuddy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.barterbuddy.ViewModelsFactory
import com.app.barterbuddy.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelsFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        lifecycleScope.launch {
            profileViewModel.getDetailUser(firebaseUser!!.uid).observe(viewLifecycleOwner){
                Glide.with(requireActivity()).load(it.profileImg).into(binding.foto)
                binding.username.text = it.username
                binding.email.text = it.email
            }
        }

        val tabTitle = arrayOf(
            "Posts",
            "Interest",
            "Transactions"
        )

        return binding.root
    }
}