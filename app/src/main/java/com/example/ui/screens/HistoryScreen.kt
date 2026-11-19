package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalculationEntity
import com.example.ui.MainViewModel
import com.example.ui.components.DottedDivider
import com.example.ui.components.GstBreakdownDialog
import com.example.ui.components.GstTapeItemView
import com.example.ui.components.MasterTapeItemView
import com.example.ui.theme.ClearLogBg
import com.example.ui.theme.ClearLogBorder
import com.example.ui.theme.ClearLogText
import com.example.ui.theme.GanakaBorder
import com.example.ui.theme.GanakaTextMuted

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allItems by viewModel.allCalculations.collectAsState()
    val settings by viewModel.settings.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    var gstDetailsItem by remember { mutableStateOf<CalculationEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Transparent)
            .testTag("screen_history")
    ) {
        if (allItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = GanakaTextMuted.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No calculations logged yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = GanakaTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("history_list")
            ) {
                items(allItems, key = { it.id }) { item ->
                    if (item.mode == "GST") {
                        GstTapeItemView(
                            item = item,
                            numberSystem = settings.numberSystem,
                            onItemClick = {
                                viewModel.selectTab(com.example.model.AppTab.GST)
                                viewModel.openEditDialog(item)
                            },
                            onItemLongClick = {
                                gstDetailsItem = item
                            }
                        )
                    } else {
                        Column {
                            MasterTapeItemView(
                                item = item,
                                numberSystem = settings.numberSystem,
                                onItemClick = {
                                    viewModel.selectTab(com.example.model.AppTab.MASTER)
                                    viewModel.openEditDialog(item)
                                }
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            DottedDivider()
                        }
                    }
                }
            }
        }

        val colors = com.example.ui.theme.GanakaTheme.colors
        HorizontalDivider(
            color = colors.border.copy(alpha = 0.5f),
            thickness = 1.dp
        )

        // Clear Log Button
        val buttonShape = RoundedCornerShape(12.dp)
        val clearBg = if (colors.isDark) Color(0xFF261418) else ClearLogBg
        val clearBorder = if (colors.isDark) Color(0xFF7F1D1D) else ClearLogBorder
        val clearText = if (colors.isDark) Color(0xFFF87171) else ClearLogText

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(buttonShape)
                    .background(clearBg)
                    .border(width = 1.dp, color = clearBorder, shape = buttonShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = clearText.copy(alpha = 0.2f)),
                        onClick = {
                            if (allItems.isNotEmpty()) {
                                showClearDialog = true
                            }
                        }
                    )
                    .testTag("button_clear_log"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Clear Log",
                        tint = clearText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CLEAR LOG",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = clearText
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = "Clear Log",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to clear all calculations from the history log?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistoryLog()
                        showClearDialog = false
                    },
                    modifier = Modifier.testTag("confirm_clear_log")
                ) {
                    Text("Clear", color = ClearLogText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (gstDetailsItem != null) {
        GstBreakdownDialog(
            item = gstDetailsItem!!,
            numberSystem = settings.numberSystem,
            onDismiss = { gstDetailsItem = null },
            onEdit = {
                val target = gstDetailsItem!!
                gstDetailsItem = null
                viewModel.selectTab(com.example.model.AppTab.GST)
                viewModel.openEditDialog(target)
            }
        )
    }
}
