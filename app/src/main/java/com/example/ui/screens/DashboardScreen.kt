package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.data.local.InvestmentEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.PortfolioGrowthChart
import com.example.ui.components.TransactionHistorySection
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    currentUser: UserEntity?,
    userTransactions: List<TransactionEntity>,
    userInvestments: List<InvestmentEntity>,
    onOpenDepositModal: () -> Unit,
    onNavigateToTiers: () -> Unit,
    onNavigateToAffiliate: () -> Unit,
    onOpenAuthModal: () -> Unit,
    onOpenChatModal: () -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 24.dp)
        ) {
            if (currentUser == null) {
                // Not Logged In View
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    highlightGold = true
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = GoldPrimary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "ACCOUNT DASHBOARD",
                            color = GoldPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Please log in or register to access your wallet, deposit funds, and view active investments.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onOpenAuthModal,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(48.dp)
                        ) {
                            Text("LOG IN / REGISTER NOW", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Logged In Dashboard Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "USER DASHBOARD", color = GoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = currentUser.email, color = TextSecondary, fontSize = 12.sp)
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.Logout, contentDescription = "Logout", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // WALLET STATS GRID
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            highlightGold = true
        ) {
            Text(
                text = "WALLET OVERVIEW",
                color = GoldPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Main Balance Highlight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "AVAILABLE BALANCE", color = TextSecondary, fontSize = 11.sp)
                    Text(
                        text = "$${String.format("%,.2f", currentUser.balance)}",
                        color = GoldLight,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Button(
                    onClick = onOpenDepositModal,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary, contentColor = DarkBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Deposit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DEPOSIT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = DarkSurfaceBorder)
            Spacer(modifier = Modifier.height(16.dp))

            // Sub Stats (Active Investment & Referral Earnings)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = "Invested", tint = EmeraldSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Active Investments", color = TextSecondary, fontSize = 11.sp)
                    }
                    Text(
                        text = "$${String.format("%,.2f", currentUser.totalInvested)}",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, contentDescription = "Referral", tint = GoldPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Referral Earnings", color = TextSecondary, fontSize = 11.sp)
                    }
                    Text(
                        text = "$${String.format("%,.2f", currentUser.referralEarnings)}",
                        color = EmeraldSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToTiers,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.RocketLaunch, contentDescription = "Invest", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Invest Tiers", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onNavigateToAffiliate,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldSecondary),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldSecondary)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Affiliate", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Affiliate Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // REAL-TIME PORTFOLIO HISTORICAL GROWTH VISUALIZATION CHART
        PortfolioGrowthChart()

        Spacer(modifier = Modifier.height(20.dp))

        // ACTIVE INVESTMENTS LIST
        Text(text = "ACTIVE PORTFOLIOS", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (userInvestments.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No active investment plans yet. Visit Investment Tiers to start earning up to 6.5% daily ROI!",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                userInvestments.forEach { inv ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = inv.tierName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Invested: $$inv.investedAmount", color = TextSecondary, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "${inv.dailyRoiPercent}% Daily", color = EmeraldSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = inv.status, color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // DETAILED TRANSACTION HISTORY COMPONENT (WITH FILTERS, SEARCH & DETAIL MODAL)
        TransactionHistorySection(transactions = userTransactions)
    } // Close else block
    } // Close Column

    // Live Support Chat Floating Action Button (FAB)
    if (currentUser != null) {
        FloatingActionButton(
            onClick = onOpenChatModal,
            containerColor = GoldPrimary,
            contentColor = DarkBackground,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .border(1.dp, GoldLight, CircleShape)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.HeadsetMic,
                    contentDescription = "Live Support",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LIVE CHAT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
}
