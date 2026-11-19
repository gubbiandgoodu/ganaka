package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class GanakaColors(
    val isDark: Boolean,
    val background: Color,
    val backgroundBrush: Brush?,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val textDark: Color,
    val textMuted: Color,
    val textMedium: Color,

    // Top Tabs
    val tabInactiveBg: Color,
    val tabInactiveBorder: Color,
    val tabInactiveContent: Color,
    val tabMasterActiveBg: Color,
    val tabMasterActiveBorder: Color,
    val tabMasterActiveContent: Color,
    val tabGstActiveBg: Color,
    val tabGstActiveBorder: Color,
    val tabGstActiveContent: Color,
    val tabHistoryActiveBg: Color,
    val tabHistoryActiveBorder: Color,
    val tabHistoryActiveContent: Color,
    val tabSettingsActiveBg: Color,
    val tabSettingsActiveBorder: Color,
    val tabSettingsActiveContent: Color,

    // Keypad Function Keys
    val keyResetBg: Color,
    val keyResetText: Color,
    val keyResetBorder: Color?,
    val keyClearBg: Color,
    val keyClearText: Color,
    val keyClearBorder: Color?,
    val keyDeleteBg: Color,
    val keyDeleteText: Color,
    val keyDeleteBorder: Color?,
    val keyEnterBg: Color,
    val keyEnterText: Color,
    val keyEnterBorder: Color?,

    // Keypad Utility Keys
    val keyParenBg: Color,
    val keyParenText: Color,
    val keyUndoBg: Color,
    val keyUndoText: Color,
    val keyPasteBg: Color,
    val keyPasteText: Color,
    val keyPercentBg: Color,
    val keyPercentText: Color,

    // Operators
    val keyOperatorBg: Color,
    val keyOperatorText: Color,
    val keyOperatorBorder: Color?,
    val keyAddBg: Color,
    val keyAddText: Color,
    val keyAddBorder: Color?,

    // Numbers
    val keyNumberBg: Color,
    val keyNumberText: Color,
    val keyNumberBorder: Color?,

    // GST Slabs
    val keyGstPlusBg: Color,
    val keyGstPlusText: Color,
    val keyGstPlusBorder: Color?,
    val keyGstMinusBg: Color,
    val keyGstMinusText: Color,
    val keyGstMinusBorder: Color?,

    // Total Bar & Handle
    val collapseHandleBg: Color,
    val collapseHandleBorder: Color,
    val collapseHandleTint: Color,
    val totalLabelColor: Color,
    val totalValueColor: Color,
    val bottomBarBorder: Color,

    // Tape Receipt
    val receiptDividerColor: Color,
    val receiptEditingBg: Color,
    val receiptEditingBorder: Color,
    val receiptCursorColor: Color
)

val LightGanakaColors = GanakaColors(
    isDark = false,
    background = Color(0xFFFFFFFF),
    backgroundBrush = null,
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF8FAFC),
    border = Color(0xFFE2E8F0),
    textDark = Color(0xFF0F172A),
    textMuted = Color(0xFF94A3B8),
    textMedium = Color(0xFF64748B),

    tabInactiveBg = Color(0xFFFFFFFF),
    tabInactiveBorder = Color(0xFFE2E8F0),
    tabInactiveContent = Color(0xFF64748B),
    tabMasterActiveBg = Color(0xFFFFEBF0),
    tabMasterActiveBorder = Color(0xFFFBC4AB),
    tabMasterActiveContent = Color(0xFF831843),
    tabGstActiveBg = Color(0xFFE0F2FE),
    tabGstActiveBorder = Color(0xFFBAE6FD),
    tabGstActiveContent = Color(0xFF0369A1),
    tabHistoryActiveBg = Color(0xFFE1F5EC),
    tabHistoryActiveBorder = Color(0xFFA7E8BD),
    tabHistoryActiveContent = Color(0xFF0F5132),
    tabSettingsActiveBg = Color(0xFFFEF7DA),
    tabSettingsActiveBorder = Color(0xFFFDE68A),
    tabSettingsActiveContent = Color(0xFF713F12),

    keyResetBg = Color(0xFFFF9AA2),
    keyResetText = Color(0xFF0F172A),
    keyResetBorder = null,
    keyClearBg = Color(0xFFFFD3A5),
    keyClearText = Color(0xFF0F172A),
    keyClearBorder = null,
    keyDeleteBg = Color(0xFFFFE885),
    keyDeleteText = Color(0xFF0F172A),
    keyDeleteBorder = null,
    keyEnterBg = Color(0xFF7CE7B9),
    keyEnterText = Color(0xFF0F172A),
    keyEnterBorder = null,

    keyParenBg = Color(0xFFD7D5FF),
    keyParenText = Color(0xFF0F172A),
    keyUndoBg = Color(0xFF8EE3D3),
    keyUndoText = Color(0xFF0F172A),
    keyPasteBg = Color(0xFFC7D2FE),
    keyPasteText = Color(0xFF0F172A),
    keyPercentBg = Color(0xFFA7D8FF),
    keyPercentText = Color(0xFF0F172A),

    keyOperatorBg = Color(0xFFC7D7FF),
    keyOperatorText = Color(0xFF0F172A),
    keyOperatorBorder = null,
    keyAddBg = Color(0xFFE8D3FF),
    keyAddText = Color(0xFF0F172A),
    keyAddBorder = null,

    keyNumberBg = Color(0xFFFFFFFF),
    keyNumberText = Color(0xFF0F172A),
    keyNumberBorder = Color(0xFFE2E8F0),

    keyGstPlusBg = Color(0xFFA0F0D8),
    keyGstPlusText = Color(0xFF064E3B),
    keyGstPlusBorder = null,
    keyGstMinusBg = Color(0xFFFFD1D7),
    keyGstMinusText = Color(0xFF881337),
    keyGstMinusBorder = null,

    collapseHandleBg = Color(0xFFFFFFFF),
    collapseHandleBorder = Color(0xFFE2E8F0),
    collapseHandleTint = Color(0xFF94A3B8),
    totalLabelColor = Color(0xFF94A3B8),
    totalValueColor = Color(0xFF0F172A),
    bottomBarBorder = Color(0xFFE2E8F0).copy(alpha = 0.5f),

    receiptDividerColor = Color(0xFFCBD5E1),
    receiptEditingBg = Color(0xFFEFF6FF),
    receiptEditingBorder = Color(0xFF2563EB),
    receiptCursorColor = Color(0xFF2563EB)
)

val DarkGanakaColors = GanakaColors(
    isDark = true,
    background = Color(0xFF05140E),
    backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF051710),
            Color(0xFF092A1D),
            Color(0xFF04120B)
        )
    ),
    surface = Color(0xFF0B1F16),
    surfaceVariant = Color(0xFF11261D),
    border = Color(0xFF1B3B2B),
    textDark = Color(0xFFFFFFFF),
    textMuted = Color(0xFF94A3B8),
    textMedium = Color(0xFFA7F3D0),

    tabInactiveBg = Color(0xFF0F221A),
    tabInactiveBorder = Color(0xFF183628),
    tabInactiveContent = Color(0xFF94A3B8),
    tabMasterActiveBg = Color(0xFF0A2B1D),
    tabMasterActiveBorder = Color(0xFF10B981),
    tabMasterActiveContent = Color(0xFF34D399),
    tabGstActiveBg = Color(0xFF0A2B1D),
    tabGstActiveBorder = Color(0xFF10B981),
    tabGstActiveContent = Color(0xFF34D399),
    tabHistoryActiveBg = Color(0xFF0A2B1D),
    tabHistoryActiveBorder = Color(0xFF10B981),
    tabHistoryActiveContent = Color(0xFF34D399),
    tabSettingsActiveBg = Color(0xFF0A2B1D),
    tabSettingsActiveBorder = Color(0xFF10B981),
    tabSettingsActiveContent = Color(0xFF34D399),

    keyResetBg = Color(0xFF18231C),
    keyResetText = Color(0xFFFB923C),
    keyResetBorder = Color(0x33FB923C),
    keyClearBg = Color(0xFF12241C),
    keyClearText = Color(0xFFF1F5F9),
    keyClearBorder = null,
    keyDeleteBg = Color(0xFF12241C),
    keyDeleteText = Color(0xFFF1F5F9),
    keyDeleteBorder = null,
    keyEnterBg = Color(0xFF0B2A1E),
    keyEnterText = Color(0xFF34D399),
    keyEnterBorder = Color(0xFF10B981),

    keyParenBg = Color(0xFF12241C),
    keyParenText = Color(0xFFA7F3D0),
    keyUndoBg = Color(0xFF12241C),
    keyUndoText = Color(0xFFA7F3D0),
    keyPasteBg = Color(0xFF12241C),
    keyPasteText = Color(0xFFA7F3D0),
    keyPercentBg = Color(0xFF12241C),
    keyPercentText = Color(0xFF34D399),

    keyOperatorBg = Color(0xFF12241C),
    keyOperatorText = Color(0xFF34D399),
    keyOperatorBorder = null,
    keyAddBg = Color(0xFF0B2A1E),
    keyAddText = Color(0xFF34D399),
    keyAddBorder = Color(0xFF10B981),

    keyNumberBg = Color(0xFF12241C),
    keyNumberText = Color(0xFFFFFFFF),
    keyNumberBorder = null,

    keyGstPlusBg = Color(0xFF0B2A1E),
    keyGstPlusText = Color(0xFF34D399),
    keyGstPlusBorder = Color(0xFF10B981),
    keyGstMinusBg = Color(0xFF26141B),
    keyGstMinusText = Color(0xFFFDA4AF),
    keyGstMinusBorder = Color(0x80FB7185),

    collapseHandleBg = Color(0xFF0B241A),
    collapseHandleBorder = Color(0xFF10B981),
    collapseHandleTint = Color(0xFF6EE7B7),
    totalLabelColor = Color(0xFFA7F3D0).copy(alpha = 0.85f),
    totalValueColor = Color(0xFFFFFFFF),
    bottomBarBorder = Color(0xFF1A382A).copy(alpha = 0.6f),

    receiptDividerColor = Color(0xFF1A3B2C),
    receiptEditingBg = Color(0xFF08261A),
    receiptEditingBorder = Color(0xFF10B981),
    receiptCursorColor = Color(0xFF34D399)
)

val LocalGanakaColors = staticCompositionLocalOf { LightGanakaColors }

object GanakaTheme {
    val colors: GanakaColors
        @Composable
        @ReadOnlyComposable
        get() = LocalGanakaColors.current
}

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF64748B),
    onSecondary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE2E8F0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF022C22),
    background = Color(0xFF05140E),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF0B1F16),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF11261D),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF1B3B2B),
    outlineVariant = Color(0xFF132A1F)
)

@Composable
fun GanakaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val ganakaColors = if (darkTheme) DarkGanakaColors else LightGanakaColors

    CompositionLocalProvider(
        LocalGanakaColors provides ganakaColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
