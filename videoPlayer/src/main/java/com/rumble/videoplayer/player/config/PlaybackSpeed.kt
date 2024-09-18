package com.rumble.videoplayer.player.config

enum class PlaybackSpeed(
    val value: Float,
    val stepValue: Float,
    val stepLabel: String,
    val title: String,
    var selected: Boolean
) {
    SPEED_0_25(0.25f, 0f, ".25x", ".25x", false),
    SPEED_0_5(0.5f, 1f, "", "0.5x", false),
    SPEED_0_75(0.75f, 2f, "", "0.75x", false),
    SPEED_0_9(0.9f, 3f, "", "0.9x", false),
    NORMAL(1f, 4f, "1x", "1x", true),
    SPEED_1_25(1.25f, 5f, "", "1.25x", false),
    SPEED_1_5(1.5f, 6f, "", "1.5x", false),
    SPEED_1_75(1.75f, 7f, "", "1.75x", false),
    SPEED_2(2f, 8f, "2x", "2x", false);

    companion object {
        fun getByStepValue(value: Float) = values().find { it.stepValue == value } ?: NORMAL
    }
}