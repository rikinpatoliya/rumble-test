package com.rumble.ui3.home

import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.RowPresenter

class HomeListRowPresenter(var lastSelectedItemPosition: Int, zoomFactor: Int, useFocusDimmer: Boolean) :
    ListRowPresenter(zoomFactor, useFocusDimmer) {

    init {
        shadowEnabled = false
        headerPresenter = HomeCustomRowHeaderPresenter()
    }

    override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder, item: Any) {
        super.onBindRowViewHolder(holder, item)

        val vh = holder as ViewHolder
        if (lastSelectedItemPosition != 0) {
            vh.gridView.selectedPosition = lastSelectedItemPosition
            lastSelectedItemPosition = 0
        }
    }

    override fun isUsingDefaultListSelectEffect() = false
}