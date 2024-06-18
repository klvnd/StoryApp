package com.dicoding.storyapp.ui.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemStoryBinding

class StoryAdapter(
    private val stories: List<ListStoryItem?>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(story: ListStoryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story, itemClickListener)
    }

    override fun getItemCount(): Int = stories.size

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem?, itemClickListener: OnItemClickListener) {
            binding.apply {
                tvTitle.text = story?.name
                tvContent.text = story?.description
                Glide.with(itemView.context)
                    .load(story?.photoUrl)
                    .into(ivUser)

                root.setOnClickListener {
                    story?.let { itemClickListener.onItemClick(it) }
                }
            }
        }
    }
}