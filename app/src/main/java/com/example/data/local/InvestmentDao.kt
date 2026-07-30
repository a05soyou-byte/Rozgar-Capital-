package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Query("SELECT * FROM investments WHERE userId = :userId ORDER BY startDate DESC")
    fun getInvestmentsForUser(userId: String): Flow<List<InvestmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: InvestmentEntity): Long

    @Update
    suspend fun updateInvestment(investment: InvestmentEntity)

    @Query("SELECT * FROM investments WHERE status = 'ACTIVE'")
    suspend fun getAllActiveInvestments(): List<InvestmentEntity>
}
