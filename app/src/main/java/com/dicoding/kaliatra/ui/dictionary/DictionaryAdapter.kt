package com.dicoding.kaliatra.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.kaliatra.databinding.ItemDictionaryBinding
import com.bumptech.glide.Glide
import com.dicoding.kaliatra.R

class DictionaryAdapter : ListAdapter<DictionaryItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DictionaryItem.CategoryHeader -> VIEW_TYPE_HEADER
            is DictionaryItem.DictionaryEntry -> VIEW_TYPE_ENTRY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_header, parent, false)
                CategoryViewHolder(view)
            }

            VIEW_TYPE_ENTRY -> {
                val binding = ItemDictionaryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                DictionaryViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> {
                val categoryHeader = getItem(position) as DictionaryItem.CategoryHeader
                holder.bind(categoryHeader)
            }

            is DictionaryViewHolder -> {
                val entry = getItem(position) as DictionaryItem.DictionaryEntry
                holder.bind(entry)
            }
        }
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryText: TextView = itemView.findViewById(R.id.category_header)

        fun bind(categoryHeader: DictionaryItem.CategoryHeader) {
            categoryText.text = categoryHeader.categoryName
        }
    }

    class DictionaryViewHolder(private val binding: ItemDictionaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: DictionaryItem.DictionaryEntry) {
            binding.tvLatin.text = entry.data.tulisanlatin
            binding.tvDecription.text = entry.data.deskripsi
            Glide.with(binding.root.context)
                .load(entry.data.imageUrl)
                .into(binding.imgAksara)
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ENTRY = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DictionaryItem>() {
            override fun areItemsTheSame(
                oldItem: DictionaryItem,
                newItem: DictionaryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DictionaryItem,
                newItem: DictionaryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}