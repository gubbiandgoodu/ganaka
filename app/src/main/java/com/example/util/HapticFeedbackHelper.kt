package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Helper to produce crisp, low-latency tactile haptic feedback for calculator keypad interactions.
 */
object HapticFeedbackHelper {

    fun performTactileFeedback(view: View, context: Context, enabled: Boolean = true) {
        if (!enabled) return

        try {
            // First attempt native view haptic feedback for low-latency system integration
            val flags = HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            val feedbackType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                HapticFeedbackConstants.KEYBOARD_PRESS
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                HapticFeedbackConstants.CONFIRM
            } else {
                HapticFeedbackConstants.KEYBOARD_TAP
            }

            val performed = try {
                view.performHapticFeedback(feedbackType, flags)
            } catch (_: Exception) {
                view.performHapticFeedback(feedbackType)
            }

            // If the view didn't trigger haptics (e.g. device global setting disabled or OEM quirk),
            // fallback directly to the device vibrator for guaranteed physical tactile feedback.
            if (!performed) {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    manager?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }

                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(18)
                    }
                }
            }
        } catch (_: Exception) {
            // Gracefully ignore if hardware vibration is unavailable
        }
    }
}
