package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AuthDialog
import com.example.ui.components.DepositModal
import com.example.ui.components.SupportChatModal
import com.example.ui.screens.*
import com.example.ui.theme.*

@Composable
fun RozgarApp(viewModel: RozgarViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val appConfig by viewModel.appConfig.collectAsStateWithLifecycle()
    val userTransactions by viewModel.userTransactions.collectAsStateWithLifecycle()
    val userInvestments by viewModel.userInvestments.collectAsStateWithLifecycle()
    val pendingDeposits by viewModel.pendingDeposits.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()

    val isDepositModalOpen by viewModel.isDepositModalOpen.collectAsStateWithLifecycle()
    val isAuthModalOpen by viewModel.isAuthModalOpen.collectAsStateWithLifecycle()
    val isChatModalOpen by viewModel.isChatModalOpen.collectAsStateWithLifecycle()
    val userChatMessages by viewModel.userChatMessages.collectAsStateWithLifecycle()
    val allChatMessages by viewModel.allChatMessages.collectAsStateWithLifecycle()

    // Show Toast for ViewModel feedback messages
    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            // App Top Bar
            Surface(
                color = DarkSurface.copy(alpha = 0.95f),
                tonalElevation = 6.dp,
                modifier = Modifier.border(
                    width = 0.5.dp,
                    brush = Brush.horizontalGradient(listOf(GoldPrimary.copy(alpha = 0.4f), DarkSurfaceBorder)),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(GoldPrimary.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = "Logo", tint = GoldPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "ROZGAR CAPITAL", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                            Text(text = "Wealth & Asset Management", color = TextSecondary, fontSize = 10.sp)
                        }
                    }

                    // User Status / Auth Button
                    if (currentUser == null) {
                        Button(
                            onClick = { viewModel.openAuthModal() },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("LOGIN / $10 BONUS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(DarkSurfaceVariant, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(EmeraldSecondary, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$${String.format("%.2f", currentUser?.balance ?: 0.0)}",
                                color = GoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // M3 NavigationBar with Luxury Dark styling
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = TextSecondary,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .border(0.5.dp, DarkSurfaceBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                val navItems = listOf(
                    NavItem("home", "Home", Icons.Default.Home),
                    NavItem("tiers", "Tiers", Icons.Default.RocketLaunch),
                    NavItem("dashboard", "Dashboard", Icons.Default.AccountBalanceWallet),
                    NavItem("affiliate", "Affiliate", Icons.Default.People),
                    NavItem("admin", "Admin", Icons.Default.Security)
                )

                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) GoldPrimary else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = if (isSelected) GoldPrimary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = GoldPrimary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    LandingScreen(
                        onNavigateToTiers = { navController.navigate("tiers") },
                        onNavigateToDashboard = { navController.navigate("dashboard") },
                        onOpenAuthModal = { viewModel.openAuthModal() }
                    )
                }
                composable("tiers") {
                    InvestmentTiersScreen(
                        currentUser = currentUser,
                        onInvest = { tierName, amount, dailyRoi, durationDays ->
                            viewModel.investInTier(tierName, amount, dailyRoi, durationDays)
                        },
                        onOpenAuthModal = { viewModel.openAuthModal() }
                    )
                }
                composable("dashboard") {
                    DashboardScreen(
                        currentUser = currentUser,
                        userTransactions = userTransactions,
                        userInvestments = userInvestments,
                        onOpenDepositModal = { viewModel.openDepositModal() },
                        onNavigateToTiers = { navController.navigate("tiers") },
                        onNavigateToAffiliate = { navController.navigate("affiliate") },
                        onOpenAuthModal = { viewModel.openAuthModal() },
                        onOpenChatModal = { viewModel.openChatModal() },
                        onLogout = { viewModel.logout() }
                    )
                }
                composable("affiliate") {
                    AffiliateCenterScreen(
                        currentUser = currentUser,
                        allUsers = allUsers,
                        onOpenAuthModal = { viewModel.openAuthModal() }
                    )
                }
                composable("admin") {
                    AdminPanelScreen(
                        currentUser = currentUser,
                        pendingDeposits = pendingDeposits,
                        allTransactions = allTransactions,
                        allUsers = allUsers,
                        appConfig = appConfig,
                        allChatMessages = allChatMessages,
                        onAdminLogin = { email, pass -> viewModel.login(email, pass) },
                        onApproveDeposit = { txId -> viewModel.approveDeposit(txId) },
                        onRejectDeposit = { txId -> viewModel.rejectDeposit(txId) },
                        onUpdateConfig = { pkrRate, usdtAddr, easyTitle, easyNum, jazzTitle, jazzNum, bankTitle, bankName, bankIban ->
                            viewModel.updateConfig(pkrRate, usdtAddr, easyTitle, easyNum, jazzTitle, jazzNum, bankTitle, bankName, bankIban)
                        },
                        onAdjustBalance = { uId, amt, note -> viewModel.adjustUserBalance(uId, amt, note) },
                        onSendAdminReply = { targetUid, replyMsg -> viewModel.sendSupportMessage(replyMsg, targetUid, "ADMIN") }
                    )
                }
            }

            // MODALS / DIALOGS
            if (isDepositModalOpen) {
                DepositModal(
                    appConfig = appConfig,
                    onDismiss = { viewModel.closeDepositModal() },
                    onSubmitDeposit = { amount, method, tid, proof ->
                        viewModel.submitDeposit(amount, method, tid, proof)
                    }
                )
            }

            if (isAuthModalOpen) {
                AuthDialog(
                    onDismiss = { viewModel.closeAuthModal() },
                    onLogin = { email, pass ->
                        viewModel.login(email, pass) { viewModel.closeAuthModal() }
                    },
                    onRegister = { email, pass, refCode ->
                        viewModel.register(email, pass, refCode) { viewModel.closeAuthModal() }
                    }
                )
            }

            if (isChatModalOpen) {
                SupportChatModal(
                    messages = userChatMessages,
                    onDismiss = { viewModel.closeChatModal() },
                    onSendMessage = { text ->
                        viewModel.sendSupportMessage(text)
                    }
                )
            }
        }
    }
}

private data class NavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
