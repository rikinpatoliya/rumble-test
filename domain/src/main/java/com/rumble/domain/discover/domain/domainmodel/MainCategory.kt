package com.rumble.domain.discover.domain.domainmodel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.rumble.domain.R

enum class MainCategory(
    @StringRes val label: Int,
    @DrawableRes val image: Int,
    val borderColor: Color,
    val path: String
) {
    MUSIC(R.string.music, R.drawable.ic_category_music, Color(0xFF67EAFF), "music"),
    NEWS(R.string.news, R.drawable.ic_category_news, Color(0xFF7DD4FF), "news"),
    GAMING(R.string.gaming, R.drawable.ic_category_gaming, Color(0xFF9D91FF), "gaming"),
    VIRAL(R.string.viral, R.drawable.ic_category_viral, Color(0xFF82B1FF), "viral")
}