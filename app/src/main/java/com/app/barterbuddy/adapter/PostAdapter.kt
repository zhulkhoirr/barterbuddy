package com.app.barterbuddy.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.barterbuddy.R
import com.app.barterbuddy.databinding.ListPostBinding
import com.app.barterbuddy.di.models.PostsItem
import com.app.barterbuddy.di.models.Users
import com.app.barterbuddy.ui.detailPost.DetailActivity
import com.app.barterbuddy.ui.detailPost.DetailActivity.Companion.POST_ID
import com.app.barterbuddy.ui.home.HomeViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PostAdapter(
    private val homeViewModel: HomeViewModel,
    private val coroutineScope: CoroutineScope,
    private val lifecycleOwner: LifecycleOwner,
    private val users: Users
    ): ListAdapter<PostsItem, PostAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, coroutineScope, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = getItem(position)
        holder.bind(dataItem, homeViewModel, users)
    }

    class ViewHolder(
        private val binding: ListPostBinding,
        private val coroutineScope: CoroutineScope,
        private val lifecycleOwner: LifecycleOwner
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostsItem, homeViewModel: HomeViewModel, users: Users) {
            Glide.with(itemView.context).load(item.image).into(binding.imageView2)
            binding.title2.text = item.title
            binding.subtitle.text = item.updatedAt
            binding.desc.text = item.description
            binding.interest.text = item.interestCount.toString()

            coroutineScope.launch {
                homeViewModel.getDetailUser(item.userId).observe(lifecycleOwner){
                    binding.title.text = it.username
                    Glide.with(itemView.context)
                        .load(it.profileImg)
                        .error(R.drawable.outline_person_24)
                        .into(binding.profile)
                }
            }

            val isInterested = users.interestedPosts.contains(item.id)
            if (isInterested){
                binding.fav.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                binding.fav.setImageResource(R.drawable.baseline_favorite_border_24)
            }

            binding.fav.setOnClickListener {
                if (isInterested) {
                    coroutineScope.launch {
                        homeViewModel.postUninterest(users.userId, item.id).observe(lifecycleOwner) { successPost ->
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
                    coroutineScope.launch {
                        homeViewModel.postInterest(users.userId, item.id).observe(lifecycleOwner) { successPost ->
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

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra(POST_ID, item.id)
                it.context.startActivity(intent)
            }
        }

        private fun messageToast(s: String) {
            Toast.makeText(itemView.context, s, Toast.LENGTH_SHORT).show()
        }
    }



    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostsItem>() {
            override fun areItemsTheSame(oldItem: PostsItem, newItem: PostsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PostsItem, newItem: PostsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}