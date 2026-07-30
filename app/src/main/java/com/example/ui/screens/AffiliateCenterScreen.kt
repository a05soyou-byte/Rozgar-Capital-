package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun AffiliateCenterScreen(
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    onOpenAuthModal: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 24.dp)
    ) {
        // Header
        Text(text = "AFFILIATE PROGRAM", color = GoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = "Earn 2-Tier Multi-Level Commissions on Every Approved Deposit", color = TextSecondary, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        if (currentUser == null) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                highlightGold = true
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.People, contentDescription = "Affiliate", tint = GoldPrimary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Log in to view your unique referral link and network commissions", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onOpenAuthModal,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("LOG IN TO ACCESS AFFILIATE LINK", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            val myCode = currentUser.myReferralCode
            val referralLink = "https://rozgar.capital/ref?id=$myCode"

            // UNIQUE REFERRAL LINK GENERATOR CARD
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                highlightGold = true
            ) {
                Text(text = "YOUR UNIQUE REFERRAL LINK", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF020617), RoundedCornerShape(10.dp))
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = referralLink,
                        color = GoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(referralLink))
                            Toast.makeText(context, "Referral link copied!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Referral Code: $myCode", color = EmeraldSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(referralLink))
                            Toast.makeText(context, "Referral link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary, contentColor = DarkBackground),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SHARE LINK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // REFERRAL NETWORK STATS CARD
            val directReferrals = allUsers.filter { it.referredByCode == myCode }
            val directCodes = directReferrals.map { it.myReferralCode }
            val indirectReferrals = allUsers.filter { it.referredByCode != null && directCodes.contains(it.referredByCode) }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "YOUR NETWORK STATS", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TOTAL EARNINGS", color = TextSecondary, fontSize = 10.sp)
                        Text("$${String.format("%.2f", currentUser.referralEarnings)}", color = EmeraldSecondary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column {
                        Text("DIRECT (TIER 1)", color = TextSecondary, fontSize = 10.sp)
                        Text("${directReferrals.size} Members", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("INDIRECT (TIER 2)", color = TextSecondary, fontSize = 10.sp)
                        Text("${indirectReferrals.size} Members", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 2-TIER COMMISSION BREAKDOWN EXPLANATION
        Text(text = "COMMISSION STRUCTURE", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        // Tier 1 Commission Details
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(GoldPrimary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("5%", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "TIER 1: DIRECT REFERRER (5%)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Earn 5% instant commission whenever your direct invitees make a deposit.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tier 2 Commission Details
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(EmeraldSecondary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2%", color = EmeraldSecondary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "TIER 2: INDIRECT REFERRER (2%)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Earn 2% passive commission whenever sub-referrals (invited by your Tier 1 members) deposit.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}
