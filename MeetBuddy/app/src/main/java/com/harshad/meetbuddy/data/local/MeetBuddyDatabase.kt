package com.harshad.meetbuddy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [VideoCallEntity::class, AudioCallEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MeetBuddyDatabase : RoomDatabase() {
    abstract fun getMeetDao(): MeetDao

    companion object {
        private var INSTANCE: MeetBuddyDatabase? = null

        fun getMeetDatabaseInstance(ctx: Context): MeetBuddyDatabase {
            return if (INSTANCE == null) {
                val builder = Room.databaseBuilder(
                    ctx.applicationContext,
                    MeetBuddyDatabase::class.java,
                    "MeetBuddyDB"
                )
                builder.fallbackToDestructiveMigration()
                INSTANCE = builder.build()
                INSTANCE!!
            } else {
                INSTANCE!!
            }
        }
    }
}