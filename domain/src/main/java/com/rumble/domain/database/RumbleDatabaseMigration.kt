package com.rumble.domain.database

import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.migration.AutoMigrationSpec


class RumbleDatabaseMigration {
    @DeleteTable("HomeCategoryViewTime")
    class MigrationSpec7_8 : AutoMigrationSpec

    @DeleteTable("HomeCategoryView")
    class MigrationSpec8_9 : AutoMigrationSpec

    @DeleteColumn(tableName = "videos", columnName = "thumbnail")
    class MigrationSpec11_12 : AutoMigrationSpec

    @DeleteColumn(tableName = "watch_progress", columnName = "playback_volume")
    class MigrationSpec16_17 : AutoMigrationSpec
}