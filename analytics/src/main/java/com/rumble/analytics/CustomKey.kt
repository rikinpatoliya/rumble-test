package com.rumble.analytics

internal enum class CustomKey(val key: String) {
    TAG_KEY("tag"),
    REQUEST_URL_KEY("requestUrl"),
    CODE_KEY("code"),
    MESSAGE_KEY("message"),
    RAW_RESPONSE_KEY("rawResponse"),
    HTTP_METHOD_KEY("httpMethod"),
    BODY("body")
}