package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
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
import com.example.model.AppTab
import com.example.ui.theme.GanakaTheme

@Composable
fun TopNavigationBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GanakaTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tab 1: History
        val isHistory = currentTab == AppTab.HISTORY
        TabItem(
            selected = isHistory,
            activeBg = colors.tabHistoryActiveBg,
            activeBorder = colors.tabHistoryActiveBorder,
            activeContent = colors.tabHistoryActiveContent,
            inactiveBg = colors.tabInactiveBg,
            inactiveBorder = colors.tabInactiveBorder,
            inactiveContent = colors.tabInactiveContent,
            onClick = { onTabSelected(AppTab.HISTORY) },
            testTag = "tab_history",
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = "History Log",
                tint = if (isHistory) colors.tabHistoryActiveContent else colors.tabInactiveContent
            )
        }

        // Tab 2: MASTER
        val isMaster = currentTab == AppTab.MASTER
        TabItem(
            selected = isMaster,
            activeBg = colors.tabMasterActiveBg,
            activeBorder = colors.tabMasterActiveBorder,
            activeContent = colors.tabMasterActiveContent,
            inactiveBg = colors.tabInactiveBg,
            inactiveBorder = colors.tabInactiveBorder,
            inactiveContent = colors.tabInactiveContent,
            onClick = { onTabSelected(AppTab.MASTER) },
            testTag = "tab_master",
            modifier = Modifier.weight(1.15f)
        ) {
            Text(
                text = "MASTER",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
                color = if (isMaster) colors.tabMasterActiveContent else colors.tabInactiveContent
            )
        }

        // Tab 3: GST
        val isGst = currentTab == AppTab.GST
        TabItem(
            selected = isGst,
            activeBg = colors.tabGstActiveBg,
            activeBorder = colors.tabGstActiveBorder,
            activeContent = colors.tabGstActiveContent,
            inactiveBg = colors.tabInactiveBg,
            inactiveBorder = colors.tabInactiveBorder,
            inactiveContent = colors.tabInactiveContent,
            onClick = { onTabSelected(AppTab.GST) },
            testTag = "tab_gst",
            modifier = Modifier.weight(1.15f)
        ) {
            Text(
                text = "GST",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
                color = if (isGst) colors.tabGstActiveContent else colors.tabInactiveContent
            )
        }

        // Tab 4: Settings
        val isSettings = currentTab == AppTab.SETTINGS
        TabItem(
            selected = isSettings,
            activeBg = colors.tabSettingsActiveBg,
            activeBorder = colors.tabSettingsActiveBorder,
            activeContent = colors.tabSettingsActiveContent,
            inactiveBg = colors.tabInactiveBg,
            inactiveBorder = colors.tabInactiveBorder,
            inactiveContent = colors.tabInactiveContent,
            onClick = { onTabSelected(AppTab.SETTINGS) },
            testTag = "tab_settings",
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = if (isSettings) colors.tabSettingsActiveContent else colors.tabInactiveContent
            )
        }
    }
}

@Composable
private fun TabItem(
    selected: Boolean,
    activeBg: Color,
    activeBorder: Color,
    activeContent: Color,
    inactiveBg: Color,
    inactiveBorder: Color,
    inactiveContent: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val bgColor = if (selected) activeBg else inactiveBg
    val borderColor = if (selected) activeBorder else inactiveBorder
    val borderWidth = if (selected && GanakaTheme.colors.isDark) 1.5.dp else 1.dp

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(shape)
            .background(bgColor)
            .border(width = borderWidth, color = borderColor, shape = shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
