package com.example.healthmate.health.data.data_source
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.healthmate.health.domain.model.HealthRecordEntity


@Dao
interface HealthDao {

    @Query("SELECT * FROM health_records WHERE date= :date")
    fun getRecordByDate(date: String): Flow <HealthRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHealthRecord(record: HealthRecordEntity)

    @Query("SELECT * FROM health_records ORDER BY date DESC")
    fun getAllRecords(): Flow <List<HealthRecordEntity>>
}