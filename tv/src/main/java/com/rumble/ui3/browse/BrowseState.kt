package com.rumble.ui3.browse


object BrowseState {
    var lastFocusView = LastFocusView.CategoryView
}

enum class LastFocusView{
    CategoryView,
    FilterView,
    DataView
}