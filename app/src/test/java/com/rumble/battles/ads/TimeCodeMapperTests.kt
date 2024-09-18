package com.rumble.battles.ads

import com.rumble.videoplayer.domain.model.VideoAdTimeCode
import com.rumble.domain.rumbleads.model.mapping.TimeCodeMapper
import org.junit.Test

class TimeCodeMapperTests {

    @Test
    fun testParseTimeCode() {
        var result = TimeCodeMapper.parseTimeCode("0")
        assert(result is VideoAdTimeCode.PreRoll)

        result = TimeCodeMapper.parseTimeCode("-1")
        assert(result is VideoAdTimeCode.PostRoll)

        result = TimeCodeMapper.parseTimeCode("-10")
        assert(result is VideoAdTimeCode.SecondsFromEnd)

        result = TimeCodeMapper.parseTimeCode("10")
        assert(result is VideoAdTimeCode.SecondsFromStart)

        result = TimeCodeMapper.parseTimeCode("10%")
        assert(result is VideoAdTimeCode.Percentage)
    }
}