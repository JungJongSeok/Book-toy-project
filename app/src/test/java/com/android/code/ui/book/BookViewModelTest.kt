package com.android.code.ui.book

import com.android.code.CoroutinesTestExtension
import com.android.code.InstantExecutorExtension
import com.android.code.models.DetailResponse
import com.android.code.repository.BookRepository
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Supplier
import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler.ExecutorWorker
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis


@kotlinx.coroutines.ExperimentalCoroutinesApi
@DisplayName("BookViewModel 테스트")
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
internal class BookViewModelTest {
    private lateinit var bookViewModel: BookViewModel

    @BeforeEach
    fun setUp() {
        val immediate: Scheduler = object : Scheduler() {
            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorWorker(Executor { obj: Runnable -> obj.run() }, false, false)
            }
        }

        RxJavaPlugins.setInitIoSchedulerHandler { immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }

        val detailResponse: DetailResponse = mock {
            on { title } doReturn "sample"
        }
        val bookRepository: BookRepository = object : BookRepository {
            override fun detail(isbn13: String): Single<DetailResponse> {
                return Single.fromCallable { detailResponse }
            }
        }
        bookViewModel = BookViewModel(bookRepository)
    }

    @Test
    @DisplayName("init api 의 호출한다.")
    fun init() {
        runBlocking {
            val totalExecutionTime = measureTimeMillis {
                bookViewModel.init("1234")
                assertEquals(bookViewModel.outputs.detailData.value?.title, "sample")
            }

            println("init() Total Time: $totalExecutionTime")
        }
    }

    @AfterEach
    fun done() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}