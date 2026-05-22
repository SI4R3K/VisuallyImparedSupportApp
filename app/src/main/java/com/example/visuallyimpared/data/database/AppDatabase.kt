package com.example.visuallyimpared.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OcrRecord::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun ocrDao(): OcrDao
}
