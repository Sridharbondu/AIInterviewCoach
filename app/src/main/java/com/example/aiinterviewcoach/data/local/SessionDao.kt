package com.example.aiinterviewcoach.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT AVG(totalScore) FROM sessions")
    suspend fun getAverageScore(): Float?

    @Query("SELECT COUNT(*) FROM sessions")
    suspend fun getTotalSessions(): Int

    @Delete
    suspend fun deleteSession(session: SessionEntity)
}