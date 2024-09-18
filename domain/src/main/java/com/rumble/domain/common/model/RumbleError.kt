package com.rumble.domain.common.model

import okhttp3.Response

data class RumbleError(
    val tag: String,
    val requestUrl: String,
    val code: Int,
    val message: String,
    val rawResponse: String = "",
    val method: String = "",
    val body: String = "",
) {
    constructor(tag: String = "", response: Response) : this(
        tag = tag,
        requestUrl = response.request.url.toString(),
        code = response.code,
        message = response.message,
        rawResponse = response.toString(),
        method = response.request.method,
        body = response.body?.toString() ?: ""
    )

    constructor(tag: String = "", response: Response, customMessage: String) : this(
        tag = tag,
        requestUrl = response.request.url.toString(),
        code = response.code,
        message = customMessage,
        rawResponse = response.toString(),
        method = response.request.method,
        body = response.body?.toString() ?: ""
    )

    constructor(tag: String = "", customMessage: String, code: Int) : this(
        tag = tag,
        requestUrl = "",
        code = code,
        message = customMessage,
    )
}