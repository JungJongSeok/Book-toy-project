package com.android.code

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@DisplayName("유닛 테스트 예제")
class ExampleUnitTest {
    private val fixedValue = "Test Success~"
    private val preMixValue = "Current"

    class ClassForTest(var text: String) {
        fun getCurrentText(): String = ""
    }

    private lateinit var foo: ClassForTest

    @BeforeEach
    fun setUp() {
        foo = mock {
            on { text } doReturn fixedValue
            on { getCurrentText() } doReturn "$preMixValue $fixedValue"
        }
    }

    @Test
    @DisplayName("기본적인 덧셈 테스트 기능을 검증한다.")
    fun checkBasicAssertion() {
        val actual = 2 + 2
        assertEquals(actual, 4)
    }

    @Test
    @DisplayName("ClassForTest 객체의 property 값을 검증한다.")
    fun checkValueOfProperty() {
        assertEquals(foo.text, "Test Success~")
    }

    @Test
    fun checkCountOfCallingMethod() {
        val check = foo.getCurrentText()
        assertEquals(check, "Current Test Success~")
    }
}