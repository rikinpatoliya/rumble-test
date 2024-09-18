package com.rumble.utils.extension

import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal

class BigDecimalExtensionTest {
    @Test
    fun testToCurrencyString() {
        var bigDecimal = BigDecimal(0)
        Assert.assertEquals(bigDecimal.toCurrencyString("$"), "$0.00")

        bigDecimal = BigDecimal(0.1)
        Assert.assertEquals(bigDecimal.toCurrencyString("$"), "$0.10")

        bigDecimal = BigDecimal(1234.56)
        Assert.assertEquals(bigDecimal.toCurrencyString("$"), "$1,234.56")

        bigDecimal = BigDecimal(-1234.56)
        Assert.assertEquals(bigDecimal.toCurrencyString("$"), "$-1,234.56")

        bigDecimal = BigDecimal(50)
        Assert.assertEquals(bigDecimal.toCurrencyString("¥"), "¥50.00")
    }
}