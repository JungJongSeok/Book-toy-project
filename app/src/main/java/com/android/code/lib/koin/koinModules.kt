package com.android.code.lib.koin

import com.android.code.lib.network.BookService
import com.android.code.lib.network.provideAPIClientService
import com.android.code.repository.BookRepository
import com.android.code.repository.BookRepositoryImpl
import com.android.code.repository.SearchRepository
import com.android.code.repository.SearchRepositoryImpl
import com.android.code.ui.book.BookViewModel
import com.android.code.ui.main.MainViewModel
import com.android.code.ui.search.SearchRxBaseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { MainViewModel() }
    viewModel { SearchRxBaseViewModel(get()) }
    viewModel { BookViewModel(get()) }
}

val repositoryModule = module {
    factory<SearchRepository> { SearchRepositoryImpl(get()) }
    factory<BookRepository> { BookRepositoryImpl(get()) }
}

val networkModule = module {
    single { provideAPIClientService<BookService>() }
}