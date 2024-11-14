package com.rumble.domain.discover.domain.domainmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryEntity(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val viewersNumber: Long,
    val description: String?,
    val path: String,
    val isPrimary: Boolean
): Parcelable