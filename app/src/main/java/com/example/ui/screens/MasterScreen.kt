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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.MainViewModel
import com.example.ui.components.ActiveInputRow
import com.example.ui.components.MasterKeypad
import com.example.ui.components.MasterTapeItemView
import com.example.ui.components.TotalBottomBar

@Composable
fun MasterScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.masterCalculations.collectAsState()
    val total by viewModel.masterTotal.collectAsState()
    val input by viewModel.masterInput.collectAsState()
    val editingItemId by viewModel.editingItemId.collectAsState()
    val cursorPos by viewModel.masterCursorPos.collectAsState()
    val isKeypadExpanded by viewModel.isMasterKeypadExpanded.collectAsState()
    val settings by viewModel.settings.collectAsState()

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
            .testTag("screen_master")
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
                    .testTag("master_tape_list")
            ) {
                items(items, key = { it.id }) { item ->
                    val isEditingThis = editingItemId == item.id
                    MasterTapeItemView(
                        item = item,
                        numberSystem = settings.numberSystem,
                        isEditing = isEditingThis,
                        activeEditingInput = input,
                        cursorPos = cursorPos,
                        onInputChange = { viewModel.onMasterInputChange(it) },
                        onCursorChange = { viewModel.setMasterCursor(it) },
                        onSaveEdit = { viewModel.onMasterEnter() },
                        onCancelEdit = { viewModel.cancelLineEditing() },
                        onDeleteEdit = { viewModel.deleteCalculation(item) },
                        onItemClick = { viewModel.selectLineForEditing(item) }
                    )
                }

                // Active input row for creating a new line
                item(key = "active_input") {
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
            onToggleKeypad = { viewModel.toggleMasterKeypad() }
        )

        // Keypad (expandable/collapsible)
        AnimatedVisibility(
            visible = isKeypadExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            MasterKeypad(
                onDigitClick = { viewModel.onMasterDigit(it) },
                onOperatorClick = { viewModel.onMasterOperator(it) },
                onDecimalClick = { viewModel.onMasterDecimal() },
                onResetPageClick = { viewModel.onMasterResetPage() },
                onClearLineClick = { viewModel.onMasterClearLine() },
                onDeleteLastClick = { viewModel.onMasterDeleteLast() },
                onEnterClick = { viewModel.onMasterEnter() },
                onParenthesesClick = { viewModel.onMasterParentheses() },
                onUndoClick = { viewModel.onUndo() },
                onPasteClick = { viewModel.onPaste() },
                onPercentClick = { viewModel.onMasterPercent() },
                onGanakaClick = { viewModel.openDeveloperPopup() },
                modifier = Modifier.imePadding()
            )
        }
    }
}
