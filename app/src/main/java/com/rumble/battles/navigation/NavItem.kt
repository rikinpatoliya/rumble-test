package com.rumble.battles.navigation

import com.rumble.battles.CameraTag
import com.rumble.battles.DiscoverTag
import com.rumble.battles.FeedTag
import com.rumble.battles.LibraryTag
import com.rumble.battles.ProfileTag
import com.rumble.battles.R
import com.rumble.battles.TabBarTag
import com.rumble.battles.TabTag

const val NAV_ITEM_INDEX_HOME = 0
const val NAV_ITEM_INDEX_DISCOVER = 1
const val NAV_ITEM_INDEX_CAMERA = 2
const val NAV_ITEM_INDEX_LIBRARY = 3
const val NAV_ITEM_INDEX_ACCOUNT = 4

data class NavItem(
    val iconId: Int,
    val iconIdSelected: Int,
    val titleId: Int,
    val root: RumbleScreens,
    val testTag: String
)

object NavItems {
    val items = listOf(
        NavItem(
            R.drawable.ic_nav_home,
            R.drawable.ic_nav_home_filled,
            R.string.nav_home,
            RumbleScreens.Feeds,
            "${TabBarTag}_$TabTag$FeedTag"
        ),
        NavItem(
            R.drawable.ic_nav_compass,
            R.drawable.ic_nav_compass_filled,
            R.string.nav_discover,
            RumbleScreens.Discover,
            "${TabBarTag}_$TabTag$DiscoverTag"
        ),
        NavItem(
            R.drawable.ic_nav_camera,
            R.drawable.ic_nav_camera_filled,
            R.string.nav_create,
            RumbleScreens.CameraGalleryScreen,
            "${TabBarTag}_$TabTag$CameraTag"
        ),
        NavItem(
            R.drawable.ic_nav_library,
            R.drawable.ic_nav_library_filled,
            R.string.nav_library,
            RumbleScreens.Library,
            "${TabBarTag}_$TabTag$LibraryTag"
        ),
        NavItem(
            R.drawable.ic_nav_account,
            R.drawable.ic_nav_account_filled,
            R.string.nav_account,
            RumbleScreens.Profile,
            "${TabBarTag}_$TabTag$ProfileTag"
        )
    )
}