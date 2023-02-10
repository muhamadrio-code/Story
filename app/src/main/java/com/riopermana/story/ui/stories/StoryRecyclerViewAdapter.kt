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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class StoryRecyclerViewAdapter : ListAdapter<Story,StoryRecyclerViewAdapter.StoryViewHolder>(StoryDiffUtil()) {

    private val originalFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    private val targetFormat: DateFormat = SimpleDateFormat("E, MMM dd yyyy", Locale.US)
    private var clickListener: ((Story) -> Unit)? = null
    inner class StoryViewHolder(private val binding: FragmentStoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Story) {
            val date = originalFormat.parse(item.createdAt)
            binding.tvItemName.text = item.name
            binding.tvCreatedAt.text = date?.let { targetFormat.format(date) } ?: ""
            binding.ivItemPhoto.load(item.photoUrl) {
                placeholder(R.drawable.ic_image)
                transformations(RoundedCornersTransformation(20f,20f,20f,20f))
            }
            binding.root.setOnClickListener {
                clickListener?.invoke(item)
            }
        }
    }

    fun setOnItemClickListener(listener:(Story) -> Unit) {
        clickListener = listener
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
