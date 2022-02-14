package com.android.code

import android.annotation.SuppressLint
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

@ExperimentalCoroutinesApi
class CoroutinesTestExtension : BeforeEachCallback, AfterEachCallback {
    /**
     * Set TestCoroutine dispatcher as main
     */
    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}


class InstantExecutorExtension : BeforeEachCallback, AfterEachCallback {

    /**
     * sets a delegate before each test that updates LiveData immediately on the calling thread
     */
    @SuppressLint("RestrictedApi")
    override fun beforeEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance()
            .setDelegate(object : TaskExecutor() {
                override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

                override fun postToMainThread(runnable: Runnable) = runnable.run()

                override fun isMainThread(): Boolean = true
            })
    }

    /**
     * Remove delegate after each test, to avoid influencing other tests
     */
    @SuppressLint("RestrictedApi")
    override fun afterEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

}