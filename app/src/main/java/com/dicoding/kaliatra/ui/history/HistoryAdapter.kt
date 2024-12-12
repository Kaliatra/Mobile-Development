package com.dicoding.kaliatra.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.kaliatra.R
import com.dicoding.kaliatra.databinding.ItemHistoryBinding
import com.dicoding.kaliatra.remote.response.HistoryData
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(private val onDeleteClick: (String) -> Unit) :
    ListAdapter<HistoryData, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val dataItem = getItem(position)
        holder.bind(dataItem)
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataItem: HistoryData) {
            val topPredictions = dataItem.topPredictions.joinToString(", ")
            binding.tvResult.text =
                topPredictions.ifEmpty { "No result available" }

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = inputFormat.parse(dataItem.createdAt)
            val formattedDate = date?.let { outputFormat.format(it) } ?: dataItem.createdAt
            binding.tvTimestamp.text = formattedDate

            if (dataItem.imageURL.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(dataItem.imageURL)
                    .placeholder(R.drawable.ic_preview_image)
                    .into(binding.imageResult)
            } else {
                binding.imageResult.setImageResource(R.drawable.ic_preview_image)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(dataItem.id)
            }
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryData>() {
        override fun areItemsTheSame(oldItem: HistoryData, newItem: HistoryData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryData, newItem: HistoryData): Boolean {
            return oldItem == newItem
        }
    }
}
