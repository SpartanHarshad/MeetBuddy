package com.harshad.meetbuddy.repository

import com.harshad.meetbuddy.data.local.AudioCallEntity
import com.harshad.meetbuddy.data.local.MeetDao
import com.harshad.meetbuddy.data.local.VideoCallEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MeetRepository(val meetDao: MeetDao) {

     fun saveVideoCallRecord(videoCallEntity: VideoCallEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            meetDao.saveVideoCall(videoCallEntity)
        }
    }

     fun saveAudioCallRecord(audioCallEntity: AudioCallEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            meetDao.saveVoiceCall(audioCallEntity)
        }
    }

     fun getAllVideoCallLogs(): List<VideoCallEntity> {
        return meetDao.getAllVideoCallLogs()
    }

     fun getAllAudioCallLogs(): List<AudioCallEntity> {
        return meetDao.getAllVoiceCallLogs()
    }

}