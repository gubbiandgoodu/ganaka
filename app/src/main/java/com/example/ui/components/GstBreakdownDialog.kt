package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalculationEntity
import com.example.model.NumberFormatter
import com.example.model.NumberSystem
import com.example.ui.theme.GanakaBorder
import com.example.ui.theme.GanakaTextDark
import com.example.ui.theme.GanakaTextMedium
import com.example.ui.theme.GanakaTextMuted
import com.example.ui.theme.GstBlue
import com.example.ui.theme.KeyGstMinus
import com.example.ui.theme.KeyGstMinusText
import com.example.ui.theme.KeyGstPlus
import com.example.ui.theme.KeyGstPlusText

/**
 * Popup dialog presenting full CGST, SGST, Base Amount, and Net/Gross details
 * when long-pressing any GST calculation line.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GstBreakdownDialog(
    item: CalculationEntity,
    numberSystem: NumberSystem,
    onDismiss: () -> Unit,
    onEdit: (() -> Unit)? = null
) {
    val totalRateStr = if (item.taxRate % 1.0 == 0.0) "${item.taxRate.toInt()}%" else "${item.taxRate}%"
    val halfRate = item.taxRate / 2.0
    val halfRateStr = if (halfRate % 1.0 == 0.0) "${halfRate.toInt()}%" else "$halfRate%"

    val enteredAmount = if (!item.isDeduction) item.baseAmount else (item.baseAmount + item.gstAmount)
    val enteredFormatted = NumberFormatter.formatNumber(enteredAmount, numberSystem)
    val baseFormatted = NumberFormatter.formatNumber(item.baseAmount, numberSystem)
    val cgstFormatted = NumberFormatter.formatNumber(item.cgstAmount, numberSystem)
    val sgstFormatted = NumberFormatter.formatNumber(item.sgstAmount, numberSystem)
    val gstFormatted = NumberFormatter.formatNumber(item.gstAmount, numberSystem)
    val totalFormatted = NumberFormatter.formatNumber(item.itemTotal, numberSystem)

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_gst_breakdown")
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "GST Details",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (item.isDeduction) "Reverse GST (Tax Deducted)" else "Forward GST (Tax Added)",
                                fontSize = 12.sp,
                                color = GanakaTextMuted
                            )
                        }
                    }

                    // Slab Pill Badge
                    val badgeBg = if (item.isDeduction) KeyGstMinus else KeyGstPlus
                    val badgeColor = if (item.isDeduction) KeyGstMinusText else KeyGstPlusText
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (item.isDeduction) "-$totalRateStr" else "+$totalRateStr",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown Card
                val cardShape = RoundedCornerShape(14.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(cardShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                        .border(1.dp, GanakaBorder.copy(alpha = 0.7f), cardShape)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Entered Expression or Amount
                    BreakdownRow(
                        label = "Entered Amount",
                        value = if (item.expression.isNotEmpty() && item.expression != enteredFormatted) {
                            "${item.expression} ($enteredFormatted)"
                        } else {
                            enteredFormatted
                        },
                        valueColor = GanakaTextDark
                    )

                    HorizontalDivider(color = GanakaBorder.copy(alpha = 0.4f), thickness = 1.dp)

                    // Base Amount (Taxable Value)
                    BreakdownRow(
                        label = "Base Amount (Net)",
                        value = baseFormatted,
                        valueColor = GanakaTextDark
                    )

                    // CGST
                    BreakdownRow(
                        label = "CGST ($halfRateStr)",
                        value = cgstFormatted,
                        valueColor = GanakaTextMedium
                    )

                    // SGST
                    BreakdownRow(
                        label = "SGST ($halfRateStr)",
                        value = sgstFormatted,
                        valueColor = GanakaTextMedium
                    )

                    // Total GST
                    BreakdownRow(
                        label = if (item.isDeduction) "GST (-$totalRateStr)" else "GST (+$totalRateStr)",
                        value = if (item.isDeduction) "-$gstFormatted" else gstFormatted,
                        valueColor = GstBlue,
                        isBold = true
                    )

                    HorizontalDivider(color = GanakaBorder.copy(alpha = 0.6f), thickness = 1.dp)

                    // Final Item Total
                    BreakdownRow(
                        label = "Final Item Total",
                        value = totalFormatted,
                        valueColor = Color(0xFF2563EB),
                        isBold = true,
                        valueSize = 18
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onEdit != null) {
                        OutlinedButton(
                            onClick = onEdit,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_breakdown_edit")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Line", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_breakdown_close")
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    valueColor: Color,
    isBold: Boolean = false,
    valueSize: Int = 15
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) MaterialTheme.colorScheme.onSurface else GanakaTextMedium
        )
        Text(
            text = value,
            fontSize = valueSize.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}
