package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey val id: Int = 1,
    val pkrExchangeRate: Double = 285.0,
    val usdtAddress: String = "TYu67Xqp98zKLmNoPqRstUVwXYZ1234567",
    val easypaisaTitle: String = "Rozgar Capital Official",
    val easypaisaNumber: String = "0300-1234567",
    val jazzcashTitle: String = "Rozgar Capital Official",
    val jazzcashNumber: String = "0303-9876543",
    val bankTitle: String = "Rozgar Capital Pvt Ltd",
    val bankName: String = "Meezan Bank",
    val bankIban: String = "PK36MEZN0001234567890123"
)
