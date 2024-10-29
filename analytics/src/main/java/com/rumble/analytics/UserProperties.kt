package com.rumble.analytics

data class UIDUserProperty(
    val value: String?,
) : AnalyticUserProperty {
    override val propertyName: String = "uid"
    override val propertyValue: String? = value
}

data class IIDUserProperty(
    val value: String?,
) : AnalyticUserProperty {
    override val propertyName: String = "iid"
    override val propertyValue: String? = value
}
