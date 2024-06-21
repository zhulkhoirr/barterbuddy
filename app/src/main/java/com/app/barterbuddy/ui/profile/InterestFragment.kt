package com.app.barterbuddy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.barterbuddy.ViewModelsFactory
import com.app.barterbuddy.adapter.InterestAdapter
import com.app.barterbuddy.databinding.FragmentInterestBinding
import kotlinx.coroutines.launch

class InterestFragment : Fragment() {

    private lateinit var binding: FragmentInterestBinding
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelsFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInterestBinding.inflate(layoutInflater, container, false)

        val dataUid = arguments?.getString(UID)

        val interestAdapter = InterestAdapter()
        with(binding){
            rv.layoutManager = LinearLayoutManager(requireActivity())
            rv.adapter = interestAdapter
        }

        lifecycleScope.launch {
            profileViewModel.getDetailUser(dataUid!!).observe(viewLifecycleOwner){
                lifecycleScope.launch {
                    profileViewModel.getInterestPost(it.interestedPosts).observe(viewLifecycleOwner){
                        interestAdapter.submitList(it)
                    }
                }
            }
        }

        return binding.root
    }

    companion object {

        fun newInstance(uid: String): InterestFragment {
            val fragment = InterestFragment()
            val args = Bundle()
            args.putString(UID, uid)
            fragment.arguments = args
            return fragment
        }

        const val UID = "UID"
    }
}