package com.rangr.util

import java.math.BigDecimal
import java.math.RoundingMode

class Utils {
    companion object {
        fun RoundNumberToDp(number: Double, dp: Int): Double {
            return BigDecimal(number).setScale(dp, RoundingMode.HALF_EVEN).toDouble()
        }
    }
}