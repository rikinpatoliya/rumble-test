package com.rumble.utils

import androidx.annotation.ColorRes
import com.rumble.utils.extension.md5
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RumbleUIUtil @Inject constructor() {

    /***/
    private val placeholderColors =
        listOf(
            R.color.fierce_red,
            R.color.rumble_green,
            R.color.royal_purple,
            R.color.tree_poppy,
            R.color.placeholderColor1,
            R.color.placeholderColor2,
            R.color.placeholderColor3,
            R.color.placeholderColor4,
            R.color.placeholderColor5,
            R.color.placeholderColor6,
        )

    // For the User object, the username attribute should be used to calculate the number.
    // For the Channel object, it should be title.
    @ColorRes
    fun getPlaceholderColorResId(name: String): Int {
        val md5Substring = name.md5().take(15)
        val number = md5Substring.toBigInteger(16)
        val index = number % placeholderColors.size.toBigInteger()
        return placeholderColors[index.toInt()]
    }

}