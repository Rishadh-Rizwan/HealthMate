package com.example.healthmate.health.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_logs",
    indices = [
        Index(value = ["date", "activityType"]),
        Index(value = ["timestamp"])
    ]
)
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val activityType: String,
    val amount: Int,
    val timestamp: Long
)

fun ActivityLogEntity.toActivityLog(): ActivityLog {
    return ActivityLog(
        id = id,
        date = date,
        activityType = activityType,
        amount = amount,
        timestamp = timestamp
    )
}
