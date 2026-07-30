package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun InvestmentTiersScreen(
    currentUser: UserEntity?,
    onInvest: (tierName: String, amount: Double, dailyRoi: Double, durationDays: Int) -> Unit,
    onOpenAuthModal: () -> Unit
) {
    var calcAmountInput by remember { mutableStateOf("500") }
    val calcAmount = calcAmountInput.toDoubleOrNull() ?: 0.0
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 24.dp)
    ) {
        // Page Title & Subtitle
        Text(
            text = "INVESTMENT TIERS",
            color = GoldPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Select a high-yield asset plan tailored for your wealth growth",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive ROI Calculator Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            highlightGold = true
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Calculate, contentDescription = "Calc", tint = GoldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LIVE INVESTMENT CALCULATOR",
                    color = GoldPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = calcAmountInput,
                onValueChange = { calcAmountInput = it },
                label = { Text("Calculate Amount ($)", color = TextSecondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val dailyAt4 = calcAmount * 0.04
                val totalAt60Days = calcAmount + (dailyAt4 * 60)

                Column {
                    Text("ESTIMATED DAILY PROFIT", color = TextSecondary, fontSize = 10.sp)
                    Text("+$${String.format("%.2f", dailyAt4)}/day", color = EmeraldSecondary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("TOTAL 60-DAY RETURN", color = TextSecondary, fontSize = 10.sp)
                    Text("$${String.format("%.2f", totalAt60Days)}", color = GoldLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TIER 1 CARD ($1 Minimum)
        TierCardItem(
            tierTitle = "Tier 1: Starter Pack",
            minInvestmentText = "$1 Minimum Investment",
            minAmountUsd = 1.0,
            dailyRoi = 2.5,
            durationDays = 30,
            badgeText = "STARTER FRIENDLY",
            badgeColor = EmeraldSecondary,
            icon = Icons.Default.Star,
            features = listOf(
                "Capital Entry: $1 - $499",
                "Daily ROI: 2.5% per day",
                "Contract Duration: 30 Days",
                "Total Return: 175% (75% Profit)",
                "Daily Profit Withdrawal Supported"
            ),
            currentUser = currentUser,
            onInvestClick = { amount ->
                if (currentUser == null) {
                    onOpenAuthModal()
                } else {
                    onInvest("Tier 1 Starter", amount, 2.5, 30)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TIER 2 CARD ($500 Minimum)
        TierCardItem(
            tierTitle = "Tier 2: Growth Portfolio",
            minInvestmentText = "$500 Minimum Investment",
            minAmountUsd = 500.0,
            dailyRoi = 4.0,
            durationDays = 60,
            badgeText = "MOST POPULAR",
            badgeColor = GoldPrimary,
            icon = Icons.Default.RocketLaunch,
            isFeatured = true,
            features = listOf(
                "Capital Entry: $500 - $1,999",
                "Daily ROI: 4.0% per day",
                "Contract Duration: 60 Days",
                "Total Return: 340% (240% Profit)",
                "Instant Auto-compounding Option",
                "VIP Support Line Access"
            ),
            currentUser = currentUser,
            onInvestClick = { amount ->
                if (currentUser == null) {
                    onOpenAuthModal()
                } else {
                    onInvest("Tier 2 Growth", amount, 4.0, 60)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TIER 3 CARD ($2000 Minimum)
        TierCardItem(
            tierTitle = "Tier 3: Elite Wealth",
            minInvestmentText = "$2,000 Minimum Investment",
            minAmountUsd = 2000.0,
            dailyRoi = 6.5,
            durationDays = 90,
            badgeText = "HIGH YIELD ELITE",
            badgeColor = Color(0xFFA855F7), // Purple Gold
            icon = Icons.Default.Diamond,
            features = listOf(
                "Capital Entry: $2,000+",
                "Daily ROI: 6.5% per day",
                "Contract Duration: 90 Days",
                "Total Return: 685% (585% Profit)",
                "Dedicated Wealth Manager",
                "Priority Deposit & Withdrawal Processing"
            ),
            currentUser = currentUser,
            onInvestClick = { amount ->
                if (currentUser == null) {
                    onOpenAuthModal()
                } else {
                    onInvest("Tier 3 Elite", amount, 6.5, 90)
                }
            }
        )
    }
}

@Composable
private fun TierCardItem(
    tierTitle: String,
    minInvestmentText: String,
    minAmountUsd: Double,
    dailyRoi: Double,
    durationDays: Int,
    badgeText: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isFeatured: Boolean = false,
    features: List<String>,
    currentUser: UserEntity?,
    onInvestClick: (amount: Double) -> Unit
) {
    var customAmountInput by remember { mutableStateOf(minAmountUsd.toInt().toString()) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        highlightGold = isFeatured
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = tierTitle, tint = badgeColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = tierTitle, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = minInvestmentText, color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Box(
                modifier = Modifier
                    .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .border(0.5.dp, badgeColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = badgeText, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Daily ROI Highlight Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "DAILY ROI", color = TextSecondary, fontSize = 10.sp)
                Text(text = "$dailyRoi%", color = EmeraldSecondary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
            Divider(modifier = Modifier.height(24.dp).width(1.dp), color = DarkSurfaceBorder)
            Column {
                Text(text = "DURATION", color = TextSecondary, fontSize = 10.sp)
                Text(text = "$durationDays Days", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Divider(modifier = Modifier.height(24.dp).width(1.dp), color = DarkSurfaceBorder)
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "TOTAL PROFIT", color = TextSecondary, fontSize = 10.sp)
                Text(text = "+${(dailyRoi * durationDays).toInt()}%", color = GoldLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Feature Bullets
        features.forEach { ft ->
            Row(
                modifier = Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Check", tint = EmeraldSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = ft, color = TextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Custom Investment Amount Field & Invest Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customAmountInput,
                onValueChange = { customAmountInput = it },
                label = { Text("Amount ($)", fontSize = 10.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = {
                    val amt = customAmountInput.toDoubleOrNull() ?: 0.0
                    if (amt < minAmountUsd) {
                        onInvestClick(minAmountUsd)
                    } else {
                        onInvestClick(amt)
                    }
                },
                modifier = Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFeatured) GoldPrimary else EmeraldSecondary,
                    contentColor = DarkBackground
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (currentUser == null) "LOG IN TO INVEST" else "INVEST NOW",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
