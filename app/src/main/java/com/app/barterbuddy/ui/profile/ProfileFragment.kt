package com.app.barterbuddy.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.barterbuddy.R
import com.app.barterbuddy.ViewModelsFactory
import com.app.barterbuddy.adapter.SectionPagerAdapter
import com.app.barterbuddy.databinding.FragmentProfileBinding
import com.app.barterbuddy.ui.MainActivity
import com.app.barterbuddy.ui.login.LoginActivity
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
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

        binding.showProfile.setOnClickListener {
            (activity as MainActivity).loadFragments(EditProfileFragment())
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        lifecycleScope.launch {
            profileViewModel.getDetailUser(firebaseUser!!.uid).observe(viewLifecycleOwner){
                Glide.with(requireActivity()).load(it.profileImg).error(R.drawable.outline_person_24).into(binding.foto)
                binding.username.text = it.username
                binding.email.text = it.email
                binding.city.text = it.city
            }
        }

        val tabTitle = arrayOf(
            "Posts",
            "Interest",
            "Transactions"
        )

        val pagerAdapter = SectionPagerAdapter(requireActivity(), uid = firebaseUser!!.uid)
        binding.tabView.adapter = pagerAdapter
        TabLayoutMediator(binding.tabs, binding.tabView){ tab, pos ->
            tab.text = tabTitle[pos]
        }.attach()

        return binding.root
    }
}