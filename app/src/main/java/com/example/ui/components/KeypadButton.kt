package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.HapticFeedbackHelper

val LocalHapticFeedbackEnabled = compositionLocalOf { true }

@Composable
fun KeypadButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color = Color(0xFF0F172A),
    borderColor: Color? = null,
    onClick: () -> Unit,
    testTag: String,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current
    val hapticEnabled = LocalHapticFeedbackEnabled.current
    val colors = com.example.ui.theme.GanakaTheme.colors
    val shape = if (colors.isDark) RoundedCornerShape(22.dp) else RoundedCornerShape(14.dp)
    val borderWidth = if (colors.isDark && borderColor != null && borderColor != colors.keyNumberBorder) 1.5.dp else 1.dp

    val borderMod = if (borderColor != null) {
        Modifier.border(width = borderWidth, color = borderColor, shape = shape)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .then(borderMod)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = contentColor.copy(alpha = 0.2f)),
                onClick = {
                    if (hapticEnabled) {
                        HapticFeedbackHelper.performTactileFeedback(view, context, enabled = true)
                    }
                    try {
                        view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                    } catch (_: Exception) {}
                    onClick()
                }
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun TextKeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color = Color(0xFF0F172A),
    borderColor: Color? = null,
    fontSize: TextUnit = 22.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    testTag: String,
    onClick: () -> Unit
) {
    KeypadButton(
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        borderColor = borderColor,
        onClick = onClick,
        testTag = testTag
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}
