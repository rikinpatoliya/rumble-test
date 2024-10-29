package com.rumble.analytics

sealed interface AnalyticUserProperty {
    val propertyName: String
    val propertyValue: String?
}
