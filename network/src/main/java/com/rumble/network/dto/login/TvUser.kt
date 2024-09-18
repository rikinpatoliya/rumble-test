package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

data class TvUser(
    val id : Int = 0,
    @SerializedName("username")
    val userName: String =  "",
    @SerializedName("profile_pic")
    val profilePic: TvUserProfilePicture = TvUserProfilePicture(),
)
