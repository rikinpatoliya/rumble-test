package com.rumble.ui3.search

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debouncer<T>(
    private val delayMillis: Long,
    private val coroutineScope: CoroutineScope,
    private val errorHandler: CoroutineExceptionHandler,
) {
    private var lastExecutionTime = 0L
    private var debounceJob: Job? = null

    fun launch(input: T, action: suspend (T) -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastExecutionTime > delayMillis || lastExecutionTime == 0L) {
            // Execute immediately if enough time has passed or it's the first execution
            lastExecutionTime = currentTime
            coroutineScope.launch(errorHandler) {
                action(input)
            }
        } else {
            // Delay further executions until the debounce time has passed
            debounceJob?.cancel()
            debounceJob = coroutineScope.launch(errorHandler) {
                delay(delayMillis - (currentTime - lastExecutionTime))
                lastExecutionTime = System.currentTimeMillis()
                action(input)
            }
        }
    }
}