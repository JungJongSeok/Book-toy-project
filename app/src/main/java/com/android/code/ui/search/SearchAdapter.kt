package com.android.code.ui.search

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.android.code.databinding.HolderSearchDataBinding
import com.android.code.databinding.HolderSearchRecentBinding
import com.android.code.util.ViewDetectable

class SearchAdapter(private val property: SearchAdapterProperty) :
    ListAdapter<SearchData, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<SearchData>() {
            override fun areContentsTheSame(oldItem: SearchData, newItem: SearchData): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: SearchData, newItem: SearchData): Boolean {
                return true
            }
        }
    ) {
    companion object {
        private const val TYPE_BASE = 0
        private const val TYPE_RECENT = 1
    }

    private val restoredHolderMap = HashMap<String, Parcelable?>()

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (itemCount <= holder.layoutPosition || RecyclerView.NO_POSITION == holder.layoutPosition) {
            return
        }
        if (holder is RecentHolder) {
            val data = getItem(holder.layoutPosition) as? SearchRecentData ?: return
            restoredHolderMap[data.hashCode().toString()] =
                holder.binding.recyclerView.layoutManager?.onSaveInstanceState()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchBaseData -> TYPE_BASE
            is SearchRecentData -> TYPE_RECENT
            else -> throw IllegalArgumentException("Do not define Type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_BASE -> SearchHolder(
                HolderSearchDataBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_RECENT -> RecentHolder(
                HolderSearchRecentBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            else -> throw IllegalArgumentException("Do not define Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchHolder -> {
                holder.binding.apply {
                    property = this@SearchAdapter.property
                    val searchBaseData = getItem(position) as? SearchBaseData
                    data = searchBaseData
                    isSelected = searchBaseData == this@SearchAdapter.property.searchedData.value
                    executePendingBindings()
                }
            }
            is RecentHolder -> {
                (holder.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)
                    ?.isFullSpan = true
                holder.binding.apply {
                    recyclerView.layoutManager =
                        LinearLayoutManager(this.root.context, RecyclerView.HORIZONTAL, false)
                    val adapter =
                        SearchRecentAdapter(object : SearchRecentAdapterProperty {
                            override val searchedText: LiveData<String>
                                get() = property.searchedText

                            override fun search(text: String) {
                                property.search(text)
                            }

                            override fun removeRecentSearch(text: String) {
                                property.removeRecentSearch(text)
                                submitListAfterRemovedRecentSearch(text)
                            }

                            fun submitListAfterRemovedRecentSearch(text: String) {
                                val adapter =
                                    (recyclerView.adapter as? SearchRecentAdapter) ?: return
                                val list = adapter.currentList.filter { it != text }
                                adapter.submitList(list)
                            }
                        })
                    recyclerView.adapter = adapter
                    val data = getItem(position) as? SearchRecentData
                    data?.run {
                        adapter.submitList(this.recentList)
                        restoredHolderMap[this.hashCode().toString()]?.let {
                            recyclerView.layoutManager?.onRestoreInstanceState(it)
                        }
                    }
                    executePendingBindings()
                }
            }

        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is ViewDetectable) {
            holder.onViewAttachedToWindow(holder)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is ViewDetectable) {
            holder.onViewDetachedFromWindow(holder)
        }
    }

    private inner class SearchHolder(val binding: HolderSearchDataBinding) :
        RecyclerView.ViewHolder(binding.root), ViewDetectable {
        private val observer = Observer<SearchBaseData> {
            binding.isSelected = it == binding.data
        }

        override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
            property.searchedData.observeForever(observer)
        }

        override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
            property.searchedData.removeObserver(observer)
        }

    }

    private inner class RecentHolder(val binding: HolderSearchRecentBinding) :
        RecyclerView.ViewHolder(binding.root)
}