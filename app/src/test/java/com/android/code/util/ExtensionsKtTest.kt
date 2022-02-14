package com.android.code.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class ExtensionsKtTest {
    data class TestData(
        val a: String = "a",
        val b: Int = 1,
        val c: List<Double> = listOf(1.0, 2.0)
    )
    private val testDataJson = "{\"a\":\"a\",\"b\":1,\"c\":[1.0,2.0]}"

    private val list = listOf(1, 2, 3)
    private val listNothingToken = listOf(1.0, 2.0, 3.0)
    private val listJson = "[1,2,3]"

    private val map = mapOf("a" to 1, "b" to 2, "c" to 3)
    private val mapNothingToken = mapOf("a" to 1.0, "b" to 2.0, "c" to 3.0)
    private val mapJson = "{\"a\":1,\"b\":2,\"c\":3}"


    @Test
    @DisplayName("JsonString 을 Object 로 변환 한다. 일반적인 경우 but Collection 일때 정상 작동 안할 수 있음.")
    fun fromJson() {
        assertEquals(testDataJson.fromJson<TestData>(), TestData())
        assertEquals(listJson.fromJson<List<Int>>(), listNothingToken)
        assertEquals(mapJson.fromJson<Map<String, Int>>(), mapNothingToken)
    }

    @Test
    @DisplayName("JsonString 을 Object 로 변환 한다. Collection 일 경우 사용")
    fun fromJsonWithTypeToken() {
        assertEquals(testDataJson.fromJsonWithTypeToken<TestData>(), TestData())
        assertEquals(listJson.fromJsonWithTypeToken<List<Int>>(), list)
        assertEquals(mapJson.fromJsonWithTypeToken<Map<String, Int>>(), map)
    }

    @Test
    @DisplayName("Object 을 JsonString 로 변환 한다.")
    fun toJson() {
        assertEquals(TestData().toJson(), testDataJson)
        assertEquals(list.toJson(), listJson)
        assertEquals(map.toJson(), mapJson)
    }

    @Test
    fun empty() {
        assertEquals(String.empty(), "")
    }

    @Test
    fun space() {
        assertEquals(String.space(), " ")
    }

    @Test
    fun zero() {
        assertEquals(Int.zero(), 0)
        assertEquals(Long.zero(), 0L)
    }
}