package com.rumble.network

import okhttp3.HttpUrl

object CustomPerformanceUrlList {
    private val urlList = listOf(
        "service.php"
    )

    fun urlIsInList(url: HttpUrl): Boolean {
        val urlStr  = url.toString()
        return urlList.find { urlStr.contains(it) } != null
    }
}