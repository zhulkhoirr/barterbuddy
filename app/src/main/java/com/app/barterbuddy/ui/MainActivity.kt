package com.app.barterbuddy.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.barterbuddy.R
import com.app.barterbuddy.databinding.ActivityMainBinding
import com.app.barterbuddy.ui.addPost.AddFragment
import com.app.barterbuddy.ui.chat.ChatFragment
import com.app.barterbuddy.ui.home.HomeFragment
import com.app.barterbuddy.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.botNavbar.itemIconSize = 90
        binding.botNavbar.itemIconTintList = null
        binding.botNavbar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.explore -> {
                    loadFragments(HomeFragment())
                    true
                }
                R.id.add -> {
                    loadFragments(AddFragment())
                    true
                }
                R.id.chat -> {
                    loadFragments(ChatFragment())
                    true
                }
                R.id.profile -> {
                    loadFragments(ProfileFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
        binding.botNavbar.selectedItemId = R.id.explore
    }

    fun loadFragments(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}