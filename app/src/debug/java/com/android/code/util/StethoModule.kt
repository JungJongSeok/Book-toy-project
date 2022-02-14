package com.android.code.util

import android.content.Context
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient

class StethoModule {
    companion object {
        fun initializeWithDefaults(context: Context) {
            Stetho.initializeWithDefaults(context)
        }
    }
}

fun OkHttpClient.Builder.addStethoInterceptor(): OkHttpClient.Builder {
    return this.addNetworkInterceptor(StethoInterceptor())
}