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

//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "gtfs_database"
//                )
//                .fallbackToDestructiveMigration()
//                .build()
//                INSTANCE = instance
//                instance
//            }
//        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "gtfs_database"
            )
                .createFromAsset("gtfs.db")
                .build()

            // prepopulate the database after onCreate was called
//            .addCallback(object : Callback() {
//                override fun onCreate(db: SupportSQLiteDatabase) {
//                    super.onCreate(db)
//                    // insert the data asynchronously
//                    Executors.newSingleThreadScheduledExecutor().execute {
//                        getDatabase(context).stopDao().insert(STOP_DATA1)
//                        getDatabase(context).stopDao().insert(STOP_DATA2)
//                        getDatabase(context).routeDao().insert(ROUTE_DATA1)
//                        getDatabase(context).routeDao().insert(ROUTE_DATA2)
//                        getDatabase(context).tripDao().insert(TRIP_DATA1)
//                        getDatabase(context).tripDao().insert(TRIP_DATA2)
//                        getDatabase(context).tripDao().insert(TRIP_DATA3)
//                        getDatabase(context).stopTimeDao().insert(STOP_TIME_DATA1)
//                        getDatabase(context).stopTimeDao().insert(STOP_TIME_DATA2)
//                        getDatabase(context).stopTimeDao().insert(STOP_TIME_DATA3)
//                    }
//                }
//            })
//            .build()

//        val STOP_DATA1 = StopEntity("1", "0679", "PABIANICA-PRADZYNSKIEGO")
//        val STOP_DATA2 = StopEntity("2", "0678", "PABIANICKA PRADZYNSKIEGO")
//
//        val ROUTE_DATA1 = RouteEntity("1", "50A")
//        val ROUTE_DATA2 = RouteEntity("2", "11B")
//
//        val TRIP_DATA1 = TripEntity("1", "1", "1")
//        val TRIP_DATA2 = TripEntity("2", "2", "2")
//        val TRIP_DATA3 = TripEntity("3", "1", "1")
//
//        val STOP_TIME_DATA1 = StopTimeEntity(1, "1", "07:67:00", "1")
//        val STOP_TIME_DATA2 = StopTimeEntity(2, "2", "15:69:00", "2")
//        val STOP_TIME_DATA3 = StopTimeEntity(3, "3", "17:67:00", "1")

    }
}
