package com.app.barterbuddy.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.barterbuddy.R
import com.app.barterbuddy.databinding.FragmentTransactionBinding

class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTransactionBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {

        fun newInstance(uid: String): TransactionFragment {
            val fragment = TransactionFragment()
            val args = Bundle()
            args.putString(UID, uid)
            fragment.arguments = args
            return fragment
        }

        const val UID = "UID"
    }

}