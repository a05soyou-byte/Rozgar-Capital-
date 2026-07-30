package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val tierName: String,
    val investedAmount: Double,
    val dailyRoiPercent: Double,
    val durationDays: Int,
    val startDate: Long = System.currentTimeMillis(),
    val accumulatedProfit: Double = 0.0,
    val status: String = "ACTIVE" // "ACTIVE", "COMPLETED"
)
