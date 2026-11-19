package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NumberFormatter
import com.example.model.NumberSystem
import com.example.ui.theme.GanakaTheme

@Composable
fun CollapsePillHandle(
    isKeypadExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GanakaTheme.colors
    val pillShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
    Box(
        modifier = modifier
            .clip(pillShape)
            .background(colors.collapseHandleBg)
            .border(width = 1.dp, color = colors.collapseHandleBorder, shape = pillShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onToggle
            )
            .padding(horizontal = 14.dp, vertical = 2.dp)
            .testTag("keypad_toggle_handle"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isKeypadExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
            contentDescription = if (isKeypadExpanded) "Collapse Keypad" else "Expand Keypad",
            tint = colors.collapseHandleTint,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun TotalBottomBar(
    total: Double,
    numberSystem: NumberSystem,
    isKeypadExpanded: Boolean,
    onToggleKeypad: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GanakaTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(if (colors.isDark) Color.Transparent else colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Collapse/expand pill centered right at top border
        CollapsePillHandle(
            isKeypadExpanded = isKeypadExpanded,
            onToggle = onToggleKeypad,
            modifier = Modifier.offset(y = (-2).dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (colors.isDark) "T O T · A L =" else "TOTAL =",
                fontSize = if (colors.isDark) 18.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = if (colors.isDark) 1.5.sp else 0.5.sp,
                color = colors.totalLabelColor,
                modifier = Modifier.testTag("total_label")
            )

            Text(
                text = NumberFormatter.formatNumber(total, numberSystem),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.totalValueColor,
                modifier = Modifier.testTag("total_value")
            )
        }

        HorizontalDivider(
            color = colors.bottomBarBorder,
            thickness = 1.dp
        )
    }
}
