package com.app.barterbuddy.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.barterbuddy.databinding.ListInterestBinding
import com.app.barterbuddy.di.models.PostsItem
import com.app.barterbuddy.ui.detailPost.DetailActivity
import com.bumptech.glide.Glide

class InterestAdapter: ListAdapter<PostsItem, InterestAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListInterestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = getItem(position)
        holder.bind(dataItem)
    }

    class ViewHolder(
        private val binding: ListInterestBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostsItem) {
            Glide.with(itemView.context).load(item.image).into(binding.image)
            binding.title.text = item.title
            binding.subtitle.text = item.updatedAt

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.POST_ID, item.id)
                it.context.startActivity(intent)
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