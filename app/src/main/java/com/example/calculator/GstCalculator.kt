package com.example.calculator

/**
 * Data holder for GST tax breakdown calculations.
 */
data class GstCalculationResult(
    val enteredAmount: Double,
    val baseAmount: Double,
    val taxRate: Double,
    val cgstAmount: Double,
    val sgstAmount: Double,
    val gstAmount: Double,
    val itemTotal: Double,
    val isDeduction: Boolean
)

/**
 * Standard GST tax calculator supporting forward GST (+GST) and reverse / inclusive GST (-GST / Tax-).
 */
object GstCalculator {

    /**
     * Calculates CGST, SGST, total GST, and resulting item total.
     *
     * @param amount The evaluated input amount. For +GST, this is the net base amount before tax.
     *               For -GST, this is the gross/inclusive amount containing tax to be removed.
     * @param taxRate The total GST percentage (e.g. 5.0, 18.0, 40.0).
     * @param isDeduction True for -GST (Reverse GST / tax deduction), False for +GST (tax addition).
     */
    fun calculate(amount: Double, taxRate: Double, isDeduction: Boolean): GstCalculationResult {
        val baseAmount: Double
        val cgstAmount: Double
        val sgstAmount: Double
        val gstTotalAmount: Double
        val itemTotal: Double

        if (!isDeduction) {
            // Forward GST (+GST):
            // Base = entered amount
            // GST = Base * (Rate / 100)
            // Item Total = Base + GST
            baseAmount = amount
            cgstAmount = (baseAmount * (taxRate / 2.0)) / 100.0
            sgstAmount = (baseAmount * (taxRate / 2.0)) / 100.0
            gstTotalAmount = cgstAmount + sgstAmount
            itemTotal = baseAmount + gstTotalAmount
        } else {
            // Reverse / Backward GST (-GST / Tax-):
            // Amount is GST-inclusive.
            // Base (Net amount without tax) = Amount / (1 + Rate / 100)
            // GST = Amount - Base
            // Item Total = Base (the net amount resulting from removing GST)
            baseAmount = amount / (1.0 + (taxRate / 100.0))
            gstTotalAmount = amount - baseAmount
            cgstAmount = gstTotalAmount / 2.0
            sgstAmount = gstTotalAmount / 2.0
            itemTotal = baseAmount
        }

        return GstCalculationResult(
            enteredAmount = amount,
            baseAmount = baseAmount,
            taxRate = taxRate,
            cgstAmount = cgstAmount,
            sgstAmount = sgstAmount,
            gstAmount = gstTotalAmount,
            itemTotal = itemTotal,
            isDeduction = isDeduction
        )
    }
}
