package com.example.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatter {

    fun formatNumber(value: Double, system: NumberSystem, showDecimalsIfInt: Boolean = false): String {
        if (value.isNaN() || value.isInfinite()) return "0"

        val isNegative = value < 0
        val absValue = kotlin.math.abs(value)

        val isWhole = (absValue % 1.0 == 0.0) && !showDecimalsIfInt

        val formatted = if (system == NumberSystem.INDIAN) {
            formatIndian(absValue, isWhole)
        } else {
            formatInternational(absValue, isWhole)
        }

        return if (isNegative) "-$formatted" else formatted
    }

    private fun formatInternational(value: Double, isWhole: Boolean): String {
        val pattern = if (isWhole) "#,##0" else "#,##0.##"
        val df = DecimalFormat(pattern, DecimalFormatSymbols(Locale.US))
        return df.format(value)
    }

    private fun formatIndian(value: Double, isWhole: Boolean): String {
        val longVal = value.toLong()
        val decimalPart = value - longVal

        val strVal = longVal.toString()
        val formattedInt = if (strVal.length <= 3) {
            strVal
        } else {
            val lastThree = strVal.substring(strVal.length - 3)
            val rest = strVal.substring(0, strVal.length - 3)
            val sb = StringBuilder()
            var count = 0
            for (i in rest.length - 1 downTo 0) {
                sb.append(rest[i])
                count++
                if (count == 2 && i != 0) {
                    sb.append(',')
                    count = 0
                }
            }
            sb.reverse().toString() + "," + lastThree
        }

        return if (isWhole || kotlin.math.abs(decimalPart) < 0.0001) {
            formattedInt
        } else {
            val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
            val decFormatted = df.format(decimalPart)
            // decFormatted might be "0.5" -> we want ".5"
            val decStr = if (decFormatted.startsWith("0.")) {
                decFormatted.substring(1)
            } else if (decFormatted.startsWith(".")) {
                decFormatted
            } else {
                ".$decFormatted"
            }
            formattedInt + decStr
        }
    }
}
