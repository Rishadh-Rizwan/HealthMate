package com.example.healthmate.health.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthmate.health.domain.model.HealthRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    @Query("SELECT * FROM health_records WHERE date = :date")
    fun getRecordByDate(date: String): Flow<HealthRecordEntity?>

    // Search query using SQL LIKE for partial date matching (e.g. "2026-11")
    @Query("SELECT * FROM health_records WHERE date LIKE '%' || :searchQuery || '%' ORDER BY date DESC")
    fun searchRecords(searchQuery: String): Flow<List<HealthRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHealthRecord(record: HealthRecordEntity)

    @Delete
    suspend fun deleteRecord(record: HealthRecordEntity)

    @Query("SELECT * FROM health_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<HealthRecordEntity>>
}