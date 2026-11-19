package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.calculator.ExpressionEvaluator
import com.example.model.NumberFormatter
import com.example.model.NumberSystem
import com.example.data.SettingsManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ganaka", appName)
  }

  @Test
  fun `evaluate arithmetic expressions`() {
    assertEquals(400.0, ExpressionEvaluator.evaluate("100+300")!!, 0.001)
    assertEquals(303.0, ExpressionEvaluator.evaluate("300+3")!!, 0.001)
    assertEquals(774.0, ExpressionEvaluator.evaluate("86x9")!!, 0.001)
    assertEquals(1180.0, ExpressionEvaluator.evaluate("1000+18%")!!, 0.001)
    assertEquals(820.0, ExpressionEvaluator.evaluate("1000-18%")!!, 0.001)
    assertEquals(180.0, ExpressionEvaluator.evaluate("1000x18%")!!, 0.001)
    assertEquals(0.5, ExpressionEvaluator.evaluate("50%")!!, 0.001)
  }

  @Test
  fun `format numbers in Indian and International systems`() {
    assertEquals("1,477", NumberFormatter.formatNumber(1477.0, NumberSystem.INDIAN))
    assertEquals("1,00,000", NumberFormatter.formatNumber(100000.0, NumberSystem.INDIAN))
    assertEquals("100,000", NumberFormatter.formatNumber(100000.0, NumberSystem.INTERNATIONAL))
  }

  @Test
  fun `haptic feedback setting defaults to true and can be toggled`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val settingsManager = SettingsManager(context)
    assertTrue(settingsManager.settings.value.hapticFeedbackEnabled)

    settingsManager.setHapticFeedbackEnabled(false)
    assertFalse(settingsManager.settings.value.hapticFeedbackEnabled)

    // Verify persistence across new manager instance
    val reloaded = SettingsManager(context)
    assertFalse(reloaded.settings.value.hapticFeedbackEnabled)

    settingsManager.setHapticFeedbackEnabled(true)
    assertTrue(settingsManager.settings.value.hapticFeedbackEnabled)
  }

  @Test
  fun `theme mode setting supports LIGHT, DARK, and SYSTEM and persists`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val settingsManager = SettingsManager(context)

    settingsManager.setThemeMode(com.example.model.AppThemeMode.DARK)
    assertEquals(com.example.model.AppThemeMode.DARK, settingsManager.settings.value.themeMode)

    val reloadedDark = SettingsManager(context)
    assertEquals(com.example.model.AppThemeMode.DARK, reloadedDark.settings.value.themeMode)

    settingsManager.setThemeMode(com.example.model.AppThemeMode.SYSTEM)
    assertEquals(com.example.model.AppThemeMode.SYSTEM, settingsManager.settings.value.themeMode)

    val reloadedSystem = SettingsManager(context)
    assertEquals(com.example.model.AppThemeMode.SYSTEM, reloadedSystem.settings.value.themeMode)

    settingsManager.setThemeMode(com.example.model.AppThemeMode.LIGHT)
    assertEquals(com.example.model.AppThemeMode.LIGHT, settingsManager.settings.value.themeMode)
  }

  @Test
  fun `gst calculator computes forward and reverse gst correctly`() {
    // 1. Forward +18% on 1000: Base = 1000, CGST = 90, SGST = 90, GST = 180, Total = 1180
    val plus18 = com.example.calculator.GstCalculator.calculate(1000.0, 18.0, isDeduction = false)
    assertEquals(1000.0, plus18.baseAmount, 0.001)
    assertEquals(90.0, plus18.cgstAmount, 0.001)
    assertEquals(90.0, plus18.sgstAmount, 0.001)
    assertEquals(180.0, plus18.gstAmount, 0.001)
    assertEquals(1180.0, plus18.itemTotal, 0.001)

    // 2. Reverse -18% on 1180: Base = 1000, CGST = 90, SGST = 90, GST = 180, Total = 1000
    val minus18From1180 = com.example.calculator.GstCalculator.calculate(1180.0, 18.0, isDeduction = true)
    assertEquals(1000.0, minus18From1180.baseAmount, 0.001)
    assertEquals(90.0, minus18From1180.cgstAmount, 0.001)
    assertEquals(90.0, minus18From1180.sgstAmount, 0.001)
    assertEquals(180.0, minus18From1180.gstAmount, 0.001)
    assertEquals(1000.0, minus18From1180.itemTotal, 0.001)

    // 3. Reverse -18% on 1000: Base = 847.4576, GST = 152.5423, Total = 847.4576
    val minus18From1000 = com.example.calculator.GstCalculator.calculate(1000.0, 18.0, isDeduction = true)
    assertEquals(847.4576, minus18From1000.baseAmount, 0.01)
    assertEquals(152.5424, minus18From1000.gstAmount, 0.01)
    assertEquals(76.2712, minus18From1000.cgstAmount, 0.01)
    assertEquals(76.2712, minus18From1000.sgstAmount, 0.01)
    assertEquals(847.4576, minus18From1000.itemTotal, 0.01)

    // 4. Reverse -5% on 1050: Base = 1000, GST = 50, Total = 1000
    val minus5 = com.example.calculator.GstCalculator.calculate(1050.0, 5.0, isDeduction = true)
    assertEquals(1000.0, minus5.baseAmount, 0.001)
    assertEquals(25.0, minus5.cgstAmount, 0.001)
    assertEquals(25.0, minus5.sgstAmount, 0.001)
    assertEquals(50.0, minus5.gstAmount, 0.001)
    assertEquals(1000.0, minus5.itemTotal, 0.001)

    // 5. Reverse -40% on 1400: Base = 1000, GST = 400, Total = 1000
    val minus40 = com.example.calculator.GstCalculator.calculate(1400.0, 40.0, isDeduction = true)
    assertEquals(1000.0, minus40.baseAmount, 0.001)
    assertEquals(200.0, minus40.cgstAmount, 0.001)
    assertEquals(200.0, minus40.sgstAmount, 0.001)
    assertEquals(400.0, minus40.gstAmount, 0.001)
    assertEquals(1000.0, minus40.itemTotal, 0.001)
  }
}
