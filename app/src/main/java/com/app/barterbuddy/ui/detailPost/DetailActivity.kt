package com.app.barterbuddy.ui.detailPost

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.app.barterbuddy.R
import com.app.barterbuddy.ViewModelsFactory
import com.app.barterbuddy.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseUser
    private lateinit var binding: ActivityDetailBinding
    private val detailPostViewModel by viewModels<DetailPostViewModel> {
        ViewModelsFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val postId = intent.getStringExtra(POST_ID)
        auth = Firebase.auth.currentUser!!

        lifecycleScope.launch {
            detailPostViewModel.getDetailPost(postId!!).observe(this@DetailActivity){ item ->
                binding.title.text = item.title
                binding.subtitle.text = item.updatedAt
                binding.desc.text = item.description
                binding.status.text = item.status
                binding.interest.text = item.interestCount.toString()
                Glide.with(this@DetailActivity).load(item.image).into(binding.imageView)

                lifecycleScope.launch {
                    detailPostViewModel.getDetailUser(auth.uid).observe(this@DetailActivity){ user ->
                        val isInterested = user.interestedPosts.contains(item.id)
                        if (isInterested){
                            binding.fav.setImageResource(R.drawable.baseline_favorite_24)
                        } else {
                            binding.fav.setImageResource(R.drawable.baseline_favorite_border_24)
                        }
                        binding.fav.setOnClickListener {
                            if (isInterested) {
                                lifecycleScope.launch {
                                    detailPostViewModel.postUninterest(user.userId, item.id).observe(this@DetailActivity) { successPost ->
                                        if (successPost.success) {
                                            binding.fav.setImageResource(R.drawable.baseline_favorite_border_24)
                                            messageToast("Berhasil Dihilangkan dari List Interest")
                                        } else {
                                            binding.fav.setImageResource(R.drawable.baseline_favorite_24)
                                            messageToast("Gagal Dihilangkan dari List Interest")
                                        }
                                    }
                                }
                            } else {
                                lifecycleScope.launch {
                                    detailPostViewModel.postInterest(user.userId, item.id).observe(this@DetailActivity) { successPost ->
                                        if (successPost.success) {
                                            binding.fav.setImageResource(R.drawable.baseline_favorite_24)
                                            messageToast("Berhasil Ditambahkan Ke List Interest")
                                        } else {
                                            binding.fav.setImageResource(R.drawable.baseline_favorite_border_24)
                                            messageToast("Gagal Ditambahkan Ke List Interest")
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private fun messageToast(s: String) {
        Toast.makeText(this@DetailActivity, s, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val POST_ID = "postId"
    }
}