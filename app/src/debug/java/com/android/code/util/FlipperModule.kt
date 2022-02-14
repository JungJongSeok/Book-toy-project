package com.android.code.util

import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import okhttp3.OkHttpClient

class FlipperModule {
    companion object {
        fun initialize(context: Context) {
            Fresco.initialize(context)
            AndroidFlipperClient.getInstance(context).apply {
                addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
                addPlugin(networkFlipperPlugin)
            }.start()
        }

        val networkFlipperPlugin = NetworkFlipperPlugin()
    }
}

fun OkHttpClient.Builder.addFlipperInterceptor(): OkHttpClient.Builder {
    return this.addNetworkInterceptor(FlipperOkhttpInterceptor(FlipperModule.networkFlipperPlugin))
}