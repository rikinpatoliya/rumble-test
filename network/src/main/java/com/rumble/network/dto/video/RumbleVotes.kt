package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class RumbleVotes(
    @SerializedName("score")
    val score: Int,
    @SerializedName("votes")
    val votes: Int,
    @SerializedName("content_id")
    val contentId: Int,
    @SerializedName("user_vote")
    val userVote: Int,
    @SerializedName("num_votes_up")
    val numVotesUp: Int,
    @SerializedName("num_votes_down")
    val numVotesDown: Int
)
