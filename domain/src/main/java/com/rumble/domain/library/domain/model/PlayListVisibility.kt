package com.rumble.domain.library.domain.model

import com.rumble.domain.R

enum class PlayListVisibility(val value: String, val titleId: Int, val subtitleId: Int) {
    PUBLIC("public", R.string.public_visibility, R.string.anyone_can_search_playlist),
    UNLISTED("unlisted", R.string.unlisted_visibility, R.string.anyone_with_link_can_view),
    PRIVATE("private", R.string.private_visibility, R.string.only_you_can_view);

    override fun toString(): String = this.value

    companion object {
        fun getByValue(value: String): PlayListVisibility =
            when (value) {
                "public" -> PUBLIC
                "unlisted" -> UNLISTED
                "private" -> PRIVATE
                else -> throw Error("Unsupported PlayListVisibility!")
            }
    }
}