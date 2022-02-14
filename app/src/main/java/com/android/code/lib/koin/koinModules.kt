package com.android.code.lib.koin

import com.android.code.lib.network.BookRxService
import com.android.code.lib.network.BookService
import com.android.code.lib.network.provideAPIClientService
import com.android.code.repository.SearchRepository
import com.android.code.repository.SearchRepositoryImpl
import com.android.code.repository.SearchRxRepository
import com.android.code.repository.SearchRxRepositoryImpl
import com.android.code.ui.main.MainViewModel
import com.android.code.ui.search.SearchBaseViewModel
import com.android.code.ui.search.SearchRxBaseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { MainViewModel() }
    viewModel { SearchBaseViewModel(get()) }
    viewModel { SearchRxBaseViewModel(get()) }
}

val repositoryModule = module {
    factory<SearchRepository> { SearchRepositoryImpl(get()) }
    factory<SearchRxRepository> { SearchRxRepositoryImpl(get()) }
}

val networkModule = module {
    single { provideAPIClientService<BookService>() }
    single { provideAPIClientService<BookRxService>() }
}