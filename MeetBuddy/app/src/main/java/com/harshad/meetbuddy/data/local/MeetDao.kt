package com.harshad.meetbuddy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MeetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVoiceCall(audioCallEntity: AudioCallEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVideoCall(videoCallEntity: VideoCallEntity)

    @Query("SELECT * FROM Audio_Call_Log")
    fun getAllVoiceCallLogs():List<AudioCallEntity>

    @Query("SELECT * FROM Video_Call_Log")
    fun getAllVideoCallLogs():List<VideoCallEntity>

}