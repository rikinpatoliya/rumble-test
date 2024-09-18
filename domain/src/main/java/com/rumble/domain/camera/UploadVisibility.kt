package com.rumble.domain.camera

import com.rumble.domain.R

enum class UploadVisibility(val apiValue: String, val titleId: Int, val subtitleId: Int) {
    PUBLIC("public", R.string.public_visibility, R.string.anyone_can_watch),
    UNLISTED("unlisted", R.string.unlisted_visibility, R.string.only_people_with_link_can_watch),
    PRIVATE("private", R.string.private_visibility, R.string.only_you_can_watch);
}