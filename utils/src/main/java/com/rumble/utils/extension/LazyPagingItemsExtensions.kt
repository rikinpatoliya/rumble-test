package com.rumble.utils.extension

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems

/**
 * This is a workaround for a bug in compose and paging where:
 * LazyColumn{
 * item{}
 * items(LazyPagingItems<T>){}
 * }
 * Causes loss of scroll position on navigation.
 * The actual issue this fixes:
 * https://issuetracker.google.com/issues/179397301
 *
 * The workaround mentioned for a similar issue that hs been fixed:
 * https://issuetracker.google.com/issues/177245496#comment24
 */
@Composable
fun <T : Any> LazyPagingItems<T>.rememberLazyListState(): LazyListState {
    // After recreation, LazyPagingItems first return 0 items, then the cached items.
    // This behavior/issue is resetting the LazyListState scroll position.
    return when (itemCount) {
        // Return a different LazyListState instance.
        0 -> remember(this) { LazyListState(0, 0) }
        // Return rememberLazyListState (normal case).
        else -> androidx.compose.foundation.lazy.rememberLazyListState()
    }
}