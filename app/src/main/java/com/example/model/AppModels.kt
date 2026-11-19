package com.example.model

enum class AppTab {
    HISTORY,
    MASTER,
    GST,
    SETTINGS
}

enum class NumberSystem(val displayName: String) {
    INDIAN("Indian (Lakhs)"),
    INTERNATIONAL("International (Millions)")
}

enum class AppThemeMode(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System")
}

data class GstBreakdown(
    val baseAmount: Double,
    val taxRatePercent: Double,
    val cgstRatePercent: Double,
    val sgstRatePercent: Double,
    val cgstAmount: Double,
    val sgstAmount: Double,
    val gstTotalAmount: Double,
    val itemTotal: Double,
    val isDeduction: Boolean = false
)
