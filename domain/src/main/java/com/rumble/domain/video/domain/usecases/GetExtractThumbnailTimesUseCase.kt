package com.rumble.domain.video.domain.usecases

import javax.inject.Inject

class GetExtractThumbnailTimesUseCase @Inject constructor() {

    operator fun invoke(duration: Long, quantity: Int): List<Long> {
        val list = mutableListOf<Long>()
        val step = duration / (quantity.minus(1))
        (1..quantity).mapIndexed { index, _ ->
            val time =
                getExtractThumbnailTime(
                    index,
                    quantity,
                    duration,
                    step
                )
            list.add(index, time)
        }
        return list
    }

    private fun getExtractThumbnailTime(
        index: Int,
        quantity: Int,
        duration: Long,
        step: Long
    ): Long {
        val time = when (index) {
            0 -> 0L
            quantity -> duration.times(1000)
            else -> step.times(index).times(1000)
        }
        return time
    }
}