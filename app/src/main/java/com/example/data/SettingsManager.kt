package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppThemeMode
import com.example.model.NumberSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsData(
    val showButtonLabels: Boolean = true,
    val taxRates: List<Double> = listOf(5.0, 18.0, 40.0),
    val numberSystem: NumberSystem = NumberSystem.INDIAN,
    val themeMode: AppThemeMode = AppThemeMode.LIGHT,
    val hapticFeedbackEnabled: Boolean = true
)

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ganaka_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<SettingsData> = _settings.asStateFlow()

    private fun loadSettings(): SettingsData {
        val showLabels = prefs.getBoolean("show_button_labels", true)
        val rate1 = prefs.getFloat("tax_rate_1", 5f).toDouble()
        val rate2 = prefs.getFloat("tax_rate_2", 18f).toDouble()
        val rate3 = prefs.getFloat("tax_rate_3", 40f).toDouble()
        val numSystemStr = prefs.getString("number_system", NumberSystem.INDIAN.name)
        val numSystem = try {
            NumberSystem.valueOf(numSystemStr ?: NumberSystem.INDIAN.name)
        } catch (_: Exception) {
            NumberSystem.INDIAN
        }
        val themeStr = prefs.getString("theme_mode", AppThemeMode.LIGHT.name)
        val themeMode = try {
            AppThemeMode.valueOf(themeStr ?: AppThemeMode.LIGHT.name)
        } catch (_: Exception) {
            AppThemeMode.LIGHT
        }
        val hapticEnabled = prefs.getBoolean("haptic_feedback_enabled", true)

        return SettingsData(
            showButtonLabels = showLabels,
            taxRates = listOf(rate1, rate2, rate3),
            numberSystem = numSystem,
            themeMode = themeMode,
            hapticFeedbackEnabled = hapticEnabled
        )
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("haptic_feedback_enabled", enabled).apply()
        _settings.value = _settings.value.copy(hapticFeedbackEnabled = enabled)
    }

    fun setShowButtonLabels(show: Boolean) {
        prefs.edit().putBoolean("show_button_labels", show).apply()
        _settings.value = _settings.value.copy(showButtonLabels = show)
    }

    fun setTaxRates(rate1: Double, rate2: Double, rate3: Double) {
        prefs.edit()
            .putFloat("tax_rate_1", rate1.toFloat())
            .putFloat("tax_rate_2", rate2.toFloat())
            .putFloat("tax_rate_3", rate3.toFloat())
            .apply()
        _settings.value = _settings.value.copy(taxRates = listOf(rate1, rate2, rate3))
    }

    fun setNumberSystem(system: NumberSystem) {
        prefs.edit().putString("number_system", system.name).apply()
        _settings.value = _settings.value.copy(numberSystem = system)
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }
}
