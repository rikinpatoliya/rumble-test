package com.rumble.ui3.search

import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRowPresenter

class SearchListRowPresenter : ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false) {

    init {
        shadowEnabled = false
    }

    override fun isUsingDefaultListSelectEffect() = false
}