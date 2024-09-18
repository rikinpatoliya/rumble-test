package com.rumble.domain.feed.model.datasource.local

import androidx.room.*

const val limitRecentViews = 100

@Dao
interface HomeCategoryViewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(roomVideoCollectionView: RoomVideoCollectionView)

    @Query(
        "SELECT name, COUNT(*) as count " +
                "FROM (SELECT * " +
                "FROM VideoCollectionView " +
                "WHERE userId = :userId " +
                "ORDER BY viewTimestamp DESC LIMIT $limitRecentViews) " +
                "GROUP BY name"
    )
    suspend fun getRecentViewCounts(userId: String): List<RoomVideoCollectionViewCount>

    @Query(
        "DELETE FROM VideoCollectionView " +
                "WHERE userId = :userId and id NOT IN " +
                "(SELECT id FROM VideoCollectionView " +
                "ORDER BY viewTimestamp DESC LIMIT $limitRecentViews)"
    )
    suspend fun deleteOlderViews(userId: String)

}