package com.example.healthmate.health.data.repository

import com.example.healthmate.health.data.data_source.HealthDao
import com.example.healthmate.health.domain.model.HealthRecord
import com.example.healthmate.health.domain.model.HealthRecordEntity
import com.example.healthmate.health.domain.model.toHealthRecord
import com.example.healthmate.health.domain.model.toHealthRecordEntity
import com.example.healthmate.health.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class HealthRepositoryImpl (private val dao: HealthDao): HealthRepository {

    override fun getRecordByDate(date: String): Flow<HealthRecord?> {
        return dao.getRecordByDate(date).map { entity -> entity?.toHealthRecord() }
    }

    override suspend fun insertOrUpdateRecord(record: HealthRecord) {
        dao.upsertHealthRecord(record.toHealthRecordEntity())
    }
    override suspend fun updateWaterIntake(date: String, amount: Int) {
        val existingEntity = dao.getRecordByDate(date).firstOrNull()
        if (existingEntity != null) {
            val updatedEntity = existingEntity.copy(water = existingEntity.water + amount)
            dao.upsertHealthRecord(updatedEntity)
        } else {
            val newEntity = HealthRecordEntity(
                date = date, steps = 0, calories = 0, water = amount
            )
            dao.upsertHealthRecord(newEntity)
        }
    }

    override suspend fun updateStepCount(date: String, steps: Int) {
        val existingEntity = dao.getRecordByDate(date).firstOrNull()
        val calories = (steps * 0.04).toInt()

        if (existingEntity != null) {
            val updatedEntity = existingEntity.copy(steps = steps, calories = calories)
            dao.upsertHealthRecord(updatedEntity)
        } else {
            val newEntity = HealthRecordEntity(
                date = date, steps = steps, calories = calories, water = 0
            )
            dao.upsertHealthRecord(newEntity)
        }
    }
    override fun getAllRecords(): Flow<List<HealthRecord>> {
        return dao.getAllRecords().map { entities ->
            entities.map { it.toHealthRecord() }
        }
    }
}
