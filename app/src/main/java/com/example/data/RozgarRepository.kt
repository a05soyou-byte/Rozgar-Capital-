package com.example.data

import android.content.Context
import com.example.data.local.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class RozgarRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val userDao = db.userDao()
    private val transactionDao = db.transactionDao()
    private val investmentDao = db.investmentDao()
    private val appConfigDao = db.appConfigDao()
    private val supportMessageDao = db.supportMessageDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialData()
        }
    }

    private suspend fun seedInitialData() {
        // 1. Seed Config if missing
        if (appConfigDao.getConfig() == null) {
            appConfigDao.insertOrUpdateConfig(AppConfigEntity())
        }

        // 2. Seed Admin user if missing
        val adminUser = userDao.getUserByEmail("admin@rozgar.com")
        if (adminUser == null) {
            val admin = UserEntity(
                uid = "ADMIN_SEC_001",
                email = "admin@rozgar.com",
                passwordHash = "admin123",
                balance = 50000.0,
                myReferralCode = "ADMIN01",
                role = "ADMIN"
            )
            userDao.insertUser(admin)
        }

        // 3. Seed Demo user if no normal users exist
        val existingUsers = userDao.getUserByEmail("investor@rozgar.com")
        if (existingUsers == null) {
            val demoUser = UserEntity(
                uid = "USR_DEMO_001",
                email = "investor@rozgar.com",
                passwordHash = "investor123",
                balance = 10.0, // $10 signup bonus
                myReferralCode = "RZG100",
                referredByCode = null,
                role = "USER"
            )
            userDao.insertUser(demoUser)
            
            // Seed sample rich transaction history for demo user
            val now = System.currentTimeMillis()
            transactionDao.insertTransaction(
                TransactionEntity(
                    userId = demoUser.uid,
                    userEmail = demoUser.email,
                    type = "SIGNUP_BONUS",
                    amount = 10.0,
                    status = "APPROVED",
                    timestamp = now - 86400000 * 5,
                    note = "$10 Instant Welcome Reward"
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    userId = demoUser.uid,
                    userEmail = demoUser.email,
                    type = "DEPOSIT",
                    amount = 250.0,
                    amountPkr = 70000.0,
                    status = "APPROVED",
                    tid = "TX8921340912",
                    paymentMethod = "USDT (TRC20)",
                    timestamp = now - 86400000 * 4,
                    note = "Deposit via USDT TRC20 Wallet"
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    userId = demoUser.uid,
                    userEmail = demoUser.email,
                    type = "INVESTMENT",
                    amount = 150.0,
                    status = "APPROVED",
                    timestamp = now - 86400000 * 3,
                    note = "Invested in Tier 2 Gold Asset Pool"
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    userId = demoUser.uid,
                    userEmail = demoUser.email,
                    type = "DAILY_ROI",
                    amount = 6.0,
                    status = "APPROVED",
                    timestamp = now - 86400000 * 2,
                    note = "Daily ROI Profit (Tier 2 4.0%)"
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    userId = demoUser.uid,
                    userEmail = demoUser.email,
                    type = "REFERRAL_TIER_1",
                    amount = 12.50,
                    status = "APPROVED",
                    timestamp = now - 86400000 * 1,
                    note = "5% Direct Commission from Usama"
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    userId = demoUser.uid,
                    userEmail = demoUser.email,
                    type = "WITHDRAWAL",
                    amount = 20.0,
                    status = "APPROVED",
                    tid = "WD99120412",
                    paymentMethod = "USDT",
                    timestamp = now - 3600000 * 5,
                    note = "Payout to wallet TX...89a2"
                )
            )

            // Seed initial welcome message for demo user
            supportMessageDao.insertMessage(
                SupportMessageEntity(
                    userUid = demoUser.uid,
                    senderType = "SUPPORT",
                    senderName = "Rozgar AI Concierge",
                    message = "Welcome to Rozgar Capital! How can we assist you with your real asset investment portfolio today?"
                )
            )
        }
    }

    fun getChatMessagesForUser(userUid: String): Flow<List<SupportMessageEntity>> =
        supportMessageDao.getMessagesForUser(userUid)

    fun getAllChatMessages(): Flow<List<SupportMessageEntity>> =
        supportMessageDao.getAllMessages()

    suspend fun sendSupportMessage(
        userUid: String,
        senderType: String,
        senderName: String,
        messageText: String
    ): Long {
        val userMsgId = supportMessageDao.insertMessage(
            SupportMessageEntity(
                userUid = userUid,
                senderType = senderType,
                senderName = senderName,
                message = messageText
            )
        )

        // If sent by USER, generate automatic intelligent bot/support response
        if (senderType == "USER") {
            val lower = messageText.lowercase()
            val replyText = when {
                lower.contains("deposit") || lower.contains("payment") || lower.contains("pkr") || lower.contains("usdt") ->
                    "Hi! Deposits are processed via USDT (TRC20), EasyPaisa, or Bank IBAN. Enter your 12-digit TID in the Deposit Modal to request instant manual approval."
                lower.contains("withdraw") || lower.contains("payout") ->
                    "Hello! Daily ROI profits can be withdrawn directly to your USDT wallet or Pakistani bank account."
                lower.contains("bonus") || lower.contains("10") ->
                    "Welcome! Every newly registered account automatically claims a $10 launch reward."
                lower.contains("tier") || lower.contains("roi") || lower.contains("rate") ->
                    "We offer Tier 1 (2.5% daily), Tier 2 (4.0% daily), and Tier 3 (6.5% daily) asset plans backed by global forex, gold, and real estate."
                lower.contains("referral") || lower.contains("affiliate") ->
                    "Earn 5% direct commission on Tier 1 referrals and 2% passive commission on Tier 2 sub-referrals!"
                else ->
                    "Thank you for contacting Rozgar Support! An institutional portfolio manager has received your message and will review it shortly."
            }

            supportMessageDao.insertMessage(
                SupportMessageEntity(
                    userUid = userUid,
                    senderType = "SUPPORT",
                    senderName = "Rozgar Concierge",
                    message = replyText
                )
            )
        }

        return userMsgId
    }

    val configFlow: Flow<AppConfigEntity?> = appConfigDao.getConfigFlow()

    fun getUserFlow(uid: String): Flow<UserEntity?> = userDao.getUserByIdFlow(uid)

    fun getUserTransactions(uid: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsForUser(uid)

    fun getUserInvestments(uid: String): Flow<List<InvestmentEntity>> =
        investmentDao.getInvestmentsForUser(uid)

    fun getPendingDeposits(): Flow<List<TransactionEntity>> =
        transactionDao.getPendingDeposits()

    fun getAllTransactions(): Flow<List<TransactionEntity>> =
        transactionDao.getAllTransactions()

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getUserByEmail(email.trim().lowercase())
            ?: return Result.failure(Exception("User not found with email: $email"))
        
        if (user.passwordHash != password) {
            return Result.failure(Exception("Invalid password"))
        }
        return Result.success(user)
    }

    suspend fun register(
        email: String,
        password: String,
        refCodeInput: String? = null
    ): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        if (userDao.getUserByEmail(cleanEmail) != null) {
            return Result.failure(Exception("Account already exists with email: $email"))
        }

        var referrerCode: String? = null
        if (!refCodeInput.isNullOrBlank()) {
            val refUser = userDao.getUserByReferralCode(refCodeInput.trim().uppercase())
            if (refUser != null) {
                referrerCode = refUser.myReferralCode
            }
        }

        val uid = "USR_" + UUID.randomUUID().toString().take(8).uppercase()
        val myRefCode = "RZ" + (1000..9999).random()
        val newUser = UserEntity(
            uid = uid,
            email = cleanEmail,
            passwordHash = password,
            balance = 10.0, // $10 Signup bonus automatically
            totalInvested = 0.0,
            referralEarnings = 0.0,
            myReferralCode = myRefCode,
            referredByCode = referrerCode,
            role = "USER"
        )

        userDao.insertUser(newUser)

        // Log $10 signup bonus transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                userId = newUser.uid,
                userEmail = newUser.email,
                type = "SIGNUP_BONUS",
                amount = 10.0,
                status = "APPROVED",
                note = "$10 Instant Signup Bonus"
            )
        )

        return Result.success(newUser)
    }

    suspend fun submitDeposit(
        userId: String,
        amountUsd: Double,
        paymentMethod: String,
        tid: String,
        proofUri: String
    ): Result<Long> {
        val user = userDao.getUserById(userId)
            ?: return Result.failure(Exception("User not found"))
        val config = appConfigDao.getConfig() ?: AppConfigEntity()
        val amountPkr = amountUsd * config.pkrExchangeRate

        val tx = TransactionEntity(
            userId = user.uid,
            userEmail = user.email,
            type = "DEPOSIT",
            amount = amountUsd,
            amountPkr = amountPkr,
            status = "PENDING",
            tid = tid.trim(),
            paymentMethod = paymentMethod,
            proofUri = proofUri,
            note = "Deposit via $paymentMethod"
        )

        val txId = transactionDao.insertTransaction(tx)
        return Result.success(txId)
    }

    suspend fun approveDeposit(txId: Long): Result<Unit> {
        val tx = transactionDao.getTransactionById(txId)
            ?: return Result.failure(Exception("Transaction not found"))
        if (tx.status != "PENDING") {
            return Result.failure(Exception("Transaction already processed"))
        }

        // 1. Mark transaction APPROVED
        transactionDao.updateStatus(txId, "APPROVED")

        // 2. Add deposit amount to User's balance
        userDao.addBalance(tx.userId, tx.amount)

        // 3. Process 2-Tier Referral Logic!
        val depositor = userDao.getUserById(tx.userId)
        if (depositor != null && !depositor.referredByCode.isNullOrBlank()) {
            // Tier 1 Direct Referrer
            val tier1Referrer = userDao.getUserByReferralCode(depositor.referredByCode)
            if (tier1Referrer != null) {
                val tier1Bonus = tx.amount * 0.05 // 5% Direct Commission
                userDao.addReferralReward(tier1Referrer.uid, tier1Bonus)
                transactionDao.insertTransaction(
                    TransactionEntity(
                        userId = tier1Referrer.uid,
                        userEmail = tier1Referrer.email,
                        type = "REFERRAL_TIER_1",
                        amount = tier1Bonus,
                        status = "APPROVED",
                        note = "5% Direct Commission from ${depositor.email}"
                    )
                )

                // Tier 2 Indirect Referrer
                if (!tier1Referrer.referredByCode.isNullOrBlank()) {
                    val tier2Referrer = userDao.getUserByReferralCode(tier1Referrer.referredByCode)
                    if (tier2Referrer != null) {
                        val tier2Bonus = tx.amount * 0.02 // 2% Indirect Commission
                        userDao.addReferralReward(tier2Referrer.uid, tier2Bonus)
                        transactionDao.insertTransaction(
                            TransactionEntity(
                                userId = tier2Referrer.uid,
                                userEmail = tier2Referrer.email,
                                type = "REFERRAL_TIER_2",
                                amount = tier2Bonus,
                                status = "APPROVED",
                                note = "2% Indirect Commission from deposit by ${depositor.email}"
                            )
                        )
                    }
                }
            }
        }

        return Result.success(Unit)
    }

    suspend fun rejectDeposit(txId: Long): Result<Unit> {
        val tx = transactionDao.getTransactionById(txId)
            ?: return Result.failure(Exception("Transaction not found"))
        if (tx.status != "PENDING") {
            return Result.failure(Exception("Transaction already processed"))
        }
        transactionDao.updateStatus(txId, "REJECTED")
        return Result.success(Unit)
    }

    suspend fun createInvestment(
        userId: String,
        tierName: String,
        amount: Double,
        dailyRoiPercent: Double,
        durationDays: Int
    ): Result<Long> {
        val user = userDao.getUserById(userId)
            ?: return Result.failure(Exception("User not found"))
        if (user.balance < amount) {
            return Result.failure(Exception("Insufficient balance ($${String.format("%.2f", user.balance)}). Please deposit funds."))
        }

        // Deduct user balance and increase totalInvested
        userDao.investAmount(userId, amount)

        val investment = InvestmentEntity(
            userId = userId,
            tierName = tierName,
            investedAmount = amount,
            dailyRoiPercent = dailyRoiPercent,
            durationDays = durationDays,
            startDate = System.currentTimeMillis(),
            accumulatedProfit = 0.0,
            status = "ACTIVE"
        )

        val invId = investmentDao.insertInvestment(investment)

        transactionDao.insertTransaction(
            TransactionEntity(
                userId = userId,
                userEmail = user.email,
                type = "INVESTMENT",
                amount = amount,
                status = "APPROVED",
                note = "Invested in $tierName Tier"
            )
        )

        return Result.success(invId)
    }

    suspend fun updateAppConfig(
        pkrRate: Double,
        usdtAddress: String,
        easypaisaTitle: String,
        easypaisaNumber: String,
        jazzcashTitle: String,
        jazzcashNumber: String,
        bankTitle: String,
        bankName: String,
        bankIban: String
    ): Result<Unit> {
        val updated = AppConfigEntity(
            id = 1,
            pkrExchangeRate = pkrRate,
            usdtAddress = usdtAddress,
            easypaisaTitle = easypaisaTitle,
            easypaisaNumber = easypaisaNumber,
            jazzcashTitle = jazzcashTitle,
            jazzcashNumber = jazzcashNumber,
            bankTitle = bankTitle,
            bankName = bankName,
            bankIban = bankIban
        )
        appConfigDao.insertOrUpdateConfig(updated)
        return Result.success(Unit)
    }

    suspend fun adjustUserBalance(userId: String, amount: Double, note: String): Result<Unit> {
        val user = userDao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
        userDao.addBalance(userId, amount)
        transactionDao.insertTransaction(
            TransactionEntity(
                userId = userId,
                userEmail = user.email,
                type = if (amount >= 0) "ADMIN_CREDIT" else "ADMIN_DEBIT",
                amount = kotlin.math.abs(amount),
                status = "APPROVED",
                note = note.ifEmpty { "Admin manual balance adjustment" }
            )
        )
        return Result.success(Unit)
    }
}
