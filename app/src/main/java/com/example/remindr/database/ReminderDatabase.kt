package com.example.remindr.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.remindr.ImageConverter
import java.security.AccessControlContext

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
@TypeConverters(ImageConverter::class)
abstract class ReminderDatabase: RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: ReminderDatabase? = null

        fun getDatabase(context: Context): ReminderDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,
                    "reminder_app_database"
                ). build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

