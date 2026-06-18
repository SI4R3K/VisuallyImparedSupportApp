package com.example.visuallyimpared.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.visuallyimpared.data.database.dao.*
import com.example.visuallyimpared.data.database.entity.*
import java.util.concurrent.Executors

@Database(
    entities = [
        StopEntity::class,
        StopTimeEntity::class,
        TripEntity::class,
        RouteEntity::class,
        CalendarEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stopDao(): StopDao
    abstract fun departureDao(): DepartureDao
    abstract fun routeDao(): RouteDao
    abstract fun tripDao(): TripDao
    abstract fun stopTimeDao(): StopTimeDao
    abstract fun calendarDao(): CalendarDao

    companion object {
        @Volatile // making sure that database instance is always read from the specific place in memory and not cached anywhere
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "gtfs_database"
            )
                .createFromAsset("gtfs.db")
                .build()

    }
}
