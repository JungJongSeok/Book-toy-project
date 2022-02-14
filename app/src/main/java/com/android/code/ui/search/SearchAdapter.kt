package com.android.code.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.code.databinding.HolderSearchDataBinding
import com.android.code.models.Book

class SearchAdapter(private val property: SearchAdapterProperty) :
    ListAdapter<Book, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<Book>() {
            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return true
            }
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchHolder(
            HolderSearchDataBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchHolder -> {
                holder.binding.apply {
                    property = this@SearchAdapter.property
                    data = getItem(position)
                    executePendingBindings()
                }
            }
        }
    }

    private inner class SearchHolder(val binding: HolderSearchDataBinding) :
        RecyclerView.ViewHolder(binding.root)
}