package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.CalculationEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ActiveInputRow
import com.example.ui.components.GstBreakdownDialog
import com.example.ui.components.GstKeypad
import com.example.ui.components.GstTapeItemView
import com.example.ui.components.TotalBottomBar

@Composable
fun GstScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.gstCalculations.collectAsState()
    val total by viewModel.gstTotal.collectAsState()
    val input by viewModel.gstInput.collectAsState()
    val editingItemId by viewModel.editingItemId.collectAsState()
    val cursorPos by viewModel.gstCursorPos.collectAsState()
    val isKeypadExpanded by viewModel.isGstKeypadExpanded.collectAsState()
    val settings by viewModel.settings.collectAsState()
    var gstDetailsItem by remember { mutableStateOf<CalculationEntity?>(null) }

    val listState = rememberLazyListState()

    LaunchedEffect(items.size, editingItemId) {
        if (editingItemId == null && items.isNotEmpty()) {
            listState.animateScrollToItem(items.size)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Transparent)
            .testTag("screen_gst")
    ) {
        // Tape receipt area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("gst_tape_list")
            ) {
                items(items, key = { it.id }) { item ->
                    val isEditingThis = editingItemId == item.id
                    GstTapeItemView(
                        item = item,
                        numberSystem = settings.numberSystem,
                        isEditing = isEditingThis,
                        activeEditingInput = input,
                        cursorPos = cursorPos,
                        onInputChange = { viewModel.onGstInputChange(it) },
                        onCursorChange = { viewModel.setGstCursor(it) },
                        onSaveEdit = { viewModel.onGstEnter() },
                        onCancelEdit = { viewModel.cancelLineEditing() },
                        onDeleteEdit = { viewModel.deleteCalculation(item) },
                        onItemClick = { viewModel.selectLineForEditing(item) },
                        onItemLongClick = { gstDetailsItem = item }
                    )
                }

                // Active input row
                item(key = "active_gst_input") {
                    ActiveInputRow(
                        input = if (editingItemId == null) input else "",
                        onClick = {
                            if (editingItemId != null) {
                                viewModel.cancelLineEditing()
                            }
                        }
                    )
                }
            }
        }

        // Running Total Bar with Collapse Handle
        TotalBottomBar(
            total = total,
            numberSystem = settings.numberSystem,
            isKeypadExpanded = isKeypadExpanded,
            onToggleKeypad = { viewModel.toggleGstKeypad() }
        )

        // Keypad (expandable/collapsible)
        AnimatedVisibility(
            visible = isKeypadExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            GstKeypad(
                taxRates = settings.taxRates,
                onDigitClick = { viewModel.onGstDigit(it) },
                onOperatorClick = { viewModel.onGstOperator(it) },
                onDecimalClick = { viewModel.onGstDecimal() },
                onResetPageClick = { viewModel.onGstResetPage() },
                onClearLineClick = { viewModel.onGstClearLine() },
                onDeleteLastClick = { viewModel.onGstDeleteLast() },
                onEnterClick = { viewModel.onGstEnter() },
                onTaxSlabClick = { rate, isDeduction -> viewModel.onGstTaxSlab(rate, isDeduction) },
                onUndoClick = { viewModel.onUndo() },
                onGanakaClick = { viewModel.openDeveloperPopup() },
                modifier = Modifier.imePadding()
            )
        }
    }

    if (gstDetailsItem != null) {
        GstBreakdownDialog(
            item = gstDetailsItem!!,
            numberSystem = settings.numberSystem,
            onDismiss = { gstDetailsItem = null },
            onEdit = {
                val target = gstDetailsItem!!
                gstDetailsItem = null
                viewModel.selectLineForEditing(target)
            }
        )
    }
}
