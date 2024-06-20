package com.app.barterbuddy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.barterbuddy.databinding.ListPostBinding
import com.app.barterbuddy.di.models.PostsItem
import com.bumptech.glide.Glide

class PostAdapter: ListAdapter<PostsItem, PostAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = getItem(position)
        holder.bind(dataItem)
    }

    class ViewHolder(private val binding: ListPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostsItem) {
            binding.title.text = item.title
            Glide.with(itemView.context).load(item.image).into(binding.imageView2)
            binding.title2.text = item.title
            binding.subtitle.text = item.updatedAt
            binding.desc.text = item.description
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