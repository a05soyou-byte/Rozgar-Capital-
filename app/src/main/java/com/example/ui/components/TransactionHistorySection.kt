package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.TransactionEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionHistorySection(
    transactions: List<TransactionEntity>,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDetailTx by remember { mutableStateOf<TransactionEntity?>(null) }

    // Summary calculation
    val totalDeposits = remember(transactions) {
        transactions.filter { it.type == "DEPOSIT" && it.status == "APPROVED" }.sumOf { it.amount }
    }
    val totalEarnings = remember(transactions) {
        transactions.filter { (it.type.contains("REFERRAL") || it.type == "SIGNUP_BONUS" || it.type == "DAILY_ROI") && it.status == "APPROVED" }.sumOf { it.amount }
    }
    val totalWithdrawals = remember(transactions) {
        transactions.filter { it.type == "WITHDRAWAL" && it.status == "APPROVED" }.sumOf { it.amount }
    }
    val totalInvested = remember(transactions) {
        transactions.filter { it.type == "INVESTMENT" && it.status == "APPROVED" }.sumOf { it.amount }
    }

    // Filtered transaction list
    val filteredTransactions = remember(transactions, selectedCategory, selectedStatusFilter, searchQuery) {
        transactions.filter { tx ->
            val matchesCategory = when (selectedCategory) {
                "DEPOSITS" -> tx.type == "DEPOSIT"
                "EARNINGS" -> tx.type == "DAILY_ROI" || tx.type == "SIGNUP_BONUS" || tx.type.contains("REFERRAL")
                "WITHDRAWALS" -> tx.type == "WITHDRAWAL"
                "INVESTMENTS" -> tx.type == "INVESTMENT"
                else -> true
            }

            val matchesStatus = when (selectedStatusFilter) {
                "APPROVED" -> tx.status == "APPROVED"
                "PENDING" -> tx.status == "PENDING"
                "REJECTED" -> tx.status == "REJECTED"
                else -> true
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                tx.tid.contains(searchQuery, ignoreCase = true) ||
                        tx.note.contains(searchQuery, ignoreCase = true) ||
                        tx.paymentMethod.contains(searchQuery, ignoreCase = true) ||
                        tx.type.contains(searchQuery, ignoreCase = true)
            }

            matchesCategory && matchesStatus && matchesSearch
        }.sortedByDescending { it.timestamp }
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        highlightGold = true
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(GoldPrimary.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = "Transaction History",
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "TRANSACTION HISTORY",
                        color = GoldPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Audited ledger of deposits, earnings & payouts",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Total count pill
            Box(
                modifier = Modifier
                    .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                    .border(0.5.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${filteredTransactions.size} Records",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Financial Metrics Overview Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricMiniCard(
                label = "Deposits",
                amount = "$${String.format("%.2f", totalDeposits)}",
                color = EmeraldSecondary,
                modifier = Modifier.weight(1f)
            )
            MetricMiniCard(
                label = "Earnings",
                amount = "$${String.format("%.2f", totalEarnings)}",
                color = GoldPrimary,
                modifier = Modifier.weight(1f)
            )
            MetricMiniCard(
                label = "Payouts",
                amount = "$${String.format("%.2f", totalWithdrawals)}",
                color = Color(0xFFF43F5E),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by TID, notes, or method...", color = TextSecondary, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldPrimary, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = DarkSurfaceBorder,
                focusedContainerColor = Color(0xFF020617).copy(alpha = 0.8f),
                unfocusedContainerColor = Color(0xFF020617).copy(alpha = 0.6f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val categories = listOf("ALL", "DEPOSITS", "EARNINGS", "WITHDRAWALS", "INVESTMENTS")
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) GoldPrimary else DarkSurfaceVariant
                        )
                        .border(
                            width = 0.5.dp,
                            color = if (isSelected) GoldLight else DarkSurfaceBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) DarkBackground else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Secondary Status Sub-Filters (ALL / APPROVED / PENDING / REJECTED)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Status:", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            listOf("ALL", "APPROVED", "PENDING", "REJECTED").forEach { status ->
                val isSelected = selectedStatusFilter == status
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent
                        )
                        .clickable { selectedStatusFilter = status }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = status,
                        color = if (isSelected) GoldPrimary else TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable Clean Transaction List
        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FilterListOff,
                        contentDescription = "No Transactions",
                        tint = TextSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No matching transactions found.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredTransactions.forEach { tx ->
                    TransactionItemRow(
                        transaction = tx,
                        onClick = { selectedDetailTx = tx }
                    )
                }
            }
        }
    }

    // Detail Modal Dialog when user clicks a transaction
    selectedDetailTx?.let { tx ->
        TransactionDetailDialog(
            transaction = tx,
            onDismiss = { selectedDetailTx = null }
        )
    }
}

@Composable
private fun MetricMiniCard(
    label: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF020617).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .border(0.5.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Column {
            Text(text = label, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = amount, color = color, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun TransactionItemRow(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }
    val dateStr = remember(transaction.timestamp) { dateFormatter.format(Date(transaction.timestamp)) }

    val (icon, iconColor, typeTitle, amountPrefix, isPositive) = remember(transaction.type) {
        when {
            transaction.type == "DEPOSIT" -> Tuple5(Icons.Default.ArrowDownward, EmeraldSecondary, "Deposit", "+$", true)
            transaction.type == "WITHDRAWAL" -> Tuple5(Icons.Default.ArrowUpward, Color(0xFFF43F5E), "Withdrawal", "-$", false)
            transaction.type == "INVESTMENT" -> Tuple5(Icons.Default.AccountBalance, Color(0xFFA855F7), "Asset Investment", "-$", false)
            transaction.type == "DAILY_ROI" -> Tuple5(Icons.Default.TrendingUp, GoldPrimary, "Daily ROI Profit", "+$", true)
            transaction.type.contains("REFERRAL") -> Tuple5(Icons.Default.Group, GoldLight, "Referral Commission", "+$", true)
            transaction.type == "SIGNUP_BONUS" -> Tuple5(Icons.Default.CardGiftcard, GoldPrimary, "Signup Reward", "+$", true)
            else -> Tuple5(Icons.Default.Receipt, TextSecondary, transaction.type, "$", true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF020617).copy(alpha = 0.7f))
            .border(0.5.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon pill
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(iconColor.copy(alpha = 0.15f), CircleShape)
                        .border(0.5.dp, iconColor.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = typeTitle,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = typeTitle,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (transaction.tid.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• #${transaction.tid.takeLast(6)}",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = transaction.note.ifEmpty { dateStr },
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )

                    Text(
                        text = dateStr,
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount and Status Tag
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$amountPrefix${String.format("%.2f", transaction.amount)}",
                    color = if (isPositive) EmeraldSecondary else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Status Chip
                val (statusBg, statusFg) = when (transaction.status) {
                    "APPROVED" -> EmeraldSecondary.copy(alpha = 0.2f) to EmeraldSecondary
                    "PENDING" -> GoldPrimary.copy(alpha = 0.2f) to GoldPrimary
                    else -> Color(0xFFF43F5E).copy(alpha = 0.2f) to Color(0xFFF43F5E)
                }

                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = transaction.status,
                        color = statusFg,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailDialog(
    transaction: TransactionEntity,
    onDismiss: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault()) }
    val dateStr = remember(transaction.timestamp) { dateFormatter.format(Date(transaction.timestamp)) }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            highlightGold = true
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSACTION DETAILS",
                        color = GoldPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Divider(color = DarkSurfaceBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                DetailRow(label = "Type", value = transaction.type)
                DetailRow(label = "Amount (USD)", value = "$${String.format("%.2f", transaction.amount)}")
                if (transaction.amountPkr > 0) {
                    DetailRow(label = "Amount (PKR)", value = "Rs. ${String.format("%.0f", transaction.amountPkr)}")
                }
                DetailRow(label = "Status", value = transaction.status)
                if (transaction.tid.isNotBlank()) {
                    DetailRow(label = "TID / Ref", value = transaction.tid)
                }
                if (transaction.paymentMethod.isNotBlank()) {
                    DetailRow(label = "Payment Method", value = transaction.paymentMethod)
                }
                DetailRow(label = "Timestamp", value = dateStr)
                if (transaction.note.isNotBlank()) {
                    DetailRow(label = "Description", value = transaction.note)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("CLOSE RECEIPT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

private data class Tuple5<A, B, C, D, E>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E
)
