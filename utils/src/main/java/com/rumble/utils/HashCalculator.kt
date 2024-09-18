package com.rumble.utils

import com.rumble.utils.extension.md5

object HashCalculator {

    fun calculateHashStretched(ds: List<String>, pt: String): String {
        var hashStretched = ""
        if (ds.size > 2) {
            hashStretched = (calculateMD5Stretch(pt, ds[0]) + ds[1]).md5() + "," +
                calculateMD5Stretch(pt, ds[2]) + "," + ds[1]
        }
        return hashStretched
    }

    private fun calculateMD5Stretch(input: String, salt: String): String {
        var hash: String = (salt + input).md5()
        for (i in 0 until 128) {
            hash = (hash + input).md5()
        }
        return hash
    }
}