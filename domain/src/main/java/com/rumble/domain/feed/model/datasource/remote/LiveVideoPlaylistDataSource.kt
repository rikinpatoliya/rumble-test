package com.rumble.domain.feed.model.datasource.remote

import com.rumble.network.api.VideoApi
import okhttp3.ResponseBody
import retrofit2.Response

interface LiveVideoPlaylistDataSource {
    suspend fun fetchLiveVideoPlayList(url: String): Response<ResponseBody>
}

class LiveVideoPlaylistDataSourceImpl(
    private val videoApi: VideoApi
) : LiveVideoPlaylistDataSource {
    override suspend fun fetchLiveVideoPlayList(url: String): Response<ResponseBody> =
        videoApi.fetchPlaylist(url)
}