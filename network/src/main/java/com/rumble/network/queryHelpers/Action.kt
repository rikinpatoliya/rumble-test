package com.rumble.network.queryHelpers

enum class Action(val value: String) {
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe"),
    BLOCK("block"),
    UNBLOCK("unblock");

    override fun toString(): String = this.value

    companion object {
        fun getByValue(value: String): Action =
            when (value) {
                "subscribe" -> SUBSCRIBE
                "unsubscribe" -> UNSUBSCRIBE
                "block" -> BLOCK
                "unblock" -> UNBLOCK
                else -> throw Error("Unsupported type!")
            }
    }
}