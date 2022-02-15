package com.android.code.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import com.android.code.App
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Function3
import io.reactivex.rxjava3.functions.Function4
import io.reactivex.rxjava3.functions.Function5
import timber.log.Timber
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

data class Quadruple<P1, P2, P3, P4>(val first: P1, val second: P2, val third: P3, val fourth: P4)

data class Quintuple<P1, P2, P3, P4, P5>(
    val first: P1, val second: P2, val third: P3, val fourth: P4, val fifth: P5
)

fun <P1 : Any, P2 : Any> zipToPair(): BiFunction<P1, P2, Pair<P1, P2>> {
    return BiFunction { t1, t2 -> Pair(t1, t2) }
}

fun <P1 : Any, P2 : Any, P3 : Any> zipToTriple()
    : Function3<P1, P2, P3, Triple<P1, P2, P3>> {
    return Function3 { t1, t2, t3 -> Triple(t1, t2, t3) }
}

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any> zipToQuadruple()
    : Function4<P1, P2, P3, P4, Quadruple<P1, P2, P3, P4>> {
    return Function4 { t1, t2, t3, t4 -> Quadruple(t1, t2, t3, t4) }
}

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> zipToQuintuple()
    : Function5<P1, P2, P3, P4, P5, Quintuple<P1, P2, P3, P4, P5>> {
    return Function5 { t1, t2, t3, t4, t5 -> Quintuple(t1, t2, t3, t4, t5) }
}

fun Context.startUrl(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
        Timber.e(e)
    }
}

fun String.Companion.empty() = ""
fun String.Companion.space() = " "
fun Int.Companion.zero() = 0
fun Long.Companion.zero() = 0L
