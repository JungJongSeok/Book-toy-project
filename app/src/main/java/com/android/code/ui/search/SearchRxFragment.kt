package com.android.code.ui.search

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.code.R
import com.android.code.databinding.FragmentSearchRxBinding
import com.android.code.models.Book
import com.android.code.ui.BaseFragment
import com.android.code.ui.main.MainActivity
import com.android.code.ui.main.MainViewModel
import com.android.code.ui.views.CommonSwipeRefreshLayout
import com.android.code.util.empty
import com.bumptech.glide.RequestManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchRxFragment : BaseFragment<FragmentSearchRxBinding>(),
    CommonSwipeRefreshLayout.OnRefreshListener {
    companion object {
        fun newInstance() = SearchRxFragment()
    }

    private val viewModel: SearchRxBaseViewModel by inject()
    private val mainViewModel: MainViewModel by sharedViewModel()

    private val adapter by lazy {
        SearchAdapter(object : SearchAdapterProperty {
            override val requestManager: RequestManager
                get() = this@SearchRxFragment.requestManager

            override fun clickBook(book: Book) {
                Toast.makeText(requireContext(), book.title, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val layoutManager by lazy {
        LinearLayoutManager(requireContext())
    }

    override fun getLayoutResId(): Int = R.layout.fragment_search_rx

    override fun initView(savedInstanceState: Bundle?) {
        binding.refresh = this

        binding.parent.recyclerView.layoutManager = layoutManager
        binding.parent.recyclerView.adapter = adapter
        binding.parent.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (layoutManager.findLastVisibleItemPosition() > adapter.itemCount - 5
                    && viewModel.canSearchMore()
                ) {
                    viewModel.inputs.searchMore()
                }
            }
        })

        binding.parent.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.parent.manual.isVisible = newText.isNullOrEmpty()
                viewModel.search(newText ?: String.empty())
                return true
            }

        })

        if (savedInstanceState != null) {
            binding.parent.searchView.setQuery(String.empty(), false)
        }
    }

    override fun setViewModelOutputs() {
        viewModel.loading.observe(this) {
            binding.loading.parent.isVisible = it
        }

        viewModel.error.observe(this) {
            showNetworkError(it)
        }

        viewModel.outputs.responseData.observe(this) { (list, isScrolled) ->
            adapter.submitList(list) {
                adapter.notifyDataSetChanged()
                if (isScrolled) {
                    layoutManager.scrollToPositionWithOffset(0, 0)
                }
            }
        }

        viewModel.outputs.refreshedSwipeRefreshLayout.observe(this) {
            binding.parent.refreshLayout.isRefreshing = it
        }

        mainViewModel.outputs.scrollToTop.observe(this) {
            if (it != MainActivity.PAGE_COROUTINE) {
                return@observe
            }
            binding.parent.recyclerView.stopScroll()
            layoutManager.scrollToPositionWithOffset(0, 0)
        }
    }

    override fun setViewModelInputs() {
        // Do noting
    }

    override fun refresh(view: SwipeRefreshLayout) {
        viewModel.inputs.search(binding.parent.searchView.query.toString(), true)
    }
}