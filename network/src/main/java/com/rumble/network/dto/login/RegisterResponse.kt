package com.rumble.network.dto.login

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("data")
    val registerData: RegisterResponse? = null,
    @SerializedName("success")
    private val _success: JsonPrimitive?,
    @SerializedName("name")
    private val _name: JsonElement,
    @SerializedName("username")
    private val _username: JsonElement,
    @SerializedName("email")
    private val _email: JsonElement,
    @SerializedName("password")
    private val _password: JsonElement,
    @SerializedName("birthday")
    private val _birthday: JsonElement,
    @SerializedName("terms")
    private val _terms: JsonElement,
    @SerializedName("error")
    private val _error: JsonElement,
) {
    val success: Boolean
        get() {
            return when {
                _success == null -> false
                _success.isBoolean -> _success.asBoolean
                _success.isNumber -> _success.asInt == 1
                else -> false
            }
        }

    private val name: String
        get() = mapStringOrStringArray(_name)
    private val username: String
        get() = mapStringOrStringArray(_username)
    private val email: String
        get() = mapStringOrStringArray(_email)
    private val password: String
        get() = mapStringOrStringArray(_password)
    private val birthday: String
        get() = mapStringOrStringArray(_birthday)
    private val terms: String
        get() = mapStringOrStringArray(_terms)
    private val error: String
        get() = mapStringOrStringArray(_error)

    private fun mapStringOrStringArray(element: JsonElement) = when (element) {
        is JsonNull -> ""
        is JsonPrimitive -> element.asString
        is JsonArray -> element.map { it.asString }.firstOrNull() ?: ""
        else -> ""
    }

    fun getFirstError(): String? = when {
        name.isNotEmpty() -> name
        username.isNotEmpty() -> username
        email.isNotEmpty() -> email
        password.isNotEmpty() -> password
        birthday.isNotEmpty() -> birthday
        terms.isNotEmpty() -> terms
        error.isNotEmpty() -> error
        else -> null
    }
}