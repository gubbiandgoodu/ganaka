package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditMasterLineDialog(
    item: CalculationEntity,
    currentTotal: Double,
    numberSystem: NumberSystem,
    onDismiss: () -> Unit,
    onUpdate: (newExpression: String) -> Unit,
    onDelete: () -> Unit
) {
    var expressionText by remember { mutableStateOf(item.expression) }

    val evaluatedResult by remember(expressionText) {
        derivedStateOf {
            ExpressionEvaluator.evaluate(expressionText.trim())
        }
    }

    val updatedTotal by remember(evaluatedResult) {
        derivedStateOf {
            val eval = evaluatedResult
            if (eval != null) {
                currentTotal - item.result + eval
            } else {
                null
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_edit_master_line")
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Edit Calculation Line",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Update expression to recalculate total",
                            fontSize = 12.sp,
                            color = GanakaTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Editable Expression Field
                OutlinedTextField(
                    value = expressionText,
                    onValueChange = { expressionText = it },
                    label = { Text("Calculation Expression") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_expression")
                )

                // Quick Operator Buttons
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val ops = listOf("+", "-", "x", "÷", "%")
                    ops.forEach { op ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { expressionText += op },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = op,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live Preview Card
                val previewShape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(previewShape)
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, GanakaBorder, previewShape)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Line Result:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = GanakaTextMuted
                            )
                            Text(
                                text = if (evaluatedResult != null) {
                                    NumberFormatter.formatNumber(evaluatedResult!!, numberSystem)
                                } else {
                                    "Invalid expression"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (evaluatedResult != null) MaterialTheme.colorScheme.onSurface else Color(0xFFDC2626)
                            )
                        }

                        HorizontalDivider(color = GanakaBorder.copy(alpha = 0.5f), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Updated Total:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (updatedTotal != null) {
                                    NumberFormatter.formatNumber(updatedTotal!!, numberSystem)
                                } else {
                                    "---"
                                },
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Delete Button
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626)),
                        modifier = Modifier.testTag("button_delete_line")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontWeight = FontWeight.Bold)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = GanakaTextMuted)
                        }

                        Button(
                            onClick = {
                                if (evaluatedResult != null) {
                                    onUpdate(expressionText.trim())
                                }
                            },
                            enabled = evaluatedResult != null,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("button_save_line")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Update Total", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGstLineDialog(
    item: CalculationEntity,
    currentTotal: Double,
    taxRates: List<Double>,
    numberSystem: NumberSystem,
    onDismiss: () -> Unit,
    onUpdate: (newBaseAmount: Double, newTaxRate: Double, isDeduction: Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val initialAmountStr = if (item.expression.isNotEmpty()) {
        item.expression
    } else {
        val entered = if (!item.isDeduction) item.baseAmount else (item.baseAmount + item.gstAmount)
        if (entered % 1.0 == 0.0) entered.toLong().toString() else entered.toString()
    }

    var amountText by remember { mutableStateOf(initialAmountStr) }
    var selectedTaxRate by remember { mutableDoubleStateOf(item.taxRate) }
    var isDeduction by remember { mutableStateOf(item.isDeduction) }

    val baseEvaluated by remember(amountText) {
        derivedStateOf {
            ExpressionEvaluator.evaluate(amountText.trim())
        }
    }

    val calculatedValues by remember(baseEvaluated, selectedTaxRate, isDeduction) {
        derivedStateOf {
            val base = baseEvaluated
            if (base != null && base > 0.0) {
                val calc = GstCalculator.calculate(base, selectedTaxRate, isDeduction)
                val newTotal = currentTotal - item.itemTotal + calc.itemTotal
                CalculatedGstPreview(calc.baseAmount, calc.cgstAmount, calc.sgstAmount, calc.gstAmount, calc.itemTotal, newTotal)
            } else {
                null
            }
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_edit_gst_line")
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Edit GST Line",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Change amount or tax slab",
                            fontSize = 12.sp,
                            color = GanakaTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Editable Amount Field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Base Amount") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_gst_amount")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tax Rate Slabs Selection
                Text(
                    text = "Select Tax Slab:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GanakaTextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    taxRates.forEach { rate ->
                        val rateStr = if (rate % 1.0 == 0.0) "${rate.toInt()}%" else "$rate%"
                        val isSelected = selectedTaxRate == rate && !isDeduction
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF2563EB) else Color(0xFFEFF6FF))
                                .clickable {
                                    selectedTaxRate = rate
                                    isDeduction = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+$rateStr",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF1D4ED8)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    taxRates.forEach { rate ->
                        val rateStr = if (rate % 1.0 == 0.0) "${rate.toInt()}%" else "$rate%"
                        val isSelected = selectedTaxRate == rate && isDeduction
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFFDC2626) else Color(0xFFFEF2F2))
                                .clickable {
                                    selectedTaxRate = rate
                                    isDeduction = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "-$rateStr",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFFDC2626)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown Preview Box
                val previewShape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(previewShape)
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, GanakaBorder, previewShape)
                        .padding(12.dp)
                ) {
                    if (calculatedValues != null) {
                        val calc = calculatedValues!!
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val taxRateLabel = if (isDeduction) "-$selectedTaxRate%" else "$selectedTaxRate%"
                                Text(
                                    text = "GST ($taxRateLabel):",
                                    fontSize = 12.sp,
                                    color = GstBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                val gstSign = if (isDeduction) "-" else ""
                                Text(
                                    text = "$gstSign${NumberFormatter.formatNumber(calc.gstAmount, numberSystem)}",
                                    fontSize = 12.sp,
                                    color = GstBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Item Total:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = NumberFormatter.formatNumber(calc.itemTotal, numberSystem),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            HorizontalDivider(color = GanakaBorder.copy(alpha = 0.5f), thickness = 1.dp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Updated GST Total:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = NumberFormatter.formatNumber(calc.newTotal, numberSystem),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Enter a valid amount",
                            fontSize = 13.sp,
                            color = Color(0xFFDC2626),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626)),
                        modifier = Modifier.testTag("button_delete_gst_line")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontWeight = FontWeight.Bold)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = GanakaTextMuted)
                        }

                        Button(
                            onClick = {
                                if (baseEvaluated != null && baseEvaluated!! > 0.0) {
                                    onUpdate(baseEvaluated!!, selectedTaxRate, isDeduction)
                                }
                            },
                            enabled = baseEvaluated != null && baseEvaluated!! > 0.0,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("button_save_gst_line")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Update Total", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private data class CalculatedGstPreview(
    val baseAmount: Double,
    val cgstAmount: Double,
    val sgstAmount: Double,
    val gstAmount: Double,
    val itemTotal: Double,
    val newTotal: Double
)
