package com.riopermana.story.ui.stories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.riopermana.story.R
import com.riopermana.story.databinding.FragmentStoryItemBinding
import com.riopermana.story.model.Story


class StoryRecyclerViewAdapter : ListAdapter<Story,StoryRecyclerViewAdapter.StoryViewHolder>(StoryDiffUtil()) {

    inner class StoryViewHolder(private val binding: FragmentStoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Story) {
            binding.tvItemName.text = item.name
            binding.tvCreatedAt.text = item.createdAt
            binding.ivItemPhoto.load(item.photoUrl) {
                placeholder(R.drawable.ic_image)
                transformations(RoundedCornersTransformation(20f,20f,20f,20f))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(
            FragmentStoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class StoryDiffUtil : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem == newItem
        }
    }

}
