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
fun MasterKeypad(
    onDigitClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onDecimalClick: () -> Unit,
    onResetPageClick: () -> Unit,
    onClearLineClick: () -> Unit,
    onDeleteLastClick: () -> Unit,
    onEnterClick: () -> Unit,
    onParenthesesClick: () -> Unit,
    onUndoClick: () -> Unit,
    onPasteClick: () -> Unit,
    onPercentClick: () -> Unit,
    onGanakaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonHeight = 52.dp
    val spacing = 7.dp
    val colors = GanakaTheme.colors

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
                testTag = "key_reset",
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
                testTag = "key_clear_line",
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
                testTag = "key_delete_last",
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
                testTag = "key_enter",
                onClick = onEnterClick
            )
        }

        Spacer(modifier = Modifier.height(spacing))

        // Row 2: ( ), UNDO, PASTE, %
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            TextKeypadButton(
                text = "( )",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyParenBg,
                contentColor = colors.keyParenText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                testTag = "key_paren",
                onClick = onParenthesesClick
            )
            TextKeypadButton(
                text = "UNDO",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyUndoBg,
                contentColor = colors.keyUndoText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                testTag = "key_undo",
                onClick = onUndoClick
            )
            TextKeypadButton(
                text = "PASTE",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyPasteBg,
                contentColor = colors.keyPasteText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                testTag = "key_paste",
                onClick = onPasteClick
            )
            TextKeypadButton(
                text = "%",
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                backgroundColor = colors.keyPercentBg,
                contentColor = colors.keyPercentText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                testTag = "key_percent",
                onClick = onPercentClick
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
                testTag = "key_7",
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
                testTag = "key_8",
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
                testTag = "key_9",
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
                testTag = "key_divide",
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
                testTag = "key_4",
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
                testTag = "key_5",
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
                testTag = "key_6",
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
                testTag = "key_multiply",
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
                testTag = "key_1",
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
                testTag = "key_2",
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
                testTag = "key_3",
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
                testTag = "key_subtract",
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
                testTag = "key_ganaka",
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
                testTag = "key_0",
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
                testTag = "key_dot",
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
                testTag = "key_add",
                onClick = { onOperatorClick("+") }
            )
        }
    }
}
