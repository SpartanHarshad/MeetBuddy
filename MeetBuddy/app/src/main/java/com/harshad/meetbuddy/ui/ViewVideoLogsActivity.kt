package com.harshad.meetbuddy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.harshad.meetbuddy.BuddyApplication
import com.harshad.meetbuddy.R
import com.harshad.meetbuddy.data.VideoCallLogAdapter
import com.harshad.meetbuddy.data.local.VideoCallEntity
import com.harshad.meetbuddy.databinding.ActivityViewVideoLogsBinding
import com.harshad.meetbuddy.viewmodel.MeetViewModel
import com.harshad.meetbuddy.viewmodel.MeetViewModelFactory

class ViewVideoLogsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewVideoLogsBinding
    private lateinit var meetViewModel: MeetViewModel
    private lateinit var videoCallLogAdapter: VideoCallLogAdapter
    private var logs = ArrayList<VideoCallEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_video_logs)
        initViewModelAndRecyclerView()
    }

    private fun initViewModelAndRecyclerView() {
        val meetApp = this.application as BuddyApplication
        val meetRepo = meetApp.meetRepository
        val meetViewModelFactory = MeetViewModelFactory(meetRepo)
        videoCallLogAdapter = VideoCallLogAdapter(logs)
        binding.rvVideoCallLogs.layoutManager = LinearLayoutManager(this)
        binding.rvVideoCallLogs.adapter = videoCallLogAdapter
        meetViewModel = ViewModelProvider(this, meetViewModelFactory)[MeetViewModel::class.java]
        meetViewModel.getVideoCallLogs()
        meetViewModel.videoCallLogs.observe(this) { videologs ->
            logs.clear()
            logs.addAll(videologs)
            videoCallLogAdapter.updateList(logs)
        }
    }
}