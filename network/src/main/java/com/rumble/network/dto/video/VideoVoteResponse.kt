package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class VideoVoteResponse(
    @SerializedName("data")
    val data: VideoVoteResponseData
)

data class VideoVoteResponseData(
    @SerializedName("video_id")
    val videoId: Long,
    @SerializedName("num_votes")
    val numberVotes: Long,
    @SerializedName("num_votes_up")
    val numVotesUp: Long,
    @SerializedName("num_votes_down")
    val numVotesDown: Long
)