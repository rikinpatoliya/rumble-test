package com.rumble.videoplayer.player.config

import com.rumble.videoplayer.R

enum class ReportType(val value: Int) {
    SPAM(R.string.it_is_spam),
    INAPPROPRIATE(R.string.it_is_inappropriate),
    TERMS(R.string.it_violates_terms),
    COPYRIGHT(R.string.it_violates_copyright);
}