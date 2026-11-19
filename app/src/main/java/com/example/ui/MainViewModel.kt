package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.ExpressionEvaluator
import com.example.calculator.GstCalculator
import com.example.data.AppDatabase
import com.example.data.CalculationEntity
import com.example.data.SettingsData
import com.example.data.SettingsManager
import com.example.model.AppTab
import com.example.model.AppThemeMode
import com.example.model.NumberFormatter
import com.example.model.NumberSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UndoAction {
    data class CharDeleted(val mode: String, val deletedChar: Char, val restoredInput: String) : UndoAction
    data class LineCleared(val mode: String, val restoredLine: String) : UndoAction
    data class PageReset(val mode: String, val restoredItems: List<CalculationEntity>, val restoredInput: String) : UndoAction
    data class ItemDeleted(val restoredItem: CalculationEntity) : UndoAction
    data class ItemUpdated(val previousItem: CalculationEntity) : UndoAction
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.calculationDao()
    private val settingsManager = SettingsManager(application)
    private val undoStack = java.util.ArrayDeque<UndoAction>()

    val settings: StateFlow<SettingsData> = settingsManager.settings

    private val _currentTab = MutableStateFlow(AppTab.MASTER)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Keypad collapse/expand states
    private val _isMasterKeypadExpanded = MutableStateFlow(true)
    val isMasterKeypadExpanded: StateFlow<Boolean> = _isMasterKeypadExpanded.asStateFlow()

    private val _isGstKeypadExpanded = MutableStateFlow(true)
    val isGstKeypadExpanded: StateFlow<Boolean> = _isGstKeypadExpanded.asStateFlow()

    // Line currently selected for editing (null means new line at bottom)
    private val _editingItemId = MutableStateFlow<Long?>(null)
    val editingItemId: StateFlow<Long?> = _editingItemId.asStateFlow()

    // Dialog state for editing a line on tap
    private val _editingDialogItem = MutableStateFlow<CalculationEntity?>(null)
    val editingDialogItem: StateFlow<CalculationEntity?> = _editingDialogItem.asStateFlow()

    // Developer Dialog state
    private val _showDeveloperPopup = MutableStateFlow(false)
    val showDeveloperPopup: StateFlow<Boolean> = _showDeveloperPopup.asStateFlow()

    // Master Mode State
    private val _masterInput = MutableStateFlow("")
    val masterInput: StateFlow<String> = _masterInput.asStateFlow()

    private var lastEvaluatedMasterExpr: String = ""

    val masterCalculations: StateFlow<List<CalculationEntity>> = dao.getCalculationsByMode("MASTER")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val masterTotal: StateFlow<Double> = masterCalculations.map { list ->
        list.sumOf { it.result }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // GST Mode State
    private val _gstInput = MutableStateFlow("")
    val gstInput: StateFlow<String> = _gstInput.asStateFlow()

    val gstCalculations: StateFlow<List<CalculationEntity>> = dao.getCalculationsByMode("GST")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gstTotal: StateFlow<Double> = gstCalculations.map { list ->
        list.sumOf { it.itemTotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // History Log
    val allCalculations: StateFlow<List<CalculationEntity>> = dao.getAllCalculations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Ensure all seeded test calculations are removed
        viewModelScope.launch {
            dao.removeTestCalculations()
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
        cancelLineEditing()
    }

    fun toggleMasterKeypad() {
        _isMasterKeypadExpanded.value = !_isMasterKeypadExpanded.value
    }

    fun toggleGstKeypad() {
        _isGstKeypadExpanded.value = !_isGstKeypadExpanded.value
    }

    fun openDeveloperPopup() {
        _showDeveloperPopup.value = true
    }

    fun closeDeveloperPopup() {
        _showDeveloperPopup.value = false
    }

    // --- Line Editing Support ---

    fun openEditDialog(item: CalculationEntity) {
        _editingDialogItem.value = item
    }

    fun closeEditDialog() {
        _editingDialogItem.value = null
    }

    fun updateMasterLine(id: Long, newExpression: String) {
        val expr = newExpression.trim()
        if (expr.isEmpty()) return
        val evaluated = ExpressionEvaluator.evaluate(expr)
        if (evaluated != null) {
            val existing = masterCalculations.value.find { it.id == id }
            if (existing != null) {
                undoStack.push(UndoAction.ItemUpdated(existing))
                val updated = existing.copy(
                    expression = expr,
                    result = evaluated,
                    itemTotal = evaluated
                )
                viewModelScope.launch {
                    dao.updateCalculation(updated)
                    cancelLineEditing()
                    closeEditDialog()
                }
            }
        }
    }

    fun updateGstLine(
        id: Long,
        newBaseAmount: Double,
        newTaxRate: Double,
        isDeduction: Boolean,
        newExpression: String? = null
    ) {
        val existing = gstCalculations.value.find { it.id == id } ?: return
        undoStack.push(UndoAction.ItemUpdated(existing))

        val isTax = newTaxRate > 0.0

        val calc = if (isTax) {
            GstCalculator.calculate(newBaseAmount, newTaxRate, isDeduction)
        } else {
            null
        }

        val baseAmount = calc?.baseAmount ?: newBaseAmount
        val cgstAmount = calc?.cgstAmount ?: 0.0
        val sgstAmount = calc?.sgstAmount ?: 0.0
        val gstTotalAmount = calc?.gstAmount ?: 0.0
        val itemTotal = calc?.itemTotal ?: newBaseAmount

        val amountStr = if (newBaseAmount % 1.0 == 0.0) newBaseAmount.toLong().toString() else newBaseAmount.toString()
        val expr = newExpression ?: existing.expression.ifEmpty { amountStr }
        val updated = existing.copy(
            expression = expr,
            result = itemTotal,
            baseAmount = baseAmount,
            taxRate = if (isTax) newTaxRate else 0.0,
            cgstAmount = cgstAmount,
            sgstAmount = sgstAmount,
            gstAmount = gstTotalAmount,
            itemTotal = itemTotal,
            isDeduction = isTax && isDeduction
        )

        viewModelScope.launch {
            dao.updateCalculation(updated)
            cancelLineEditing()
            closeEditDialog()
        }
    }

    fun deleteCalculation(item: CalculationEntity) {
        undoStack.push(UndoAction.ItemDeleted(item))
        viewModelScope.launch {
            dao.deleteById(item.id)
            cancelLineEditing()
            closeEditDialog()
        }
    }

    fun selectLineForEditing(item: CalculationEntity) {
        _editingItemId.value = item.id
        if (item.mode == "MASTER") {
            _masterInput.value = item.expression
            _masterCursorPos.value = item.expression.length
            _isMasterKeypadExpanded.value = true
        } else {
            val expr = if (item.expression.isNotEmpty()) {
                item.expression
            } else {
                val entered = if (!item.isDeduction) item.baseAmount else (item.baseAmount + item.gstAmount)
                if (entered % 1.0 == 0.0) entered.toLong().toString() else entered.toString()
            }
            _gstInput.value = expr
            _gstCursorPos.value = expr.length
            _isGstKeypadExpanded.value = true
        }
    }

    fun cancelLineEditing() {
        _editingItemId.value = null
        _masterCursorPos.value = -1
        _gstCursorPos.value = -1
        if (_currentTab.value == AppTab.MASTER) {
            _masterInput.value = ""
        } else if (_currentTab.value == AppTab.GST) {
            _gstInput.value = ""
        }
    }

    // --- Cursor & Input Synchronization for Inline Editing ---
    private val _masterCursorPos = MutableStateFlow<Int>(-1)
    val masterCursorPos: StateFlow<Int> = _masterCursorPos.asStateFlow()

    private val _gstCursorPos = MutableStateFlow<Int>(-1)
    val gstCursorPos: StateFlow<Int> = _gstCursorPos.asStateFlow()

    fun onMasterInputChange(newText: String) {
        _masterInput.value = newText
    }

    fun setMasterCursor(pos: Int) {
        _masterCursorPos.value = pos
    }

    fun onGstInputChange(newText: String) {
        _gstInput.value = newText
    }

    fun setGstCursor(pos: Int) {
        _gstCursorPos.value = pos
    }

    // --- Master Mode Actions ---

    fun onMasterDigit(digit: String) {
        val curr = _masterInput.value
        val pos = if (_masterCursorPos.value in 0..curr.length) _masterCursorPos.value else curr.length
        _masterInput.value = curr.substring(0, pos) + digit + curr.substring(pos)
        _masterCursorPos.value = pos + digit.length
    }

    fun onMasterOperator(op: String) {
        val curr = _masterInput.value
        val pos = if (_masterCursorPos.value in 0..curr.length) _masterCursorPos.value else curr.length
        if (curr.isEmpty()) {
            if (op == "-") {
                _masterInput.value = "-"
                _masterCursorPos.value = 1
            }
            return
        }
        if (pos in 0..curr.length) {
            if (pos > 0 && curr[pos - 1] in listOf('+', '-', 'x', '÷', '*', '/')) {
                _masterInput.value = curr.substring(0, pos - 1) + op + curr.substring(pos)
                _masterCursorPos.value = pos
            } else {
                _masterInput.value = curr.substring(0, pos) + op + curr.substring(pos)
                _masterCursorPos.value = pos + op.length
            }
        } else {
            val lastChar = curr.last()
            if (lastChar in listOf('+', '-', 'x', '÷', '*', '/')) {
                _masterInput.value = curr.dropLast(1) + op
            } else {
                _masterInput.value = curr + op
            }
            _masterCursorPos.value = _masterInput.value.length
        }
    }

    fun onMasterDecimal() {
        val curr = _masterInput.value
        val pos = if (_masterCursorPos.value in 0..curr.length) _masterCursorPos.value else curr.length
        val left = curr.substring(0, pos)
        val lastSegment = left.split('+', '-', 'x', '÷', '*', '/', '(', ')').lastOrNull() ?: ""
        if (!lastSegment.contains('.')) {
            val insert = if (lastSegment.isEmpty()) "0." else "."
            _masterInput.value = curr.substring(0, pos) + insert + curr.substring(pos)
            _masterCursorPos.value = pos + insert.length
        }
    }

    fun onMasterParentheses() {
        val curr = _masterInput.value
        val pos = if (_masterCursorPos.value in 0..curr.length) _masterCursorPos.value else curr.length
        val openCount = curr.count { it == '(' }
        val closeCount = curr.count { it == ')' }

        val paren = if (openCount > closeCount && pos > 0 && (curr[pos - 1].isDigit() || curr[pos - 1] == ')')) {
            ")"
        } else {
            "("
        }
        _masterInput.value = curr.substring(0, pos) + paren + curr.substring(pos)
        _masterCursorPos.value = pos + paren.length
    }

    fun onMasterPercent() {
        val curr = _masterInput.value
        val pos = if (_masterCursorPos.value in 0..curr.length) _masterCursorPos.value else curr.length
        if (pos > 0 && (curr[pos - 1].isDigit() || curr[pos - 1] == ')')) {
            _masterInput.value = curr.substring(0, pos) + "%" + curr.substring(pos)
            _masterCursorPos.value = pos + 1
        }
    }

    fun onMasterDeleteLast() {
        val curr = _masterInput.value
        if (curr.isNotEmpty()) {
            val pos = if (_masterCursorPos.value in 1..curr.length) _masterCursorPos.value else curr.length
            val deleted = curr[pos - 1]
            val next = curr.substring(0, pos - 1) + curr.substring(pos)
            undoStack.push(UndoAction.CharDeleted("MASTER", deleted, curr))
            _masterInput.value = next
            _masterCursorPos.value = pos - 1
        } else if (_editingItemId.value != null) {
            // Delete the line being edited
            val editId = _editingItemId.value!!
            val item = masterCalculations.value.find { it.id == editId }
            if (item != null) {
                undoStack.push(UndoAction.ItemDeleted(item))
                viewModelScope.launch {
                    dao.deleteById(editId)
                    cancelLineEditing()
                }
            }
        } else if (masterCalculations.value.isNotEmpty()) {
            // Delete the last line on the tape
            val lastItem = masterCalculations.value.last()
            undoStack.push(UndoAction.ItemDeleted(lastItem))
            viewModelScope.launch {
                dao.deleteById(lastItem.id)
            }
        }
    }

    fun onMasterClearLine() {
        val curr = _masterInput.value
        if (curr.isNotEmpty()) {
            undoStack.push(UndoAction.LineCleared("MASTER", curr))
            _masterInput.value = ""
        } else if (_editingItemId.value != null) {
            cancelLineEditing()
        }
    }

    fun onMasterResetPage() {
        val currentItems = masterCalculations.value
        val currentInput = _masterInput.value
        if (currentItems.isNotEmpty() || currentInput.isNotEmpty()) {
            undoStack.push(UndoAction.PageReset("MASTER", currentItems, currentInput))
            viewModelScope.launch {
                dao.clearMode("MASTER")
                _masterInput.value = ""
                _editingItemId.value = null
            }
        }
    }

    fun onMasterEnter() {
        val expr = _masterInput.value.trim()
        if (expr.isEmpty()) return

        val evaluated = ExpressionEvaluator.evaluate(expr)
        if (evaluated != null) {
            lastEvaluatedMasterExpr = expr
            val editId = _editingItemId.value
            if (editId != null) {
                val existing = masterCalculations.value.find { it.id == editId }
                if (existing != null) {
                    undoStack.push(UndoAction.ItemUpdated(existing))
                    val updated = existing.copy(
                        expression = expr,
                        result = evaluated,
                        itemTotal = evaluated
                    )
                    viewModelScope.launch {
                        dao.updateCalculation(updated)
                        cancelLineEditing()
                    }
                    return
                }
            }

            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        mode = "MASTER",
                        expression = expr,
                        result = evaluated,
                        itemTotal = evaluated
                    )
                )
                _masterInput.value = ""
            }
        }
    }

    // --- GST Mode Actions ---

    fun onGstDigit(digit: String) {
        val curr = _gstInput.value
        val pos = if (_gstCursorPos.value in 0..curr.length) _gstCursorPos.value else curr.length
        _gstInput.value = curr.substring(0, pos) + digit + curr.substring(pos)
        _gstCursorPos.value = pos + digit.length
    }

    fun onGstOperator(op: String) {
        val curr = _gstInput.value
        val pos = if (_gstCursorPos.value in 0..curr.length) _gstCursorPos.value else curr.length
        if (curr.isEmpty()) {
            if (op == "-") {
                _gstInput.value = "-"
                _gstCursorPos.value = 1
            }
            return
        }
        if (pos in 0..curr.length) {
            if (pos > 0 && curr[pos - 1] in listOf('+', '-', 'x', '÷', '*', '/')) {
                _gstInput.value = curr.substring(0, pos - 1) + op + curr.substring(pos)
                _gstCursorPos.value = pos
            } else {
                _gstInput.value = curr.substring(0, pos) + op + curr.substring(pos)
                _gstCursorPos.value = pos + op.length
            }
        } else {
            val lastChar = curr.last()
            if (lastChar in listOf('+', '-', 'x', '÷', '*', '/')) {
                _gstInput.value = curr.dropLast(1) + op
            } else {
                _gstInput.value = curr + op
            }
            _gstCursorPos.value = _gstInput.value.length
        }
    }

    fun onGstDecimal() {
        val curr = _gstInput.value
        val pos = if (_gstCursorPos.value in 0..curr.length) _gstCursorPos.value else curr.length
        val left = curr.substring(0, pos)
        val lastSegment = left.split('+', '-', 'x', '÷', '*', '/', '(', ')', '%').lastOrNull() ?: ""
        if (!lastSegment.contains('.')) {
            val insert = if (lastSegment.isEmpty()) "0." else "."
            _gstInput.value = curr.substring(0, pos) + insert + curr.substring(pos)
            _gstCursorPos.value = pos + insert.length
        }
    }

    fun onGstPercent() {
        val curr = _gstInput.value
        val pos = if (_gstCursorPos.value in 0..curr.length) _gstCursorPos.value else curr.length
        if (pos > 0 && (curr[pos - 1].isDigit() || curr[pos - 1] == ')')) {
            _gstInput.value = curr.substring(0, pos) + "%" + curr.substring(pos)
            _gstCursorPos.value = pos + 1
        }
    }

    fun onGstDeleteLast() {
        val curr = _gstInput.value
        if (curr.isNotEmpty()) {
            val pos = if (_gstCursorPos.value in 1..curr.length) _gstCursorPos.value else curr.length
            val deleted = curr[pos - 1]
            val next = curr.substring(0, pos - 1) + curr.substring(pos)
            undoStack.push(UndoAction.CharDeleted("GST", deleted, curr))
            _gstInput.value = next
            _gstCursorPos.value = pos - 1
        } else if (_editingItemId.value != null) {
            // Delete the line being edited
            val editId = _editingItemId.value!!
            val item = gstCalculations.value.find { it.id == editId }
            if (item != null) {
                undoStack.push(UndoAction.ItemDeleted(item))
                viewModelScope.launch {
                    dao.deleteById(editId)
                    cancelLineEditing()
                }
            }
        } else if (gstCalculations.value.isNotEmpty()) {
            // Delete the last line on the tape
            val lastItem = gstCalculations.value.last()
            undoStack.push(UndoAction.ItemDeleted(lastItem))
            viewModelScope.launch {
                dao.deleteById(lastItem.id)
            }
        }
    }

    fun onGstClearLine() {
        val curr = _gstInput.value
        if (curr.isNotEmpty()) {
            undoStack.push(UndoAction.LineCleared("GST", curr))
            _gstInput.value = ""
        } else if (_editingItemId.value != null) {
            cancelLineEditing()
        }
    }

    fun onGstResetPage() {
        val currentItems = gstCalculations.value
        val currentInput = _gstInput.value
        if (currentItems.isNotEmpty() || currentInput.isNotEmpty()) {
            undoStack.push(UndoAction.PageReset("GST", currentItems, currentInput))
            viewModelScope.launch {
                dao.clearMode("GST")
                _gstInput.value = ""
                _editingItemId.value = null
            }
        }
    }

    fun onGstEnter() {
        // If Enter is clicked without a tax slab, evaluate expression and add as regular answer item (no CGST/SGST/GST)
        val curr = _gstInput.value.trim()
        if (curr.isEmpty()) return

        val evaluated = ExpressionEvaluator.evaluate(curr)
        if (evaluated != null) {
            val editId = _editingItemId.value
            if (editId != null) {
                val existing = gstCalculations.value.find { it.id == editId }
                if (existing != null) {
                    val taxRate = existing.taxRate
                    val isDeduction = existing.isDeduction
                    updateGstLine(editId, evaluated, taxRate, isDeduction, newExpression = curr)
                    return
                }
            }

            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        mode = "GST",
                        expression = curr,
                        result = evaluated,
                        baseAmount = evaluated,
                        taxRate = 0.0,
                        cgstAmount = 0.0,
                        sgstAmount = 0.0,
                        gstAmount = 0.0,
                        itemTotal = evaluated,
                        isDeduction = false
                    )
                )
                _gstInput.value = ""
                _gstCursorPos.value = -1
            }
        }
    }

    fun onGstTaxSlab(taxRate: Double, isDeduction: Boolean) {
        val curr = _gstInput.value.trim()
        if (curr.isEmpty()) return

        val baseEvaluated = ExpressionEvaluator.evaluate(curr)
        if (baseEvaluated == null || baseEvaluated <= 0.0) {
            return
        }

        val calc = GstCalculator.calculate(baseEvaluated, taxRate, isDeduction)

        val editId = _editingItemId.value
        if (editId != null) {
            val existing = gstCalculations.value.find { it.id == editId }
            if (existing != null) {
                undoStack.push(UndoAction.ItemUpdated(existing))
                val updated = existing.copy(
                    expression = curr,
                    result = calc.itemTotal,
                    baseAmount = calc.baseAmount,
                    taxRate = taxRate,
                    cgstAmount = calc.cgstAmount,
                    sgstAmount = calc.sgstAmount,
                    gstAmount = calc.gstAmount,
                    itemTotal = calc.itemTotal,
                    isDeduction = isDeduction
                )
                viewModelScope.launch {
                    dao.updateCalculation(updated)
                    cancelLineEditing()
                }
                return
            }
        }

        viewModelScope.launch {
            dao.insertCalculation(
                CalculationEntity(
                    mode = "GST",
                    expression = curr,
                    result = calc.itemTotal,
                    baseAmount = calc.baseAmount,
                    taxRate = taxRate,
                    cgstAmount = calc.cgstAmount,
                    sgstAmount = calc.sgstAmount,
                    gstAmount = calc.gstAmount,
                    itemTotal = calc.itemTotal,
                    isDeduction = isDeduction
                )
            )
            _gstInput.value = ""
            _gstCursorPos.value = -1
        }
    }

    // --- Undo / Restore Action (Works for both Master and GST!) ---
    fun onUndo() = onRestore()

    fun onRestore() {
        if (undoStack.isNotEmpty()) {
            val action = undoStack.pop()
            when (action) {
                is UndoAction.CharDeleted -> {
                    if (action.mode == "MASTER") {
                        _masterInput.value = action.restoredInput
                    } else {
                        _gstInput.value = action.restoredInput
                    }
                }
                is UndoAction.LineCleared -> {
                    if (action.mode == "MASTER") {
                        _masterInput.value = action.restoredLine
                    } else {
                        _gstInput.value = action.restoredLine
                    }
                }
                is UndoAction.PageReset -> {
                    viewModelScope.launch {
                        dao.insertAll(action.restoredItems)
                        if (action.mode == "MASTER") {
                            _masterInput.value = action.restoredInput
                        } else {
                            _gstInput.value = action.restoredInput
                        }
                    }
                }
                is UndoAction.ItemDeleted -> {
                    viewModelScope.launch {
                        dao.insertCalculation(action.restoredItem)
                    }
                }
                is UndoAction.ItemUpdated -> {
                    viewModelScope.launch {
                        dao.updateCalculation(action.previousItem)
                    }
                }
            }
        } else {
            if (_currentTab.value == AppTab.MASTER && lastEvaluatedMasterExpr.isNotEmpty()) {
                _masterInput.value = lastEvaluatedMasterExpr
            }
        }
    }

    fun onPaste() {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clipText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()?.trim()
        if (!clipText.isNullOrEmpty()) {
            val sanitized = clipText.filter { it.isDigit() || it in "+-x*÷/.%() " }
            if (sanitized.isNotEmpty()) {
                if (_currentTab.value == AppTab.MASTER) {
                    _masterInput.value += sanitized
                } else if (_currentTab.value == AppTab.GST) {
                    _gstInput.value += sanitized
                }
            }
        }
    }

    // --- Clipboard & Common Actions ---

    fun copyMasterTotalToClipboard() {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val formatted = NumberFormatter.formatNumber(masterTotal.value, settings.value.numberSystem)
        val clip = ClipData.newPlainText("Ganaka Total", formatted)
        clipboard.setPrimaryClip(clip)
    }

    fun copyGstTotalToClipboard() {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val formatted = NumberFormatter.formatNumber(gstTotal.value, settings.value.numberSystem)
        val clip = ClipData.newPlainText("Ganaka GST Total", formatted)
        clipboard.setPrimaryClip(clip)
    }

    fun clearHistoryLog() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }

    // --- Settings Actions ---

    fun toggleButtonLabels() {
        val current = settings.value.showButtonLabels
        settingsManager.setShowButtonLabels(!current)
    }

    fun setTaxRates(rate1: Double, rate2: Double, rate3: Double) {
        settingsManager.setTaxRates(rate1, rate2, rate3)
    }

    fun toggleNumberSystem() {
        val next = if (settings.value.numberSystem == NumberSystem.INDIAN) {
            NumberSystem.INTERNATIONAL
        } else {
            NumberSystem.INDIAN
        }
        settingsManager.setNumberSystem(next)
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        settingsManager.setHapticFeedbackEnabled(enabled)
    }

    fun toggleHapticFeedback() {
        val current = settings.value.hapticFeedbackEnabled
        settingsManager.setHapticFeedbackEnabled(!current)
    }

    fun setThemeMode(mode: AppThemeMode) {
        settingsManager.setThemeMode(mode)
    }

    fun cycleThemeMode() {
        val next = when (settings.value.themeMode) {
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.SYSTEM
            AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
        }
        settingsManager.setThemeMode(next)
    }
}
