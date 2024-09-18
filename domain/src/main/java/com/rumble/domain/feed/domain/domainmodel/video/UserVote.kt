package com.rumble.domain.feed.domain.domainmodel.video

enum class UserVote(val value: Int) {
    NONE(0),
    LIKE(1),
    DISLIKE(-1);

    companion object {
        fun getByVote(vote: Int): UserVote =
            when (vote) {
                1 -> LIKE
                -1 -> DISLIKE
                else -> NONE
            }
    }
}