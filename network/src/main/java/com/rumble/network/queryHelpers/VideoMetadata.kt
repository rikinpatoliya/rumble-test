package com.rumble.network.queryHelpers

enum class VideoMetadata(val key: String, val commonValue: String = "") {
    AppName("appname", "Rumble"),
    AppBundleId("bundleid"),
    AppStoreUrl("appstoreurl"),
    Domain("domain", "rumble.com"),
    PublisherId("pubid"),
    ResettableId("rdid"),
    Uuid("uid"),
    DoNotTrack("dnt"),
    Session("session"),
    Width("w"),
    Height("h"),
    VideoId("extid"),
    GDPR("isgdpr", "0"),
    Category("vertical"),
    Rating("rating"),
    ChannelId("channel_id"),
    Testing("testing", "FALSE")
}