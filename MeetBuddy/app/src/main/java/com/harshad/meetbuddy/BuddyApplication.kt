package com.harshad.meetbuddy

import android.app.Application
import com.harshad.meetbuddy.data.local.MeetBuddyDatabase
import com.harshad.meetbuddy.repository.MeetRepository

class BuddyApplication : Application() {
    private val meetDao by lazy {
        val meetDb = MeetBuddyDatabase.getMeetDatabaseInstance(this)
        meetDb.getMeetDao()
    }

    val meetRepository by lazy {
        MeetRepository(meetDao)
    }

}