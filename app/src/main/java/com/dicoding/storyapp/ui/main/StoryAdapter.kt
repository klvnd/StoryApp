package com.dicoding.storyapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemStoryBinding

class StoryAdapter(private val stories: List<ListStoryItem?>) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int = stories.size

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem?) {
            binding.apply {
                tvTitle.text = story?.name
                tvContent.text = story?.description
                Glide.with(itemView.context)
                    .load(story?.photoUrl)
                    .into(ivUser)
            }
        }
    }
}
