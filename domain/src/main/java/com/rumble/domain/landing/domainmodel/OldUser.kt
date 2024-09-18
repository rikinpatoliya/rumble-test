package com.rumble.domain.landing.domainmodel

import com.google.gson.annotations.SerializedName

class OldUser {

    @SerializedName("userId")
    var userId = 0

    @SerializedName("username")
    var username: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("isLoggedIn")
    var isLoggedIn = false

    @SerializedName("facebookId")
    var facebookId: String? = null

    @SerializedName("balance")
    var balance = 0f

    @SerializedName("email")
    var email: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("pictureUrl")
    var pictureUrl: String? = null

    @SerializedName("payinfo")
    var payinfo: String? = null

    @SerializedName("address1")
    var address: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("stateprov")
    var stateprov: String? = null

    @SerializedName("postalcode")
    var postalcode: String? = null

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("countryID")
    var countryId: String? = null

    @SerializedName("profileUpdated")
    var profileUpdated = false

    @SerializedName("apiKey")
    var apiKey: String? = null

    @SerializedName("validated")
    var isValidated = 0

    @SerializedName("cookie")
    var cookie: String? = null

    @SerializedName("rumblesScore")
    var rumblesScore = 0

    @SerializedName("payInfoConfirmed")
    var payInfoConfirmed = false

    @SerializedName("following_count")
    var following = 0

    @SerializedName("followers_count")
    var followers = 0

    override fun toString(): String {
        return "User{" +
            "userId=" + userId +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", isLoggedIn=" + isLoggedIn +
            ", facebookId='" + facebookId + '\'' +
            ", balance=" + balance +
            ", email='" + email + '\'' +
            ", name='" + name + '\'' +
            ", pictureUrl='" + pictureUrl + '\'' +
            ", payinfo='" + payinfo + '\'' +
            ", address='" + address + '\'' +
            ", city='" + city + '\'' +
            ", stateprov='" + stateprov + '\'' +
            ", postalcode='" + postalcode + '\'' +
            ", phone='" + phone + '\'' +
            ", countryId='" + countryId + '\'' +
            ", profileUpdated=" + profileUpdated +
            ", apiKey='" + apiKey + '\'' +
            ", isValidated=" + isValidated +
            ", cookie='" + cookie + '\'' +
            ", rumblesScore=" + rumblesScore +
            ", payInfoConfirmed=" + payInfoConfirmed +
            ", following=" + following +
            ", followers=" + followers +
            '}'
    }
}