package com.rumble.battles.navigation

import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.utils.extension.navigationSafeEncode

sealed class RumbleScreens(val rootName: String) {
    object Feeds : RumbleScreens("feeds")
    object Discover : RumbleScreens("discover")
    object Videos : RumbleScreens("videos")
    object Library : RumbleScreens("library")
    object PlayListsScreen : RumbleScreens("playlists")
    object PlayListScreen : RumbleScreens("playlist/{${RumblePath.PLAYLIST.path}}") {
        fun getPath(playListId: String): String = "playlist/$playListId"
    }

    object Profile : RumbleScreens("profile")
    object EditProfile : RumbleScreens("editProfile")
    object Subscriptions : RumbleScreens("subscriptions")
    object Referrals : RumbleScreens("referrals")
    object Settings : RumbleScreens("settings/{${RumblePath.PARAMETER.path}}") {
        fun getPath(scrollToPlayback: Boolean = false): String = "settings/$scrollToPlayback"
    }
    object Credits : RumbleScreens("credits")
    object ChangeEmail : RumbleScreens("changeEmail")
    object ChangePassword : RumbleScreens("changePassword")
    object ChangeSubdomain : RumbleScreens("changeSubdomain")
    object DebugAdSettings : RumbleScreens("debugAdSettings")
    object CloseAccount : RumbleScreens("closeAccount")
    object UploadQuality : RumbleScreens("uploadQuality")
    object RecommendedChannelsScreen : RumbleScreens("recommendedChannelsScreen")
    object TopChannelsScreen : RumbleScreens("topChannelsScreen")
    object Channel : RumbleScreens("channel/{${RumblePath.CHANNEL.path}}") {
        fun getPath(channelId: String): String = "channel/$channelId"
    }

    object Search :
        RumbleScreens(
            "search?" +
                    "${RumblePath.QUERY.path}={${RumblePath.QUERY.path}}" +
                    "&${RumblePath.NAVIGATION.path}={${RumblePath.NAVIGATION.path}}" +
                    "&${RumblePath.PARAMETER.path}={${RumblePath.PARAMETER.path}}"
        ) {
        fun getPath(query: String = "", navDest: String = "", parent: String = Feeds.rootName): String =
            "search?" +
                    "${RumblePath.QUERY.path}=${query.navigationSafeEncode()}" +
                    "&${RumblePath.NAVIGATION.path}=${navDest.navigationSafeEncode()}" +
                    "&${RumblePath.PARAMETER.path}=$parent"
    }

    object CombinedSearchResult : RumbleScreens("combinedSearchResult/{${RumblePath.QUERY.path}}") {
        fun getPath(query: String): String = "combinedSearchResult/${query.navigationSafeEncode()}"
    }

    object ChannelSearchScreen : RumbleScreens(
        "channelSearch?" +
                "${RumblePath.QUERY.path}={${RumblePath.QUERY.path}}"
    ) {
        fun getPath(query: String): String =
            "channelSearch?${RumblePath.QUERY.path}=${query.navigationSafeEncode()}"
    }

    object VideoSearchScreen : RumbleScreens(
        "videoSearch?" +
                "${RumblePath.QUERY.path}={${RumblePath.QUERY.path}}" +
                "&${RumblePath.SORT.path}={${RumblePath.SORT.path}}" +
                "&${RumblePath.UPLOAD_DATE.path}={${RumblePath.UPLOAD_DATE.path}}" +
                "&${RumblePath.DURATION.path}={${RumblePath.DURATION.path}}"
    ) {
        fun getPath(query: String, sort: String, uploadDate: String, duration: String): String =
            "videoSearch?" +
                    "${RumblePath.QUERY.path}=${query.navigationSafeEncode()}" +
                    "&${RumblePath.SORT.path}=${sort}" +
                    "&${RumblePath.UPLOAD_DATE.path}=${uploadDate}" +
                    "&${RumblePath.DURATION.path}=${duration}"
    }

    object VideoListScreen : RumbleScreens("videoList/{${RumblePath.VIDEO_CATEGORY.path}}") {
        fun getPath(category: String): String = "videoList/$category"
    }

    object DiscoverPlayer :
        RumbleScreens("discoverPlayer/${RumblePath.VIDEO_CATEGORY.path}={${RumblePath.VIDEO_CATEGORY.path}}&${RumblePath.CHANNEL.path}={${RumblePath.CHANNEL.path}}") {
        fun getPath(category: String = "", channelId: String = "0"): String =
            "discoverPlayer/${RumblePath.VIDEO_CATEGORY.path}=${category}&${RumblePath.CHANNEL.path}=$channelId"
    }
    object EarningsScreen : RumbleScreens("earningsScreen")
    object ProfileNotifications : RumbleScreens("profileNotifications")
    object CameraGalleryScreen: RumbleScreens("cameraGallery")
    object CameraMode: RumbleScreens("CameraMode")
    object VideoUploadPreview : RumbleScreens("videoPreview/{${RumblePath.VIDEO_URL.path}}") {
        fun getPath(videoUrl: String): String = "videoPreview/${videoUrl.navigationSafeEncode()}"
    }
    object CameraUploadStepOne: RumbleScreens("cameraUploadStepOne")
    object CameraUploadStepTwo: RumbleScreens("cameraUploadStepTwo")
    object UploadChannelSelection: RumbleScreens("uploadChannelSelection")
    object UploadLicenseSelection: RumbleScreens("uploadLicenseSelection")
    object UploadVisibilitySelection: RumbleScreens("UploadVisibilitySelection")
    object UploadScheduleSelection: RumbleScreens("UploadScheduleSelection")
    object UploadCategorySelection: RumbleScreens("UploadCategorySelection/{${RumblePath.IS_PRIMARY_CATEGORY.path}}") {
        fun getPath(isPrimaryCategory: Boolean): String = "UploadCategorySelection/$isPrimaryCategory"
    }
    object CategoryScreen: RumbleScreens("browseCategory/{${RumblePath.VIDEO_CATEGORY.path}}/{${RumblePath.PARAMETER.path}}") {
        fun getPath(path: String, showLiveCategoryList: Boolean): String = "browseCategory/$path/$showLiveCategoryList"
    }
    object BrowseAllCategories: RumbleScreens("browseAllCategories/{${RumblePath.TYPE.path}}") {
        fun getPath(displayType: CategoryDisplayType): String = "browseAllCategories/${displayType.name}"
    }
    object NotificationSettings: RumbleScreens("notificationSettings")
}

enum class RumblePath(val path: String) {
    URL("url"),
    CHANNEL("channelId"),
    QUERY("query"),
    SORT("sort"),
    UPLOAD_DATE("uploadDate"),
    DURATION("duration"),
    NAVIGATION("navDest"),
    VIDEO_CATEGORY("videoCategory"),
    VIDEO("videoId"),
    ORIENTATION("orientation"),
    PARAMETER("parameter"),
    VIDEO_URL("videoUrl"),
    TYPE("type"),
    PLAYLIST("playlist"),
    PLAYLIST_SHUFFLE("playlistShuffle"),
    IS_PRIMARY_CATEGORY("isPrimaryCategory"),
}
