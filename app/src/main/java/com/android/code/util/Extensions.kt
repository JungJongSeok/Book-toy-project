package com.android.code.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import com.android.code.App
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.StringReader

// Json
inline fun <reified T> String.fromJson(): T {
    val reader = JsonReader(StringReader(this)).apply {
        isLenient = true
    }
    return Gson().fromJson(reader, T::class.java)
}

inline fun <reified T> String.fromJsonWithTypeToken(): T {
    val reader = JsonReader(StringReader(this)).apply {
        isLenient = true
    }
    return Gson().fromJson(reader, object : TypeToken<T>() {}.type)
}

fun Any.toJson(): String = Gson().toJson(this)

fun Int.toDp(): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    App.instance.resources.displayMetrics
)

fun Int.toDp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
)

fun Int.toPx(): Int = (this * App.instance.resources.displayMetrics.density).toInt()

fun Float.toPx(): Float = (this * App.instance.resources.displayMetrics.density)

fun String.Companion.empty() = ""
fun String.Companion.space() = " "
fun Int.Companion.zero() = 0
fun Long.Companion.zero() = 0L
