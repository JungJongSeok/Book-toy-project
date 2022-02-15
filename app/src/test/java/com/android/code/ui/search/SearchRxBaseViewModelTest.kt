package com.android.code.ui.search

import com.android.code.CoroutinesTestExtension
import com.android.code.InstantExecutorExtension
import com.android.code.models.Book
import com.android.code.models.SearchResponse
import com.android.code.repository.SearchRepository
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

@kotlinx.coroutines.ExperimentalCoroutinesApi
@DisplayName("SearchRxBaseViewModel 테스트")
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
internal class SearchRxBaseViewModelTest {
    private lateinit var searchRxBaseViewModel: SearchRxBaseViewModel

    @BeforeEach
    fun setUp() {
        val immediate: Scheduler = object : Scheduler() {
            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { obj: Runnable -> obj.run() },
                    false,
                    false)
            }
        }

        RxJavaPlugins.setInitIoSchedulerHandler { immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }

        val book1: Book = mock {
            on { title } doReturn "book1"
            on { isbn13 } doReturn "book1"
        }
        val book2: Book = mock {
            on { title } doReturn "book2"
            on { isbn13 } doReturn "book2"
        }
        val book3: Book = mock {
            on { title } doReturn "book3"
            on { isbn13 } doReturn "book3"
        }
        val searchResponse1: SearchResponse = mock {
            on { books } doReturn listOf(book1, book2)
            on { error } doReturn "0"
            on { page } doReturn "1"
            on { total } doReturn "1"
        }
        val searchResponse2: SearchResponse = mock {
            on { books } doReturn listOf(book2, book3)
            on { error } doReturn "0"
            on { page } doReturn "1"
            on { total } doReturn "1"
        }
        val searchResponse3: SearchResponse = mock {
            on { books } doReturn listOf(book3)
            on { error } doReturn "0"
            on { page } doReturn "2"
            on { total } doReturn "1"
        }
        val searchResponse4: SearchResponse = mock {
            on { books } doReturn listOf(book1, book2, book3)
            on { error } doReturn "0"
            on { page } doReturn "1"
            on { total } doReturn "1"
        }


        val searchRepository: SearchRepository = object : SearchRepository {

            override fun search(query: String, page: Int): Single<SearchResponse> {
                return when (query) {
                    "search1" -> {
                        when (page) {
                            1 -> Single.fromCallable { searchResponse1 }
                            2 -> Single.fromCallable { searchResponse3 }
                            else -> Single.fromCallable { searchResponse4 }
                        }
                    }
                    "search2" -> Single.fromCallable { searchResponse2 }
                    else -> Single.fromCallable { searchResponse4 }
                }
            }
        }
        searchRxBaseViewModel = SearchRxBaseViewModel(searchRepository)
    }

    @Test
    @DisplayName("search api 의 호출한다.")
    fun search() {
        runBlocking {
            val totalExecutionTime = measureTimeMillis {
                searchRxBaseViewModel.search("search1")
                assertEquals(searchRxBaseViewModel.outputs.responseData.value?.first?.map { it.title },
                    listOf("book1", "book2"))
                searchRxBaseViewModel.search("search1-search2")
                assertEquals(searchRxBaseViewModel.outputs.responseData.value?.first?.map { it.title },
                    listOf("book1"))
                searchRxBaseViewModel.search("search1|search2")
                assertEquals(searchRxBaseViewModel.outputs.responseData.value?.first?.map { it.title },
                    listOf("book1", "book2", "book3"))
            }

            println("search() Total Time: $totalExecutionTime")
        }
    }

    @Test
    @DisplayName("pagination api 의 호출한다.")
    fun searchMore() {
        runBlocking {
            val totalExecutionTime = measureTimeMillis {
                searchRxBaseViewModel.search("search1")
                assertEquals(searchRxBaseViewModel.outputs.responseData.value?.first?.map { it.title },
                    listOf("book1", "book2"))

                searchRxBaseViewModel.searchMore()
                assertEquals(searchRxBaseViewModel.outputs.responseData.value?.first?.map { it.title },
                    listOf("book1", "book2", "book3"))


                searchRxBaseViewModel.search("search1-search2")
                assertEquals(searchRxBaseViewModel.outputs.responseData.value?.first?.map { it.title },
                    listOf("book1"))

                searchRxBaseViewModel.searchMore()
                assertEquals(searchRxBaseViewModel.outputs.responseData.value?.first?.map { it.title },
                    listOf("book1"))
            }

            println("searchMore() Total Time: $totalExecutionTime")
        }
    }

    @AfterEach
    fun done() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}