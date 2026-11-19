package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculations")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mode: String, // "MASTER" or "GST"
    val expression: String,
    val result: Double,
    val baseAmount: Double = 0.0,
    val taxRate: Double = 0.0,
    val cgstAmount: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val gstAmount: Double = 0.0,
    val itemTotal: Double = 0.0,
    val isDeduction: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
