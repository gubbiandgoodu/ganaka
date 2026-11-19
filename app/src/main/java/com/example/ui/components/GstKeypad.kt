package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GanakaTextMuted
import com.example.ui.theme.GanakaTheme

@Composable
fun GstKeypad(
    taxRates: List<Double>,
    onDigitClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onDecimalClick: () -> Unit,
    onResetPageClick: () -> Unit,
    onClearLineClick: () -> Unit,
    onDeleteLastClick: () -> Unit,
    onEnterClick: () -> Unit,
    onTaxSlabClick: (rate: Double, isDeduction: Boolean) -> Unit,
    onUndoClick: () -> Unit,
    onGanakaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonHeight = 52.dp
    val spacing = 7.dp
    val colors = GanakaTheme.colors

    val r1 = if (taxRates.isNotEmpty()) taxRates[0] else 5.0
    val r2 = if (taxRates.size > 1) taxRates[1] else 18.0
    val r3 = if (taxRates.size > 2) taxRates[2] else 40.0

    val r1Str = if (r1 % 1.0 == 0.0) "${r1.toInt()}%" else "$r1%"
    val r2Str = if (r2 % 1.0 == 0.0) "${r2.toInt()}%" else "$r2%"
    val r3Str = if (r3 % 1.0 == 0.0) "${r3.toInt()}%" else "$r3%"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        // Row 1: RESET, CLEAR, DELETE, ENTER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            TextKeypadButton(
                text = "RESET",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyResetBg,
                contentColor = colors.keyResetText,
                borderColor = colors.keyResetBorder,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                testTag = "gst_key_reset",
                onClick = onResetPageClick
            )
            TextKeypadButton(
                text = "CLEAR",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyClearBg,
                contentColor = colors.keyClearText,
                borderColor = colors.keyClearBorder,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                testTag = "gst_key_clear_line",
                onClick = onClearLineClick
            )
            TextKeypadButton(
                text = "DELETE",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyDeleteBg,
                contentColor = colors.keyDeleteText,
                borderColor = colors.keyDeleteBorder,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                testTag = "gst_key_delete_last",
                onClick = onDeleteLastClick
            )
            TextKeypadButton(
                text = "ENTER",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyEnterBg,
                contentColor = colors.keyEnterText,
                borderColor = colors.keyEnterBorder,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                testTag = "gst_key_enter",
                onClick = onEnterClick
            )
        }

        Spacer(modifier = Modifier.height(spacing))

        // Row 2: Tax Slabs Section (height 52.dp = 24.dp + 4.dp + 24.dp)
        val halfHeight = 24.dp
        val halfSpacing = 4.dp

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            // Col 1: +r1, -r1
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(halfSpacing)
            ) {
                TextKeypadButton(
                    text = "+$r1Str",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(halfHeight),
                    backgroundColor = colors.keyGstPlusBg,
                    contentColor = colors.keyGstPlusText,
                    borderColor = colors.keyGstPlusBorder,
                    fontSize = 12.sp,
                    testTag = "key_tax_plus_1",
                    onClick = { onTaxSlabClick(r1, false) }
                )
                TextKeypadButton(
                    text = "-$r1Str",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(halfHeight),
                    backgroundColor = colors.keyGstMinusBg,
                    contentColor = colors.keyGstMinusText,
                    borderColor = colors.keyGstMinusBorder,
                    fontSize = 12.sp,
                    testTag = "key_tax_minus_1",
                    onClick = { onTaxSlabClick(r1, true) }
                )
            }

            // Col 2: +r2, -r2
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(halfSpacing)
            ) {
                TextKeypadButton(
                    text = "+$r2Str",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(halfHeight),
                    backgroundColor = colors.keyGstPlusBg,
                    contentColor = colors.keyGstPlusText,
                    borderColor = colors.keyGstPlusBorder,
                    fontSize = 12.sp,
                    testTag = "key_tax_plus_2",
                    onClick = { onTaxSlabClick(r2, false) }
                )
                TextKeypadButton(
                    text = "-$r2Str",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(halfHeight),
                    backgroundColor = colors.keyGstMinusBg,
                    contentColor = colors.keyGstMinusText,
                    borderColor = colors.keyGstMinusBorder,
                    fontSize = 12.sp,
                    testTag = "key_tax_minus_2",
                    onClick = { onTaxSlabClick(r2, true) }
                )
            }

            // Col 3: +r3, -r3
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(halfSpacing)
            ) {
                TextKeypadButton(
                    text = "+$r3Str",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(halfHeight),
                    backgroundColor = colors.keyGstPlusBg,
                    contentColor = colors.keyGstPlusText,
                    borderColor = colors.keyGstPlusBorder,
                    fontSize = 12.sp,
                    testTag = "key_tax_plus_3",
                    onClick = { onTaxSlabClick(r3, false) }
                )
                TextKeypadButton(
                    text = "-$r3Str",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(halfHeight),
                    backgroundColor = colors.keyGstMinusBg,
                    contentColor = colors.keyGstMinusText,
                    borderColor = colors.keyGstMinusBorder,
                    fontSize = 12.sp,
                    testTag = "key_tax_minus_3",
                    onClick = { onTaxSlabClick(r3, true) }
                )
            }

            // Col 4: UNDO (Full Height)
            TextKeypadButton(
                text = "UNDO",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyUndoBg,
                contentColor = colors.keyUndoText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                testTag = "gst_key_undo",
                onClick = onUndoClick
            )
        }

        Spacer(modifier = Modifier.height(spacing))

        // Row 3: 7, 8, 9, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            TextKeypadButton(
                text = "7",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                testTag = "gst_key_7",
                onClick = { onDigitClick("7") }
            )
            TextKeypadButton(
                text = "8",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                testTag = "gst_key_8",
                onClick = { onDigitClick("8") }
            )
            TextKeypadButton(
                text = "9",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                testTag = "gst_key_9",
                onClick = { onDigitClick("9") }
            )
            TextKeypadButton(
                text = "÷",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyOperatorBg,
                contentColor = colors.keyOperatorText,
                borderColor = colors.keyOperatorBorder,
                fontSize = 24.sp,
                testTag = "gst_key_divide",
                onClick = { onOperatorClick("÷") }
            )
        }

        Spacer(modifier = Modifier.height(spacing))

        // Row 4: 4, 5, 6, x
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            TextKeypadButton(
                text = "4",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                testTag = "gst_key_4",
                onClick = { onDigitClick("4") }
            )
            TextKeypadButton(
                text = "5",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                testTag = "gst_key_5",
                onClick = { onDigitClick("5") }
            )
            TextKeypadButton(
                text = "6",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                testTag = "gst_key_6",
                onClick = { onDigitClick("6") }
            )
            TextKeypadButton(
                text = "x",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyOperatorBg,
                contentColor = colors.keyOperatorText,
                borderColor = colors.keyOperatorBorder,
                fontSize = 22.sp,
                testTag = "gst_key_multiply",
                onClick = { onOperatorClick("x") }
            )
        }

        Spacer(modifier = Modifier.height(spacing))

        // Row 5: 1, 2, 3, -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            TextKeypadButton(
                text = "1",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                testTag = "gst_key_1",
                onClick = { onDigitClick("1") }
            )
            TextKeypadButton(
                text = "2",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                testTag = "gst_key_2",
                onClick = { onDigitClick("2") }
            )
            TextKeypadButton(
                text = "3",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                testTag = "gst_key_3",
                onClick = { onDigitClick("3") }
            )
            TextKeypadButton(
                text = "-",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyOperatorBg,
                contentColor = colors.keyOperatorText,
                borderColor = colors.keyOperatorBorder,
                fontSize = 26.sp,
                testTag = "gst_key_subtract",
                onClick = { onOperatorClick("-") }
            )
        }

        Spacer(modifier = Modifier.height(spacing))

        // Row 6: GANAKA, 0, ., +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            TextKeypadButton(
                text = "Ganaka",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = if (colors.isDark) colors.keyUndoText else GanakaTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                testTag = "gst_key_ganaka",
                onClick = onGanakaClick
            )
            TextKeypadButton(
                text = "0",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                testTag = "gst_key_0",
                onClick = { onDigitClick("0") }
            )
            TextKeypadButton(
                text = ".",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyNumberBg,
                borderColor = colors.keyNumberBorder,
                contentColor = colors.keyNumberText,
                fontSize = 26.sp,
                testTag = "gst_key_dot",
                onClick = onDecimalClick
            )
            TextKeypadButton(
                text = "+",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyAddBg,
                contentColor = colors.keyAddText,
                borderColor = colors.keyAddBorder,
                fontSize = 26.sp,
                testTag = "gst_key_add",
                onClick = { onOperatorClick("+") }
            )
        }
    }
}
