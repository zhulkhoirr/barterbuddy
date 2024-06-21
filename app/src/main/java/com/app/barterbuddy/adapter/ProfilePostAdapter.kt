package com.app.barterbuddy.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.barterbuddy.databinding.ListPostUserBinding
import com.app.barterbuddy.di.models.PostsItem
import com.app.barterbuddy.ui.profile.ProfileViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ProfilePostAdapter(
    private val profileViewModel: ProfileViewModel,
    private val coroutineScope: CoroutineScope
): ListAdapter<PostsItem, ProfilePostAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListPostUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, coroutineScope)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = getItem(position)
        holder.bind(dataItem, profileViewModel)
    }

    class ViewHolder(
        private val binding: ListPostUserBinding,
        private val coroutineScope: CoroutineScope
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostsItem, viewModel: ProfileViewModel) {
            Glide.with(itemView.context).load(item.image).into(binding.imageView2)
            binding.title2.text = item.title
            binding.subtitle.text = item.updatedAt
            binding.desc.text = item.description
            binding.delete.setOnClickListener {
                coroutineScope.launch {
                    try {
                        viewModel.deletePostById(userId = item.userId, postId = item.id)
                        Toast.makeText(itemView.context, "Postingan Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception){
                        Toast.makeText(itemView.context, "Postingan Gagal Dihapus ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.d("Delete Post", e.message.toString())
                    }
                }
            }
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