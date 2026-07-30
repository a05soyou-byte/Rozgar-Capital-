package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.components.PortfolioGrowthChart
import com.example.ui.components.TrustBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(
    onNavigateToTiers: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onOpenAuthModal: () -> Unit
) {
    // Live Countdown Timer State for Viral Launch Banner
    var timeLeftSeconds by remember { mutableStateOf(86400L + 4320L) } // ~24 hours launch timer

    LaunchedEffect(Unit) {
        while (timeLeftSeconds > 0) {
            delay(1000L)
            timeLeftSeconds--
        }
    }

    val hours = (timeLeftSeconds / 3600)
    val minutes = (timeLeftSeconds % 3600) / 60
    val seconds = (timeLeftSeconds % 60)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // Top Trust Badge Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            TrustBadge()
        }

        // Viral Launch Countdown Banner (Gold Gradient - Artistic Flair Theme)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GoldPrimary, GoldLight)
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "VIRAL LAUNCH EVENT",
                            color = Color(0xFF020617).copy(alpha = 0.75f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Get Your $10 Signup Bonus",
                            color = Color(0xFF020617),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Countdown Timer Pill
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF020617), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = GoldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = String.format("%02dh : %02dm : %02ds", hours, minutes, seconds),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Hero Section Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            highlightGold = true
        ) {
            // Hero Image Backdrop
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner_1785436586102),
                    contentDescription = "Rozgar Wealth Management",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, DarkSurface)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hero Badge ($1 Minimum Investment)
            Box(
                modifier = Modifier
                    .background(GoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .border(0.5.dp, GoldPrimary, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "💎 $1 MINIMUM INVESTMENT",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rozgar Capital",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Empowering Financial Freedom with Real Asset Wealth Portfolios",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons (Get Started & Tiers)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenAuthModal,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "GET STARTED ($10 BONUS)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToTiers,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(EmeraldSecondary, GoldPrimary))),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "VIEW TIERS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // REAL-TIME PORTFOLIO HISTORICAL GROWTH VISUALIZATION
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            PortfolioGrowthChart()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Real Asset Portfolio Section Header
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "REAL ASSET PORTFOLIO",
                color = GoldPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Backed by tangible global financial market instruments",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Real Asset Portfolio Grid Cards
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PortfolioAssetItem(
                title = "Forex & Currency Arbitrage",
                category = "Foreign Exchange",
                roiText = "2.5% - 4.5% Daily ROI",
                icon = Icons.Default.CurrencyExchange,
                iconColor = GoldPrimary,
                description = "Institutional liquidity trading across major currency pairs (EUR/USD, GBP/USD, USD/PKR) with Automated Arbitrage execution."
            )

            PortfolioAssetItem(
                title = "Commercial Real Estate",
                category = "Property & Development",
                roiText = "4.0% - 6.5% Daily ROI",
                icon = Icons.Default.Apartment,
                iconColor = EmeraldSecondary,
                description = "Direct equity investment in high-yielding urban commercial developments and rental asset portfolios across Pakistan and Dubai."
            )

            PortfolioAssetItem(
                title = "Global Commodities & Gold",
                category = "Precious Metals & Crude",
                roiText = "3.0% - 5.5% Daily ROI",
                icon = Icons.Default.ShowChart,
                iconColor = GoldVariant,
                description = "Physical bullion reserves and commodity futures hedging providing long-term capital preservation against inflation."
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Why Choose Rozgar Capital Stats
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "WHY ROZGAR CAPITAL?",
                color = GoldPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatColumn(number = "100%", label = "Secured Returns")
                StatColumn(number = "$1", label = "Min Deposit")
                StatColumn(number = "5% + 2%", label = "Affiliate Bonus")
            }
        }
    }
}

@Composable
private fun PortfolioAssetItem(
    title: String,
    category: String,
    roiText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    description: String
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, iconColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = category, color = TextSecondary, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .background(EmeraldDark.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = roiText, color = EmeraldSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun StatColumn(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = number, color = GoldLight, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
    }
}
