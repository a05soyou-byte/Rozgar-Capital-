package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "support_messages")
data class SupportMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userUid: String,
    val senderType: String, // "USER", "SUPPORT", "BOT", "ADMIN"
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Dao
interface SupportMessageDao {
    @Query("SELECT * FROM support_messages WHERE userUid = :userUid ORDER BY timestamp ASC")
    fun getMessagesForUser(userUid: String): Flow<List<SupportMessageEntity>>

    @Query("SELECT * FROM support_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<SupportMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: SupportMessageEntity): Long

    @Query("DELETE FROM support_messages WHERE userUid = :userUid")
    suspend fun clearMessagesForUser(userUid: String)
}
