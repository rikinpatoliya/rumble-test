package com.rumble.battles.common.presentation

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.State

interface LazyGridStateHandler {

    val gridState: State<LazyGridState>

    fun updateGridState(newState: LazyGridState)

}