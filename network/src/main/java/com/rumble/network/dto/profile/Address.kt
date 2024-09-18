package com.rumble.network.dto.profile

import com.google.gson.annotations.SerializedName

/**
 * Address model
 *
 * @param addId Integer.
 * @param fullName String.
 * @param phone String.
 * @param address1 String.
 * @param address2 String.
 * @param payInfo String.
 * @param payMethod String.
 * @param city String.
 * @param stateProv String.
 * @param postalCode String.
 * @param countryID Integer. Optional.
 * @param countryName String. Optional.
 * @param is_payInfo_confirmed Boolean.
 *
 */

data class Address(
    @SerializedName("addid")
    val addId: Int,
    @SerializedName("fullname")
    val fullName: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("address1")
    val address1: String,
    @SerializedName("address2")
    val address2: String,
    @SerializedName("payinfo")
    val payInfo: String,
    @SerializedName("paymethod")
    val payMethod: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("stateprov")
    val stateProv: String,
    @SerializedName("postalcode")
    val postalCode: String,
    @SerializedName("countryID")
    val countryID: Int,
    @SerializedName("countryName")
    val countryName: String,
    @SerializedName("is_payinfo_confirmed")
    val is_payInfo_confirmed: Boolean,
)