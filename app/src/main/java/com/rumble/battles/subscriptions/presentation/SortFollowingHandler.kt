package com.rumble.battles.subscriptions.presentation

import com.rumble.domain.sort.SortFollowingType

interface SortFollowingHandler {
    fun onSortFollowingSelected(sortFollowingType: SortFollowingType)
}