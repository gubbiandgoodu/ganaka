package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import com.example.util.HapticFeedbackHelper
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ExpressionEvaluator
import com.example.calculator.GstCalculator
import com.example.data.CalculationEntity
import com.example.model.NumberFormatter
import com.example.model.NumberSystem
import com.example.ui.theme.GanakaBorder
import com.example.ui.theme.GanakaTextDark
import com.example.ui.theme.GanakaTextMedium
import com.example.ui.theme.GanakaTextMuted
import com.example.ui.theme.GstBlue
import kotlinx.coroutines.delay

@Composable
fun DottedDivider(
    modifier: Modifier = Modifier,
    color: Color = com.example.ui.theme.GanakaTheme.colors.receiptDividerColor,
    dotSpacing: Float = 8f,
    dotRadius: Float = 3f
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dotRadius, dotSpacing), 0f)
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2f,
            pathEffect = pathEffect
        )
    }
}

/**
 * InteractivePointerText allows users to tap or drag anywhere on the line
 * to place the pointer (cursor) precisely at any character position without
 * invoking the Android software keyboard. Keypad buttons handle all edits.
 */
@Composable
fun InteractivePointerText(
    text: String,
    cursorPos: Int,
    onCursorChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        fontSize = 19.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    ),
    cursorColor: Color = Color(0xFF16A34A),
    alignEnd: Boolean = false
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var cursorVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            cursorVisible = !cursorVisible
        }
    }

    val safeCursorPos = if (cursorPos < 0) text.length else cursorPos.coerceIn(0, text.length)

    Box(
        modifier = modifier
            .pointerInput(text) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    textLayoutResult?.let { layout ->
                        val pos = layout.getOffsetForPosition(down.position)
                        onCursorChange(pos)
                    }
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break
                        if (!change.pressed) break
                        textLayoutResult?.let { layout ->
                            val pos = layout.getOffsetForPosition(change.position)
                            onCursorChange(pos)
                        }
                    }
                }
            }
            .drawWithContent {
                drawContent()
                if (cursorVisible) {
                    val layout = textLayoutResult
                    if (layout != null && text.isNotEmpty()) {
                        val rect = layout.getCursorRect(safeCursorPos)
                        drawRoundRect(
                            color = cursorColor,
                            topLeft = Offset(rect.left.coerceAtLeast(0f), rect.top + 2.dp.toPx()),
                            size = Size(2.5.dp.toPx(), (rect.bottom - rect.top - 4.dp.toPx()).coerceAtLeast(16.dp.toPx())),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    } else {
                        drawRoundRect(
                            color = cursorColor,
                            topLeft = Offset(if (alignEnd) (size.width - 4.dp.toPx()).coerceAtLeast(0f) else 0f, 4.dp.toPx()),
                            size = Size(2.5.dp.toPx(), (size.height - 8.dp.toPx()).coerceAtLeast(18.dp.toPx())),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }
                }
            }
    ) {
        Text(
            text = if (text.isEmpty()) " " else text,
            style = textStyle,
            onTextLayout = { textLayoutResult = it },
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MasterTapeItemView(
    item: CalculationEntity,
    numberSystem: NumberSystem,
    isEditing: Boolean = false,
    activeEditingInput: String = "",
    cursorPos: Int = -1,
    onInputChange: ((String) -> Unit)? = null,
    onCursorChange: ((Int) -> Unit)? = null,
    onSaveEdit: (() -> Unit)? = null,
    onCancelEdit: (() -> Unit)? = null,
    onDeleteEdit: (() -> Unit)? = null,
    onItemClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    val backgroundModifier = if (isEditing) {
        Modifier
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clip(shape)
            .background(Color(0xFFF0FDF4))
            .border(1.5.dp, Color(0xFF16A34A), shape)
    } else {
        Modifier
            .padding(horizontal = 4.dp, vertical = 1.dp)
            .clip(shape)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(backgroundModifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                enabled = !isEditing && onItemClick != null,
                onClick = { onItemClick?.invoke() }
            )
            .padding(horizontal = 12.dp, vertical = if (isEditing) 8.dp else 6.dp)
            .testTag("master_item_${item.id}")
    ) {
        if (isEditing) {
            // Header bar for inline editing with Save, Delete, Cancel buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editing",
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "EDITING LINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF16A34A)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Save Button
                    if (onSaveEdit != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF16A34A))
                                .clickable(onClick = onSaveEdit)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = "Save Line",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "SAVE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Delete Button
                    if (onDeleteEdit != null) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFEE2E2))
                                .clickable(onClick = onDeleteEdit),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete Line",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Cancel Button
                    if (onCancelEdit != null) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE2E8F0))
                                .clickable(onClick = onCancelEdit),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Cancel Editing",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Editable Input with pointer positioning (NO Android software keyboard)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    InteractivePointerText(
                        text = activeEditingInput,
                        cursorPos = cursorPos,
                        onCursorChange = { onCursorChange?.invoke(it) },
                        cursorColor = Color(0xFF16A34A),
                        textStyle = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                // Live Evaluated Preview Result
                val evaluatedPreview = remember(activeEditingInput) {
                    ExpressionEvaluator.evaluate(activeEditingInput.trim())
                }
                val previewTotal = evaluatedPreview ?: item.result
                Text(
                    text = NumberFormatter.formatNumber(previewTotal, numberSystem),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (evaluatedPreview != null) MaterialTheme.colorScheme.onSurface else GanakaTextMuted,
                    textAlign = TextAlign.End
                )
            }
        } else {
            // Normal unedited display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Tap to edit",
                        tint = GanakaTextMuted.copy(alpha = 0.35f),
                        modifier = Modifier
                            .size(14.dp)
                            .padding(end = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.expression,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GanakaTextMuted
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = NumberFormatter.formatNumber(item.result, numberSystem),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GstTapeItemView(
    item: CalculationEntity,
    numberSystem: NumberSystem,
    isEditing: Boolean = false,
    activeEditingInput: String = "",
    cursorPos: Int = -1,
    onInputChange: ((String) -> Unit)? = null,
    onCursorChange: ((Int) -> Unit)? = null,
    onSaveEdit: (() -> Unit)? = null,
    onCancelEdit: (() -> Unit)? = null,
    onDeleteEdit: (() -> Unit)? = null,
    onItemClick: (() -> Unit)? = null,
    onItemLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isTaxItem = item.taxRate > 0.0

    val shape = RoundedCornerShape(10.dp)
    val backgroundModifier = if (isEditing) {
        Modifier
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clip(shape)
            .background(Color(0xFFEFF6FF))
            .border(1.5.dp, Color(0xFF2563EB), shape)
    } else {
        Modifier
            .padding(horizontal = 4.dp, vertical = 1.dp)
            .clip(shape)
    }

    val view = LocalView.current
    val context = LocalContext.current
    val hapticEnabled = LocalHapticFeedbackEnabled.current

    val clickableMod = if (!isEditing && onItemClick != null) {
        Modifier.combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            onClick = { onItemClick.invoke() },
            onLongClick = {
                if (isTaxItem && onItemLongClick != null) {
                    if (hapticEnabled) {
                        HapticFeedbackHelper.performTactileFeedback(view, context, enabled = true)
                    }
                    onItemLongClick.invoke()
                }
            }
        )
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(backgroundModifier)
            .then(clickableMod)
            .padding(horizontal = 12.dp, vertical = if (isEditing) 8.dp else 6.dp)
            .testTag("gst_item_${item.id}")
    ) {
        if (isEditing) {
            // Header bar for inline editing with Save, Delete, Cancel buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editing",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val totalRateStr = if (item.taxRate % 1.0 == 0.0) "${item.taxRate.toInt()}%" else "${item.taxRate}%"
                    Text(
                        text = if (isTaxItem) {
                            "EDITING GST LINE (${if (item.isDeduction) "-$totalRateStr" else "+$totalRateStr"})"
                        } else {
                            "EDITING LINE"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        color = Color(0xFF2563EB)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Save Button
                    if (onSaveEdit != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2563EB))
                                .clickable(onClick = onSaveEdit)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = "Save Line",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "SAVE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Delete Button
                    if (onDeleteEdit != null) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFEE2E2))
                                .clickable(onClick = onDeleteEdit),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete Line",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Cancel Button
                    if (onCancelEdit != null) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE2E8F0))
                                .clickable(onClick = onCancelEdit),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Cancel Editing",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Editable Input with pointer positioning (NO Android software keyboard)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    InteractivePointerText(
                        text = activeEditingInput,
                        cursorPos = cursorPos,
                        onCursorChange = { onCursorChange?.invoke(it) },
                        cursorColor = Color(0xFF2563EB),
                        textStyle = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                val evaluatedPreview = remember(activeEditingInput) {
                    ExpressionEvaluator.evaluate(activeEditingInput.trim())
                }
                val previewTotal = remember(evaluatedPreview, item) {
                    if (evaluatedPreview != null) {
                        if (isTaxItem) {
                            GstCalculator.calculate(evaluatedPreview, item.taxRate, item.isDeduction).itemTotal
                        } else {
                            evaluatedPreview
                        }
                    } else {
                        item.itemTotal
                    }
                }

                Text(
                    text = NumberFormatter.formatNumber(previewTotal, numberSystem),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (evaluatedPreview != null) MaterialTheme.colorScheme.onSurface else GanakaTextMuted,
                    textAlign = TextAlign.End
                )
            }
        } else {
            // Normal unedited single-line display
            val totalRateStr = if (item.taxRate % 1.0 == 0.0) "${item.taxRate.toInt()}%" else "${item.taxRate}%"
            val signStr = if (item.isDeduction) "-$totalRateStr" else "+$totalRateStr"
            val displayText = if (isTaxItem) {
                if (item.expression.isNotEmpty()) {
                    "${item.expression} ($signStr)"
                } else {
                    val initialAmt = if (!item.isDeduction) item.baseAmount else (item.baseAmount + item.gstAmount)
                    "${NumberFormatter.formatNumber(initialAmt, numberSystem)} ($signStr)"
                }
            } else {
                item.expression.ifEmpty { NumberFormatter.formatNumber(item.result, numberSystem) }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Tap to edit",
                        tint = GanakaTextMuted.copy(alpha = 0.35f),
                        modifier = Modifier
                            .size(14.dp)
                            .padding(end = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = displayText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GanakaTextDark
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = NumberFormatter.formatNumber(item.itemTotal, numberSystem),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        DottedDivider()
    }
}

@Composable
fun ActiveInputRow(
    input: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var cursorVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(530)
            cursorVisible = !cursorVisible
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("active_input_row")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = input,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (cursorVisible) {
                Text(
                    text = "|",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        DottedDivider()
    }
}
