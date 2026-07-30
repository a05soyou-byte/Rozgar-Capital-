package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppConfigEntity
import com.example.data.local.SupportMessageEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun AdminPanelScreen(
    currentUser: UserEntity?,
    pendingDeposits: List<TransactionEntity>,
    allTransactions: List<TransactionEntity>,
    allUsers: List<UserEntity>,
    appConfig: AppConfigEntity,
    allChatMessages: List<SupportMessageEntity> = emptyList(),
    onAdminLogin: (email: String, pass: String) -> Unit,
    onApproveDeposit: (txId: Long) -> Unit,
    onRejectDeposit: (txId: Long) -> Unit,
    onUpdateConfig: (
        pkrRate: Double,
        usdtAddr: String,
        easyTitle: String,
        easyNum: String,
        jazzTitle: String,
        jazzNum: String,
        bankTitle: String,
        bankName: String,
        bankIban: String
    ) -> Unit,
    onAdjustBalance: (userId: String, amount: Double, note: String) -> Unit = { _, _, _ -> },
    onSendAdminReply: (userUid: String, msg: String) -> Unit = { _, _ -> }
) {
    var adminEmailInput by remember { mutableStateOf("admin@rozgar.com") }
    var adminPassInput by remember { mutableStateOf("admin123") }

    // Live Config Edit States
    var pkrRateInput by remember { mutableStateOf(appConfig.pkrExchangeRate.toInt().toString()) }
    var usdtAddrInput by remember { mutableStateOf(appConfig.usdtAddress) }
    var easyTitleInput by remember { mutableStateOf(appConfig.easypaisaTitle) }
    var easyNumInput by remember { mutableStateOf(appConfig.easypaisaNumber) }
    var jazzTitleInput by remember { mutableStateOf(appConfig.jazzcashTitle) }
    var jazzNumInput by remember { mutableStateOf(appConfig.jazzcashNumber) }
    var bankTitleInput by remember { mutableStateOf(appConfig.bankTitle) }
    var bankNameInput by remember { mutableStateOf(appConfig.bankName) }
    var bankIbanInput by remember { mutableStateOf(appConfig.bankIban) }

    // Balance adjustment states
    var selectedUserForAdjustment by remember { mutableStateOf<UserEntity?>(null) }
    var adjustAmountInput by remember { mutableStateOf("") }
    var adjustNoteInput by remember { mutableStateOf("Admin Bonus Credit") }
    var isCreditMode by remember { mutableStateOf(true) }

    // Support Reply State
    var selectedChatUserUid by remember { mutableStateOf("") }
    var adminReplyMessage by remember { mutableStateOf("") }

    LaunchedEffect(appConfig) {
        pkrRateInput = appConfig.pkrExchangeRate.toInt().toString()
        usdtAddrInput = appConfig.usdtAddress
        easyTitleInput = appConfig.easypaisaTitle
        easyNumInput = appConfig.easypaisaNumber
        jazzTitleInput = appConfig.jazzcashTitle
        jazzNumInput = appConfig.jazzcashNumber
        bankTitleInput = appConfig.bankTitle
        bankNameInput = appConfig.bankName
        bankIbanInput = appConfig.bankIban
    }

    val context = LocalContext.current
    val isAdminLoggedIn = currentUser != null && currentUser.role == "ADMIN"

    // Real-time Platform Financial Analytics Calculations
    val totalSystemBalanceUsd = allUsers.sumOf { it.balance }
    val totalSystemBalancePkr = totalSystemBalanceUsd * appConfig.pkrExchangeRate

    val approvedDeposits = allTransactions.filter { it.type == "DEPOSIT" && it.status == "APPROVED" }
    val totalIngoingUsd = approvedDeposits.sumOf { it.amount }
    val totalIngoingPkr = totalIngoingUsd * appConfig.pkrExchangeRate

    val approvedWithdrawals = allTransactions.filter { it.type == "WITHDRAWAL" && it.status == "APPROVED" }
    val totalOutgoingUsd = approvedWithdrawals.sumOf { it.amount }
    val totalOutgoingPkr = totalOutgoingUsd * appConfig.pkrExchangeRate

    val activeInvestmentsUsd = allTransactions.filter { it.type == "INVESTMENT" && it.status == "APPROVED" }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 24.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "FULL ADMIN CONTROL PANEL", color = GoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Platform Settings, Payment Gateways & Global Balances", color = TextSecondary, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .background(if (isAdminLoggedIn) EmeraldDark else ErrorRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isAdminLoggedIn) "ADMIN UNLOCKED" else "LOCKED",
                    color = if (isAdminLoggedIn) EmeraldSecondary else ErrorRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isAdminLoggedIn) {
            // ADMIN LOGIN FORM
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                highlightGold = true
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = GoldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ADMIN AUTHENTICATION", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = adminEmailInput,
                    onValueChange = { adminEmailInput = it },
                    label = { Text("Admin Email", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = adminPassInput,
                    onValueChange = { adminPassInput = it },
                    label = { Text("Admin Password", color = TextSecondary) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (adminEmailInput.isBlank() || adminPassInput.isBlank()) {
                            Toast.makeText(context, "Enter admin credentials", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onAdminLogin(adminEmailInput, adminPassInput)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("UNLOCK ADMIN DASHBOARD", fontWeight = FontWeight.Bold)
                }
            }
            return
        }

        // 1. FINANCIAL METRICS DASHBOARD SUMMARY (TOTAL BALANCE, INCOMING, OUTGOING)
        Text(text = "PLATFORM FINANCIAL OVERVIEW", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "Real-time ledger audit across all registered accounts", color = TextSecondary, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(10.dp))

        // Total System Balance Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            highlightGold = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("TOTAL SYSTEM BALANCE", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$${String.format("%,.2f", totalSystemBalanceUsd)} USD",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Rs. ${String.format("%,.0f", totalSystemBalancePkr)} PKR",
                        color = GoldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(GoldPrimary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Balance", tint = GoldPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ingoing (Deposits) & Outgoing (Withdrawals) Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // INGOING DEPOSITS CARD
            GlassCard(modifier = Modifier.weight(1f)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Ingoing", tint = EmeraldSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TOTAL INGOING (DEPOSITS)", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$${String.format("%,.0f", totalIngoingUsd)} USD",
                        color = EmeraldSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rs. ${String.format("%,.0f", totalIngoingPkr)} PKR",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // OUTGOING WITHDRAWALS CARD
            GlassCard(modifier = Modifier.weight(1f)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Outgoing", tint = ErrorRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TOTAL OUTGOING (PAYOUTS)", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$${String.format("%,.0f", totalOutgoingUsd)} USD",
                        color = ErrorRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rs. ${String.format("%,.0f", totalOutgoingPkr)} PKR",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GlassCard(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = "Invested", tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("ACTIVE INVESTMENTS", color = TextSecondary, fontSize = 10.sp)
                        Text("$${String.format("%,.0f", activeInvestmentsUsd)} USD", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            GlassCard(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, contentDescription = "Users", tint = EmeraldSecondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("REGISTERED USERS", color = TextSecondary, fontSize = 10.sp)
                        Text("${allUsers.size} Users", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. PENDING DEPOSITS APPROVAL QUEUE
        Text(text = "PENDING DEPOSITS APPROVAL QUEUE", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "Approving a deposit credits user balance and executes 2-tier referral reward payout.", color = TextSecondary, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(10.dp))

        if (pendingDeposits.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "✓ No pending deposits awaiting verification.", color = EmeraldSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pendingDeposits.forEach { tx ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = tx.userEmail, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Method: ${tx.paymentMethod}", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "$${tx.amount} USD", color = EmeraldSecondary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Rs. ${String.format("%,.0f", tx.amountPkr)} PKR", color = TextSecondary, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = DarkSurfaceBorder)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "12-Digit TID: ${tx.tid}", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Proof: ${tx.proofUri.ifEmpty { "deposit_proof_attached.png" }}", color = TextSecondary, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // APPROVE / REJECT ACTION BUTTONS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onApproveDeposit(tx.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary, contentColor = DarkBackground),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("APPROVE & CREDIT", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { onRejectDeposit(tx.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ErrorRed)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Reject", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("REJECT", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. PAYMENT GATEWAY & ACCOUNT SETTINGS CONTROL (JAZZCASH, EASYPAISA, BANK, USDT)
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = GoldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "PAYMENT ACCOUNTS & SETTINGS CONTROL", color = GoldPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = "Modify JazzCash, EasyPaisa, Bank IBAN, USDT Address, and Exchange Rate", color = TextSecondary, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(14.dp))

            // USD / PKR Live Exchange Rate Edit
            OutlinedTextField(
                value = pkrRateInput,
                onValueChange = { pkrRateInput = it },
                label = { Text("Live Exchange Rate (1 USD = ? PKR)", color = TextSecondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = DarkSurfaceBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // JAZZCASH SETTINGS EDIT
            Text(text = "JAZZCASH ACCOUNT SETTINGS", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = jazzTitleInput,
                    onValueChange = { jazzTitleInput = it },
                    label = { Text("JazzCash Account Title", color = TextSecondary, fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = jazzNumInput,
                    onValueChange = { jazzNumInput = it },
                    label = { Text("JazzCash Mobile Number", color = TextSecondary, fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // EASYPAISA SETTINGS EDIT
            Text(text = "EASYPAISA ACCOUNT SETTINGS", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = easyTitleInput,
                    onValueChange = { easyTitleInput = it },
                    label = { Text("EasyPaisa Account Title", color = TextSecondary, fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = easyNumInput,
                    onValueChange = { easyNumInput = it },
                    label = { Text("EasyPaisa Mobile Number", color = TextSecondary, fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // BANK TRANSFER SETTINGS EDIT
            Text(text = "BANK TRANSFER SETTINGS", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = bankNameInput,
                    onValueChange = { bankNameInput = it },
                    label = { Text("Bank Name", color = TextSecondary, fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = bankTitleInput,
                    onValueChange = { bankTitleInput = it },
                    label = { Text("Account Title", color = TextSecondary, fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = bankIbanInput,
                onValueChange = { bankIbanInput = it },
                label = { Text("Bank IBAN Number", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // USDT WALLET ADDRESS EDIT
            Text(text = "USDT (TRC20) WALLET ADDRESS", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = usdtAddrInput,
                onValueChange = { usdtAddrInput = it },
                label = { Text("USDT Receiving Wallet Address", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val rate = pkrRateInput.toDoubleOrNull() ?: 285.0
                    onUpdateConfig(
                        rate, usdtAddrInput, easyTitleInput, easyNumInput,
                        jazzTitleInput, jazzNumInput, bankTitleInput, bankNameInput, bankIbanInput
                    )
                    Toast.makeText(context, "All Payment & Platform Settings Saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SAVE ALL PLATFORM SETTINGS", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. USER MANAGEMENT & DIRECT BALANCE ADJUSTMENT (FULL APP CONTROL)
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ManageAccounts, contentDescription = "User Control", tint = GoldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "USER MANAGEMENT & BALANCE ADJUSTMENT", color = GoldPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = "Directly credit bonuses, debit funds, or manage user account details", color = TextSecondary, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(14.dp))

            // User Selection Dropdown / Selector
            Text("SELECT TARGET USER", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                allUsers.forEach { user ->
                    val isSelected = selectedUserForAdjustment?.uid == user.uid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isSelected) GoldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) GoldPrimary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedUserForAdjustment = user }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = user.email, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Role: ${user.role} • Ref Code: ${user.myReferralCode}", color = TextSecondary, fontSize = 10.sp)
                        }
                        Text(
                            text = "$${String.format("%.2f", user.balance)}",
                            color = GoldLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedUserForAdjustment != null) {
                Text(
                    text = "ADJUST BALANCE FOR ${selectedUserForAdjustment?.email}",
                    color = EmeraldSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Credit vs Debit Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isCreditMode) EmeraldSecondary else DarkSurfaceVariant, RoundedCornerShape(8.dp))
                            .clickable { isCreditMode = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CREDIT (+ BONUS)", color = if (isCreditMode) DarkBackground else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (!isCreditMode) ErrorRed else DarkSurfaceVariant, RoundedCornerShape(8.dp))
                            .clickable { isCreditMode = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("DEBIT (- DEDUCTION)", color = if (!isCreditMode) Color.White else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = adjustAmountInput,
                    onValueChange = { adjustAmountInput = it },
                    label = { Text("Amount (USD $)", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = adjustNoteInput,
                    onValueChange = { adjustNoteInput = it },
                    label = { Text("Reason / Transaction Note", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val parsedAmt = adjustAmountInput.toDoubleOrNull()
                        if (parsedAmt == null || parsedAmt <= 0) {
                            Toast.makeText(context, "Enter a valid positive amount", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val finalAmt = if (isCreditMode) parsedAmt else -parsedAmt
                        onAdjustBalance(selectedUserForAdjustment!!.uid, finalAmt, adjustNoteInput)
                        adjustAmountInput = ""
                        Toast.makeText(context, "Balance adjusted!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCreditMode) EmeraldSecondary else ErrorRed,
                        contentColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isCreditMode) "CONFIRM CREDIT BALANCE" else "CONFIRM DEBIT DEDUCTION",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text("Select a user from the list above to modify balance.", color = TextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. LIVE ADMIN SUPPORT CHAT CONCIERGE
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SupportAgent, contentDescription = "Support", tint = GoldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "LIVE ADMIN SUPPORT CHAT CONCIERGE", color = GoldPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = "Reply to investor tickets and inquiry messages", color = TextSecondary, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(12.dp))

            if (allChatMessages.isEmpty()) {
                Text("No support tickets recorded.", color = TextSecondary, fontSize = 12.sp)
            } else {
                val uniqueUsers = allChatMessages.map { it.userUid }.distinct()

                if (selectedChatUserUid.isEmpty() && uniqueUsers.isNotEmpty()) {
                    selectedChatUserUid = uniqueUsers.first()
                }

                Text("ACTIVE TICKET THREADS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    uniqueUsers.forEach { uId ->
                        val isSel = selectedChatUserUid == uId
                        Box(
                            modifier = Modifier
                                .background(if (isSel) GoldPrimary else DarkSurfaceVariant, RoundedCornerShape(16.dp))
                                .clickable { selectedChatUserUid = uId }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "User ${uId.take(6)}...",
                                color = if (isSel) DarkBackground else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val threadMessages = allChatMessages.filter { it.userUid == selectedChatUserUid }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    threadMessages.forEach { msg ->
                        Column {
                            Text(
                                text = "${msg.senderName} (${msg.senderType}):",
                                color = if (msg.senderType == "ADMIN") GoldPrimary else EmeraldSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = msg.message, color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = adminReplyMessage,
                        onValueChange = { adminReplyMessage = it },
                        placeholder = { Text("Type admin reply...", color = TextSecondary, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = DarkSurfaceBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            if (adminReplyMessage.isNotBlank() && selectedChatUserUid.isNotEmpty()) {
                                onSendAdminReply(selectedChatUserUid, adminReplyMessage)
                                adminReplyMessage = ""
                                Toast.makeText(context, "Reply sent!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("REPLY", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
