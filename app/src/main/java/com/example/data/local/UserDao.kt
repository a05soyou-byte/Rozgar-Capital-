package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserByIdFlow(uid: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE myReferralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET balance = balance + :amount WHERE uid = :uid")
    suspend fun addBalance(uid: String, amount: Double)

    @Query("UPDATE users SET balance = balance + :amount, referralEarnings = referralEarnings + :amount WHERE uid = :uid")
    suspend fun addReferralReward(uid: String, amount: Double)

    @Query("UPDATE users SET balance = balance - :amount, totalInvested = totalInvested + :amount WHERE uid = :uid")
    suspend fun investAmount(uid: String, amount: Double)
}
