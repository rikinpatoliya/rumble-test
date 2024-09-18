package com.rumble.ui3.common

import android.content.Context
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.VerticalGridPresenter
import com.rumble.R
import com.rumble.util.Constant


class RumbleVerticalGridPresenter(
    val context: Context,
    focusZoomFactor: Int,
    useFocusDimmer: Boolean,
    private val windowAlignment: Float = BaseGridView.WINDOW_ALIGN_OFFSET_PERCENT_DISABLED,
    var windowAlignmentOffsetPercent: Float = Constant.CHANNEL_DETAILS_OFFSET_PERCENT,
) : VerticalGridPresenter(focusZoomFactor, useFocusDimmer) {

    override fun initializeGridViewHolder(vh: ViewHolder) {
        super.initializeGridViewHolder(vh)
        val gridView = vh.gridView
        gridView.horizontalSpacing = context.resources.getDimensionPixelSize(R.dimen.rumble_vertical_grid_horizontal_spacing)
        gridView.windowAlignment = windowAlignment.toInt()
        gridView.windowAlignmentOffset = 0
        gridView.windowAlignmentOffsetPercent = windowAlignmentOffsetPercent
    }
}