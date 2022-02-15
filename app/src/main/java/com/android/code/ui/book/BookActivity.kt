package com.android.code.ui.book

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.android.code.BuildConfig
import com.android.code.R
import com.android.code.databinding.ActivityBookBinding
import com.android.code.models.Book
import com.android.code.ui.BaseActivity
import com.android.code.ui.RequiresActivityViewModel


@RequiresActivityViewModel(value = BookViewModel::class)
class BookActivity : BaseActivity<ActivityBookBinding, BookViewModel>() {
    companion object {
        private const val BUNDLE_BOOK = "bundle_book"
        fun startActivity(context: Context, book: Book) {
            context.startActivity(Intent(context, BookActivity::class.java)
                .putExtra(BUNDLE_BOOK, book))
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_book

    private val book: Book? by lazy {
        intent.getParcelableExtra(BUNDLE_BOOK)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.requestManager = requestManager
        binding.book = book
    }

    override fun setViewModelOutputs() {
        viewModel.outputs.detailData.observe(this) {
            requestManager.load(BuildConfig.IMAGE_URL + it.isbn13).into(binding.thumbnail)
            binding.title.text = it.title
            binding.subtitle.text = it.subtitle
            binding.price.text = it.price
        }
    }

    override fun setViewModelInputs() {
        viewModel.init(book?.isbn13 ?: return)
    }
}