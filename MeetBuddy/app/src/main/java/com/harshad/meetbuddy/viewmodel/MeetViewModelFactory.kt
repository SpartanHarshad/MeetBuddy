package com.harshad.meetbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.harshad.meetbuddy.repository.MeetRepository

class MeetViewModelFactory(private val meetRepository: MeetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MeetViewModel(meetRepository) as T
    }

}