package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val passwordHash: String,
    val balance: Double = 10.0, // $10 signup bonus default
    val totalInvested: Double = 0.0,
    val referralEarnings: Double = 0.0,
    val myReferralCode: String,
    val referredByCode: String? = null,
    val role: String = "USER", // "USER" or "ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)
