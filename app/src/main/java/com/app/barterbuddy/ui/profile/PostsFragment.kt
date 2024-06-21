package com.app.barterbuddy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.barterbuddy.ViewModelsFactory
import com.app.barterbuddy.adapter.ProfilePostAdapter
import com.app.barterbuddy.databinding.FragmentPostsBinding
import kotlinx.coroutines.launch


class PostsFragment : Fragment() {

    private lateinit var binding: FragmentPostsBinding
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelsFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostsBinding.inflate(inflater, container, false)

        val dataUid = arguments?.getString(UID)
        val profilePostAdapter = ProfilePostAdapter(profileViewModel, viewLifecycleOwner.lifecycleScope)

        with(binding){
            rv.layoutManager = GridLayoutManager(requireActivity(), 2)
            rv.adapter = profilePostAdapter
        }

        lifecycleScope.launch {
            profileViewModel.getPostByUser(dataUid!!).observe(viewLifecycleOwner){
                profilePostAdapter.submitList(it)
            }
        }

        return binding.root
    }

    companion object {

        fun newInstance(uid: String): PostsFragment {
            val fragment = PostsFragment()
            val args = Bundle()
            args.putString(UID, uid)
            fragment.arguments = args
            return fragment
        }

        private const val UID = "UID"
    }
}