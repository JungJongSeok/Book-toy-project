package com.android.code.util

import java.util.regex.Pattern

class SearchParser {
    companion object {
        private const val SPECIAL_REGEX = "[`~!@#\$%^&*|\\\\\\'\\\";:\\/?^=^+_()<> ].+"

        fun parse(text: String): ParsedSearchQuery {
            val excludedQueries = text.split("-").mapIndexedNotNull { index, s ->
                if (index == 0) {
                    null
                } else {
                    removeSpecialText(s)
                }
            }

            val query = text.split("|").joinToString("|") {
                removeSpecialText(it)
            }

            return ParsedSearchQuery(query, excludedQueries)
        }

        private fun removeSpecialText(text: String): String {
            val pattern = Pattern.compile(SPECIAL_REGEX)
            val result = pattern.matcher(text)
            result.find()
            val specialText = result.group()
            return text.replace(specialText, "")
        }
    }
}

data class ParsedSearchQuery(
    val query: String,
    val excludedQueries: List<String>,
)