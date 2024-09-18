package com.rumble.network.queryHelpers

enum class PlayListInclude(val value: String) {
    Public("public"),
    Nonpublic("nonpublic"),
    Followed("followed"),
    All("${Public},${Nonpublic},${Followed}");

    override fun toString(): String = this.value
}