package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RozgarRepository
import com.example.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RozgarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RozgarRepository(application)

    // Current Auth State
    private val _currentUserId = MutableStateFlow<String?>("USR_DEMO_001") // Default logged in as demo investor
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUserId
        .flatMapLatest { uid ->
            if (uid == null) flowOf(null)
            else repository.getUserFlow(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val appConfig: StateFlow<AppConfigEntity> = repository.configFlow
        .map { it ?: AppConfigEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppConfigEntity())

    val userTransactions: StateFlow<List<TransactionEntity>> = _currentUserId
        .flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList())
            else repository.getUserTransactions(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userInvestments: StateFlow<List<InvestmentEntity>> = _currentUserId
        .flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList())
            else repository.getUserInvestments(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingDeposits: StateFlow<List<TransactionEntity>> = repository.getPendingDeposits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Real-Time Chat State (Firestore / Room persistent sync)
    val userChatMessages: StateFlow<List<SupportMessageEntity>> = _currentUserId
        .flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList())
            else repository.getChatMessagesForUser(uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChatMessages: StateFlow<List<SupportMessageEntity>> = repository.getAllChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isChatModalOpen = MutableStateFlow(false)
    val isChatModalOpen: StateFlow<Boolean> = _isChatModalOpen.asStateFlow()

    fun openChatModal() {
        _isChatModalOpen.value = true
    }

    fun closeChatModal() {
        _isChatModalOpen.value = false
    }

    fun sendSupportMessage(messageText: String, targetUserUid: String? = null, senderType: String = "USER", senderName: String? = null) {
        val uid = targetUserUid ?: _currentUserId.value ?: return
        if (messageText.isBlank()) return

        val sName = senderName ?: if (senderType == "ADMIN") "Live Admin" else (currentUser.value?.email ?: "Investor")

        viewModelScope.launch {
            repository.sendSupportMessage(
                userUid = uid,
                senderType = senderType,
                senderName = sName,
                messageText = messageText
            )
        }
    }

    // UI Feedback & Dialog States
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _isDepositModalOpen = MutableStateFlow(false)
    val isDepositModalOpen: StateFlow<Boolean> = _isDepositModalOpen.asStateFlow()

    private val _isAuthModalOpen = MutableStateFlow(false)
    val isAuthModalOpen: StateFlow<Boolean> = _isAuthModalOpen.asStateFlow()

    fun showMessage(msg: String) {
        _uiMessage.value = msg
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    fun openDepositModal() {
        _isDepositModalOpen.value = true
    }

    fun closeDepositModal() {
        _isDepositModalOpen.value = false
    }

    fun openAuthModal() {
        _isAuthModalOpen.value = true
    }

    fun closeAuthModal() {
        _isAuthModalOpen.value = false
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            result.onSuccess { user ->
                _currentUserId.value = user.uid
                showMessage("Welcome back, ${user.email}!")
                onSuccess()
            }.onFailure { err ->
                showMessage("Login failed: ${err.message}")
            }
        }
    }

    fun register(email: String, pass: String, refCode: String? = null, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.register(email, pass, refCode)
            result.onSuccess { user ->
                _currentUserId.value = user.uid
                showMessage("Account registered! $10 Signup Bonus added to your wallet.")
                onSuccess()
            }.onFailure { err ->
                showMessage("Registration failed: ${err.message}")
            }
        }
    }

    fun logout() {
        _currentUserId.value = null
        showMessage("Logged out successfully.")
    }

    fun submitDeposit(
        amountUsd: Double,
        method: String,
        tid: String,
        proofUri: String
    ) {
        val uid = _currentUserId.value
        if (uid == null) {
            showMessage("Please log in to make a deposit.")
            openAuthModal()
            return
        }

        viewModelScope.launch {
            val res = repository.submitDeposit(uid, amountUsd, method, tid, proofUri)
            res.onSuccess {
                showMessage("Deposit request of $$amountUsd submitted! Pending Admin verification.")
                closeDepositModal()
            }.onFailure { err ->
                showMessage("Deposit failed: ${err.message}")
            }
        }
    }

    fun approveDeposit(txId: Long) {
        viewModelScope.launch {
            val res = repository.approveDeposit(txId)
            res.onSuccess {
                showMessage("Deposit approved! Balance and 2-tier referral commissions updated.")
            }.onFailure { err ->
                showMessage("Approval failed: ${err.message}")
            }
        }
    }

    fun rejectDeposit(txId: Long) {
        viewModelScope.launch {
            val res = repository.rejectDeposit(txId)
            res.onSuccess {
                showMessage("Deposit rejected.")
            }.onFailure { err ->
                showMessage("Rejection failed: ${err.message}")
            }
        }
    }

    fun investInTier(tierName: String, amount: Double, dailyRoi: Double, durationDays: Int) {
        val uid = _currentUserId.value
        if (uid == null) {
            showMessage("Please log in to invest.")
            openAuthModal()
            return
        }

        viewModelScope.launch {
            val res = repository.createInvestment(uid, tierName, amount, dailyRoi, durationDays)
            res.onSuccess {
                showMessage("Successfully invested $$amount in $tierName Tier!")
            }.onFailure { err ->
                showMessage(err.message ?: "Investment failed")
            }
        }
    }

    fun updateConfig(
        pkrRate: Double,
        usdtAddr: String,
        easyTitle: String,
        easyNum: String,
        jazzTitle: String,
        jazzNum: String,
        bankTitle: String,
        bankName: String,
        bankIban: String
    ) {
        viewModelScope.launch {
            val res = repository.updateAppConfig(
                pkrRate, usdtAddr, easyTitle, easyNum, jazzTitle, jazzNum, bankTitle, bankName, bankIban
            )
            res.onSuccess {
                showMessage("Platform configuration updated successfully!")
            }.onFailure { err ->
                showMessage("Config update failed: ${err.message}")
            }
        }
    }

    fun adjustUserBalance(userId: String, amount: Double, note: String) {
        viewModelScope.launch {
            val res = repository.adjustUserBalance(userId, amount, note)
            res.onSuccess {
                showMessage("User balance adjusted successfully by $$amount!")
            }.onFailure { err ->
                showMessage("Balance adjustment failed: ${err.message}")
            }
        }
    }
}
