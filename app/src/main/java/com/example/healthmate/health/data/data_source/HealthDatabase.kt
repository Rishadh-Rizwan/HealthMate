package com.example.healthmate.health.data.data_source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.healthmate.health.domain.model.HealthRecordEntity
import com.example.healthmate.health.domain.model.ActivityLogEntity

@Database(
    entities = [
        HealthRecordEntity::class,
        ActivityLogEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class HealthDatabase : RoomDatabase() {

    abstract val healthDao: HealthDao

    companion object {

        @Volatile
        private var INSTANCE: HealthDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL(
                    """
                    CREATE TABLE health_records_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        date TEXT NOT NULL,
                        steps INTEGER NOT NULL,
                        calories INTEGER NOT NULL,
                        water INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    INSERT INTO health_records_new
                    (id, date, steps, calories, water)
                    SELECT
                        MAX(id),
                        date,
                        SUM(steps),
                        SUM(calories),
                        SUM(water)
                    FROM health_records
                    GROUP BY date
                    """.trimIndent()
                )

                database.execSQL("DROP TABLE health_records")

                database.execSQL(
                    "ALTER TABLE health_records_new RENAME TO health_records"
                )

                database.execSQL(
                    """
                    CREATE UNIQUE INDEX index_health_records_date
                    ON health_records(date)
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS activity_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        date TEXT NOT NULL,
                        activityType TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_activity_logs_date_activityType
                    ON activity_logs(date, activityType)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_activity_logs_timestamp
                    ON activity_logs(timestamp)
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL(
                    """
                    ALTER TABLE health_records
                    ADD COLUMN stepCalories INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE health_records
                    ADD COLUMN calorieSteps INTEGER NOT NULL DEFAULT 0
                    """.trimIndent()
                )

                // Existing steps become the baseline.
                // This prevents old steps from receiving retroactive calories.
                database.execSQL(
                    "UPDATE health_records SET calorieSteps = steps"
                )
            }
        }

        fun getInstance(context: Context): HealthDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthDatabase::class.java,
                    "health_db"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4
                    )
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}