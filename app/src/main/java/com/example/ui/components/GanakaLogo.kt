package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Minimalist app logo featuring the Kannada letter 'ಗ' (Ga for Ganaka / ಗಣಕ).
 */
@Composable
fun GanakaLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    showText: Boolean = false
) {
    val kannadaFont = remember {
        try {
            FontFamily(Font(R.font.kannada, FontWeight.Bold))
        } catch (_: Exception) {
            FontFamily.Default
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Minimal Emblem Container with Kannada letter 'ಗ'
        val cornerRadius = size * 0.24f
        val shape = RoundedCornerShape(cornerRadius)

        Box(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = (size.value * 0.08f).dp,
                    shape = shape,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color(0xFF0F172A).copy(alpha = 0.35f)
                )
                .clip(shape)
                .background(Color(0xFF000000))
                .border(
                    width = (size.value * 0.02f).coerceAtLeast(1f).dp,
                    color = Color(0xFF262626),
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ಗ",
                fontSize = (size.value * 0.28f).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = kannadaFont,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = size * 0.015f)
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.height(size * 0.12f))
            Text(
                text = "Ganaka",
                fontSize = (size.value * 0.22f).sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = Color.Black
            )
            Text(
                text = "TAPE & GST CALCULATOR",
                fontSize = (size.value * 0.09f).sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = Color(0xFF2563EB)
            )
        }
    }
}
