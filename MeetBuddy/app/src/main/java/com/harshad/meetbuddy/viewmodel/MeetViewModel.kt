package com.harshad.meetbuddy.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harshad.meetbuddy.data.local.AudioCallEntity
import com.harshad.meetbuddy.data.local.VideoCallEntity
import com.harshad.meetbuddy.repository.MeetRepository

class MeetViewModel(val meetRepository: MeetRepository) : ViewModel() {

    val audioCallLogs = MutableLiveData<List<AudioCallEntity>>()
    val videoCallLogs = MutableLiveData<List<VideoCallEntity>>()

    fun saveVideoCallDetails(videoCallEntity: VideoCallEntity) {
        meetRepository.saveVideoCallRecord(videoCallEntity)
    }

    fun saveAudioCallDetails(audioCallEntity: AudioCallEntity) {
        meetRepository.saveAudioCallRecord(audioCallEntity)
    }

    fun getVideoCallLogs(){
        videoCallLogs.postValue(meetRepository.getAllVideoCallLogs())
    }

    fun getAudioCallLogs(){
        audioCallLogs.postValue(meetRepository.getAllAudioCallLogs())
    }

}