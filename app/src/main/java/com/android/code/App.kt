package com.android.code

import android.app.Application
import com.android.code.lib.koin.managerModule
import com.android.code.lib.koin.networkModule
import com.android.code.lib.koin.repositoryModule
import com.android.code.lib.koin.uiModule
import com.android.code.util.FlipperModule
import com.android.code.util.StethoModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class App : Application() {

    private val appModule by lazy {
        listOf(
            uiModule,
            repositoryModule,
            networkModule
        )
    }


    companion object {
        lateinit var instance: App
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(appModule)
        }

        if (BuildConfig.DEBUG) {
            // Timber Initialize
            Timber.uprootAll()
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    val threadName = Thread.currentThread().name
                    return "<$threadName> (${element.fileName}:${element.lineNumber})#${element.methodName} "
                }
            })
        }

        StethoModule.initializeWithDefaults(this)
        FlipperModule.initialize(this)
    }
}