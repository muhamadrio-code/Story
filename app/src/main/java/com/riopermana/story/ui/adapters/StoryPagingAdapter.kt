package com.riopermana.story.ui.adapters

import android.graphics.Typeface.BOLD
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.riopermana.story.R
import com.riopermana.story.databinding.FragmentStoryItemBinding
import com.riopermana.story.model.Story
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class StoryPagingAdapter : PagingDataAdapter<Story, StoryPagingAdapter.StoryViewHolder>(
    StoryDiffUtil()
) {

    private val originalFormat: DateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    private var clickListener: ((Story) -> Unit)? = null

    inner class StoryViewHolder(private val binding: FragmentStoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Story) {
            val date = originalFormat.parse(item.createdAt)
            binding.apply {
                val relativeTimeString = date?.let {
                    val timiMillis = date.time
                    DateUtils.getRelativeTimeSpanString(
                        timiMillis,
                        System.currentTimeMillis() - TimeUnit.HOURS.toMillis(7L),
                        DateUtils.MINUTE_IN_MILLIS
                    )
                }
                val textPlaceholder = root.context.getString(R.string.story_by_placeholder)
                val text = "$textPlaceholder ${item.name} · $relativeTimeString"
                val start = textPlaceholder.length + 1
                val end = start + item.name.length
                val spannable =
                    SpannableString(text).apply {
                        setSpan(
                            StyleSpan(BOLD),
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                tvItemName.text = spannable

                ivItemPhoto.load(item.photoUrl) {
                    error(R.drawable.ic_broken_image)
                    target(
                        onStart = {
                            progressCircular.isVisible = true
                        },
                        onError = {
                            progressCircular.isVisible = false
                            ivItemPhoto.setImageDrawable(it)
                        },
                        onSuccess = {
                            progressCircular.isVisible = false
                            ivItemPhoto.setImageDrawable(it)
                        }
                    )
                }
                root.setOnClickListener {
                    clickListener?.invoke(item)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (Story) -> Unit) {
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

        if (item != null) {
            holder.bind(item)
        }
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
