package com.rumble.network.queryHelpers

enum class EnablePush(val value: Int) {
    DISABLE(0),
    ENABLE(1);

    override fun toString(): String = this.value.toString()

    companion object {
        fun getByValue(value: Boolean): EnablePush =
            when (value) {
                false -> DISABLE
                true -> ENABLE
            }
    }
}