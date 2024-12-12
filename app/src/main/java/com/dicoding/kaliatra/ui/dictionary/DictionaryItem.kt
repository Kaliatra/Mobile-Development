package com.dicoding.kaliatra.ui.dictionary

import com.dicoding.kaliatra.remote.response.DictionaryResponse

sealed class DictionaryItem {
    data class CategoryHeader(val categoryName: String) : DictionaryItem()
    data class DictionaryEntry(val data: DictionaryResponse) : DictionaryItem()
}