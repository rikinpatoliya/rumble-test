package com.rumble.ui3.search

object SearchItemsPosition {
    var selectedRowPosition: Int = -1
    var lastClickedChannelRowPosition: Int = -1
    var selectedChannelRowPosition: Int = -1
    var lastClickedChannelItemPosition: Int = -1
    var selectedVideoItemPosition: Int = -1
    var selectedChannelItemPosition: Int = -1
    var combinedSelectedItemPosition: Int = -1
    var hasCreatedRows = false
    var refreshChannelDetailInRowItem = false
    var lastFocusView = LastFocusView.SearchView
}
enum class LastFocusView{
    SearchView,
    DataView
}
