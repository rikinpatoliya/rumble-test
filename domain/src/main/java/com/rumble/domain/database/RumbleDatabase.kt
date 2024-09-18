package com.rumble.domain.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.rumble.domain.camera.model.datasource.local.RoomVideo
import com.rumble.domain.camera.model.datasource.local.VideoDao
import com.rumble.domain.channels.channeldetails.domain.domainmodel.RoomChannelFollow
import com.rumble.domain.channels.model.datasource.local.ChannelFollowDao
import com.rumble.domain.feed.model.datasource.local.ChannelViewDao
import com.rumble.domain.feed.model.datasource.local.HomeCategoryViewDao
import com.rumble.domain.feed.model.datasource.local.RoomChannelView
import com.rumble.domain.feed.model.datasource.local.RoomVideoCollectionView
import com.rumble.domain.onboarding.domain.domainmodel.RoomOnboardingView
import com.rumble.domain.onboarding.model.datasource.local.OnboardingViewDao
import com.rumble.domain.search.model.datasource.local.QueryDao
import com.rumble.domain.search.model.datasource.local.RoomQuery
import com.rumble.domain.timerange.model.datasource.local.RoomTimeRange
import com.rumble.domain.timerange.model.datasource.local.RoomWatchProgress
import com.rumble.domain.timerange.model.datasource.local.TimeRangeDao
import com.rumble.domain.timerange.model.datasource.local.WatchProgressDao
import com.rumble.domain.video.model.datasource.local.LastPositionDao
import com.rumble.domain.video.model.datasource.local.RoomLastPosition


@Database(
    version = 17,
    entities = [
        RoomQuery::class,
        RoomChannelView::class,
        RoomOnboardingView::class,
        RoomLastPosition::class,
        RoomVideoCollectionView::class,
        RoomVideo::class,
        RoomChannelFollow::class,
        RoomTimeRange::class,
        RoomWatchProgress::class
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(
            from = 7, to = 8,
            spec = RumbleDatabaseMigration.MigrationSpec7_8::class
        ),
        AutoMigration(
            from = 8, to = 9,
            spec = RumbleDatabaseMigration.MigrationSpec8_9::class
        ),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(
            from = 11, to = 12,
            spec = RumbleDatabaseMigration.MigrationSpec11_12::class
        ),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14),
        AutoMigration(from = 14, to = 15),
        AutoMigration(from = 15, to = 16),
        AutoMigration(
            from = 16, to = 17,
            spec = RumbleDatabaseMigration.MigrationSpec16_17::class
        ),
    ]
)
abstract class RumbleDatabase : RoomDatabase() {

    abstract fun queryDao(): QueryDao
    abstract fun channelViewDao(): ChannelViewDao
    abstract fun onboardingViewDao(): OnboardingViewDao
    abstract fun homeCategoryViewTimeDao(): HomeCategoryViewDao
    abstract fun lastPositionDao(): LastPositionDao
    abstract fun videoDao(): VideoDao
    abstract fun channelFollowDao(): ChannelFollowDao
    abstract fun timeRangeDao(): TimeRangeDao
    abstract fun watchProgressDao(): WatchProgressDao
}