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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppThemeMode
import com.example.ui.MainViewModel
import com.example.ui.theme.GanakaTextMuted
import com.example.ui.theme.GanakaTheme

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val colors = GanakaTheme.colors
    var showTaxRatesDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .testTag("screen_settings")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // Row 1: Theme (Light / Dark / System default)
            val themeDisplayName = when (settings.themeMode) {
                AppThemeMode.LIGHT -> "Light"
                AppThemeMode.DARK -> "Dark"
                AppThemeMode.SYSTEM -> "System default"
            }
            SettingRow(
                icon = Icons.Outlined.DarkMode,
                title = "Theme",
                valueChip = themeDisplayName,
                onClick = { showThemeDialog = true },
                testTag = "setting_theme"
            )

            HorizontalDivider(color = colors.border.copy(alpha = 0.5f), thickness = 1.dp)

            // Row 2: Default Tax Rates
            val ratesStr = settings.taxRates.joinToString(", ") { rate ->
                if (rate % 1.0 == 0.0) "${rate.toInt()}%" else "$rate%"
            }
            SettingRow(
                icon = Icons.Outlined.Percent,
                title = "Default Tax Rates",
                valueChip = ratesStr,
                onClick = { showTaxRatesDialog = true },
                testTag = "setting_tax_rates"
            )

            HorizontalDivider(color = colors.border.copy(alpha = 0.5f), thickness = 1.dp)

            // Row 3: Number System
            SettingRow(
                icon = Icons.Outlined.Language,
                title = "Number System",
                valueChip = settings.numberSystem.displayName,
                onClick = { viewModel.toggleNumberSystem() },
                testTag = "setting_number_system"
            )

            HorizontalDivider(color = colors.border.copy(alpha = 0.5f), thickness = 1.dp)

            // Row 4: Tactile Haptic Feedback
            SettingSwitchRow(
                icon = Icons.Outlined.Vibration,
                title = "Haptic Feedback",
                subtitle = "Tactile vibration on calculator button presses",
                checked = settings.hapticFeedbackEnabled,
                onCheckedChange = { viewModel.setHapticFeedbackEnabled(it) },
                testTag = "setting_haptic_feedback"
            )

            HorizontalDivider(color = colors.border.copy(alpha = 0.5f), thickness = 1.dp)

            // Row 5: Developer & About
            SettingRow(
                icon = Icons.Default.Info,
                title = "Developer & About",
                valueChip = "Prashanth Shetty",
                onClick = { viewModel.openDeveloperPopup() },
                testTag = "setting_developer"
            )

            HorizontalDivider(color = colors.border.copy(alpha = 0.5f), thickness = 1.dp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // App Logo & Version
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .clickable { viewModel.openDeveloperPopup() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            com.example.ui.components.GanakaLogo(size = 56.dp, showText = false)
            Text(
                text = "Ganaka v1.2.0",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = GanakaTextMuted.copy(alpha = 0.7f),
                modifier = Modifier.testTag("app_version_text")
            )
        }
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        val themeOptions = listOf(
            Triple(AppThemeMode.LIGHT, "Light", "Classic bright pastel aesthetic"),
            Triple(AppThemeMode.DARK, "Dark", "Emerald dark forest aesthetic"),
            Triple(AppThemeMode.SYSTEM, "System default", "Follows device appearance")
        )

        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text = "Choose Theme",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    themeOptions.forEach { (mode, label, desc) ->
                        val isSelected = settings.themeMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(horizontal = 8.dp, vertical = 10.dp)
                                .testTag("theme_option_${mode.name.lowercase()}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = label,
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = desc,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showThemeDialog = false },
                    modifier = Modifier.testTag("dismiss_theme_dialog")
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Default Tax Rates Configuration Dialog
    if (showTaxRatesDialog) {
        val currentRates = settings.taxRates
        var rate1 by remember { mutableStateOf(currentRates.getOrNull(0)?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "5") }
        var rate2 by remember { mutableStateOf(currentRates.getOrNull(1)?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "18") }
        var rate3 by remember { mutableStateOf(currentRates.getOrNull(2)?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "40") }

        AlertDialog(
            onDismissRequest = { showTaxRatesDialog = false },
            title = {
                Text(
                    text = "Configure Default Tax Rates",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Customize the 3 quick tax slab percentages on the GST keypad:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = rate1,
                        onValueChange = { rate1 = it },
                        label = { Text("Tax Rate 1 (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = rate2,
                        onValueChange = { rate2 = it },
                        label = { Text("Tax Rate 2 (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = rate3,
                        onValueChange = { rate3 = it },
                        label = { Text("Tax Rate 3 (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val r1 = rate1.toDoubleOrNull() ?: 5.0
                        val r2 = rate2.toDoubleOrNull() ?: 18.0
                        val r3 = rate3.toDoubleOrNull() ?: 40.0
                        viewModel.setTaxRates(r1, r2, r3)
                        showTaxRatesDialog = false
                    },
                    modifier = Modifier.testTag("save_tax_rates")
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTaxRatesDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    valueChip: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val colors = GanakaTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Value Chip
        val chipShape = RoundedCornerShape(8.dp)
        val chipBorderMod = if (colors.isDark) {
            Modifier.border(1.dp, colors.border, chipShape)
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .clip(chipShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .then(chipBorderMod)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = valueChip,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = { onCheckedChange(!checked) }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag("${testTag}_switch")
        )
    }
}
