package com.rumble.domain.search.model.datasource.local

import androidx.room.*

@Dao
interface QueryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuery(query: RoomQuery)

    @Update
    suspend fun updateQuery(query: RoomQuery)
    @Delete
    suspend fun deleteQuery(query: RoomQuery)
    @Query("DELETE FROM queries")
    suspend fun deleteAllQueries()

    @Query("SELECT * FROM queries ORDER BY time DESC")
    suspend fun getAll(): List<RoomQuery>

    @Query("SELECT * FROM queries WHERE `query` LIKE :filter")
    suspend fun filter(filter: String): List<RoomQuery>

    @Query("SELECT * FROM queries WHERE `query` = :query")
    suspend fun getQuery(query: String): RoomQuery?
}