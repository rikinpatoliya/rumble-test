package com.rumble.network.dto.settings

import com.google.gson.annotations.SerializedName

data class AuthProviders(
    @SerializedName("apple")
    val apple: AuthProvider,
    @SerializedName("facebook")
    val facebook: AuthProvider,
    @SerializedName("google")
    val google: AuthProvider,
)

data class AuthProvider(
    @SerializedName("can_unlink")
    val canUnlink: Boolean,
    @SerializedName("is_connected")
    val isConnected: Boolean
)