package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val userEmail: String,
    val type: String, // "SIGNUP_BONUS", "DEPOSIT", "INVESTMENT", "REFERRAL_TIER_1", "REFERRAL_TIER_2"
    val amount: Double,
    val amountPkr: Double = 0.0,
    val status: String, // "APPROVED", "PENDING", "REJECTED"
    val tid: String = "",
    val paymentMethod: String = "USDT", // "USDT", "EASYPAISA", "BANK"
    val proofUri: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
