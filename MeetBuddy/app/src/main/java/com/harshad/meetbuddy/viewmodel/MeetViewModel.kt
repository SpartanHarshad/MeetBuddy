package com.harshad.meetbuddy.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshad.meetbuddy.data.local.AudioCallEntity
import com.harshad.meetbuddy.data.local.VideoCallEntity
import com.harshad.meetbuddy.repository.MeetRepository
import kotlinx.coroutines.launch

class MeetViewModel(val meetRepository: MeetRepository) : ViewModel() {

    val audioCallLogs = MutableLiveData<List<AudioCallEntity>>()
    val videoCallLogs = MutableLiveData<List<VideoCallEntity>>()

    fun saveVideoCallDetails(videoCallEntity: VideoCallEntity) {
        meetRepository.saveVideoCallRecord(videoCallEntity)
    }

    fun saveAudioCallDetails(audioCallEntity: AudioCallEntity) {
        meetRepository.saveAudioCallRecord(audioCallEntity)
    }

    fun getVideoCallLogs() {
        viewModelScope.launch {
            videoCallLogs.postValue(meetRepository.getAllVideoCallLogs())
        }
    }

    fun getAudioCallLogs() {
        viewModelScope.launch {
            audioCallLogs.postValue(meetRepository.getAllAudioCallLogs())
        }
    }

}