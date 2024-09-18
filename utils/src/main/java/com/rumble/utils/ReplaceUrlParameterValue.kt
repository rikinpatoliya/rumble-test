package com.rumble.utils

fun replaceUrlParameter(url: String, paramName: String, newValue: String): String {
    val urlParts = url.split("?")
    if (urlParts.size < 2) {
        return url
    }

    val baseUrl = urlParts[0]
    val queryString = urlParts[1]
    val params = queryString.split("&").toMutableList()

    for (index in 0 until params.size) {
        val param = params[index].split("=")
        if (param.size >= 2 && param[0] == paramName) {
            params[index] = "$paramName=$newValue"
            break
        }
    }

    return "$baseUrl?${params.joinToString("&")}"
}



