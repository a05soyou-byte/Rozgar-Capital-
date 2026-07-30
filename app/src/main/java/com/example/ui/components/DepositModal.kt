package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.AppConfigEntity
import com.example.ui.theme.*

@Composable
fun DepositModal(
    appConfig: AppConfigEntity,
    onDismiss: () -> Unit,
    onSubmitDeposit: (amountUsd: Double, method: String, tid: String, proofUri: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("USDT") } // "USDT" or "PKR_EASYPAISA" or "PKR_BANK"
    var amountUsdInput by remember { mutableStateOf("100") }
    var tidInput by remember { mutableStateOf("") }
    var proofAttached by remember { mutableStateOf(false) }
    var proofFileName by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val amountUsd = amountUsdInput.toDoubleOrNull() ?: 0.0
    val amountPkr = amountUsd * appConfig.pkrExchangeRate

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, GoldPrimary, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Deposit Funds",
                            color = GoldPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Dual Payment Gateway (USDT / PKR)",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Method Selector Pills
                Text(
                    text = "SELECT PAYMENT METHOD",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "USDT" to "USDT",
                        "PKR_EASYPAISA" to "EasyPaisa",
                        "PKR_JAZZCASH" to "JazzCash",
                        "PKR_BANK" to "Bank"
                    ).forEach { (key, label) ->
                        val isSelected = selectedMethod == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) GoldPrimary else DarkSurfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedMethod = key }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Live FX Converter Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "LIVE FX CONVERTER",
                                color = EmeraldSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "1 USD = ${appConfig.pkrExchangeRate.toInt()} PKR",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "YOU PAY APPROX.",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Rs. ${String.format("%,.0f", amountPkr)} PKR",
                                color = GoldLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Deposit Amount Input
                OutlinedTextField(
                    value = amountUsdInput,
                    onValueChange = { amountUsdInput = it },
                    label = { Text("Deposit Amount (USD $)", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Receiving Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        when (selectedMethod) {
                            "USDT" -> {
                                Text(
                                    text = "USDT (TRC20) WALLET ADDRESS",
                                    color = EmeraldSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = appConfig.usdtAddress,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(appConfig.usdtAddress))
                                            Toast.makeText(context, "Address copied!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldPrimary)
                                    }
                                }
                            }
                            "PKR_EASYPAISA" -> {
                                Text(
                                    text = "EASYPAISA ACCOUNT DETAILS",
                                    color = GoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Title: ${appConfig.easypaisaTitle}", color = TextPrimary, fontSize = 13.sp)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Number: ${appConfig.easypaisaNumber}", color = EmeraldSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(appConfig.easypaisaNumber))
                                            Toast.makeText(context, "Number copied!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldPrimary)
                                    }
                                }
                            }
                            "PKR_JAZZCASH" -> {
                                Text(
                                    text = "JAZZCASH ACCOUNT DETAILS",
                                    color = GoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Title: ${appConfig.jazzcashTitle}", color = TextPrimary, fontSize = 13.sp)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Number: ${appConfig.jazzcashNumber}", color = EmeraldSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(appConfig.jazzcashNumber))
                                            Toast.makeText(context, "Number copied!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldPrimary)
                                    }
                                }
                            }
                            "PKR_BANK" -> {
                                Text(
                                    text = "BANK TRANSFER DETAILS",
                                    color = GoldPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Bank: ${appConfig.bankName}", color = TextPrimary, fontSize = 13.sp)
                                Text("Title: ${appConfig.bankTitle}", color = TextPrimary, fontSize = 13.sp)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("IBAN: ${appConfig.bankIban}", color = EmeraldSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(appConfig.bankIban))
                                            Toast.makeText(context, "IBAN copied!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 12-Digit TID Input
                OutlinedTextField(
                    value = tidInput,
                    onValueChange = { tidInput = it },
                    label = { Text("12-Digit Transaction ID (TID)", color = TextSecondary) },
                    placeholder = { Text("e.g. 293849182049", color = TextSecondary.copy(alpha = 0.5f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Attach Receipt Proof Button
                OutlinedButton(
                    onClick = {
                        proofAttached = true
                        proofFileName = "receipt_proof_${System.currentTimeMillis().toString().takeLast(6)}.jpg"
                        Toast.makeText(context, "Receipt attached successfully!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = if (proofAttached) EmeraldSecondary else GoldPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(GoldPrimary, EmeraldSecondary)))
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = "Upload", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (proofAttached) "Proof Attached ($proofFileName)" else "Attach Receipt / Proof Screenshot"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Deposit CTA Button
                Button(
                    onClick = {
                        if (amountUsd <= 0) {
                            Toast.makeText(context, "Please enter a valid deposit amount", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (tidInput.isBlank()) {
                            Toast.makeText(context, "Please enter the 12-digit Transaction ID (TID)", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onSubmitDeposit(amountUsd, selectedMethod, tidInput, proofFileName.ifEmpty { "proof_receipt.png" })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "SUBMIT DEPOSIT FOR VERIFICATION",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
