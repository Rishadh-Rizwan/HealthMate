package com.example.healthmate.health.data.data_source

import com.example.healthmate.health.domain.model.HealthRecordEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HealthRecordEntity::class], version = 1, exportSchema = false)
abstract class HealthDatabase : RoomDatabase() {
    abstract val healthDao: HealthDao

    companion object {
        @Volatile
        private var INSTANCE: HealthDatabase? = null

        fun getInstance(context: Context): HealthDatabase {

            return INSTANCE ?:synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthDatabase::class.java,
                    "health_db"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }
}