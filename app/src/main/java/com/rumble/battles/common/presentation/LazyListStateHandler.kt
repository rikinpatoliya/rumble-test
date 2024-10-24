package com.rumble.battles.common.presentation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State

interface LazyListStateHandler {

    val listState: State<LazyListState>
}