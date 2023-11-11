package com.harshad.meetbuddy.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.harshad.meetbuddy.R
import com.harshad.meetbuddy.databinding.ActivityChooseCallModeBinding

class ChooseCallModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseCallModeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_call_mode)
        performClicks()
    }

    private fun performClicks() {
        binding.btnVideo.setOnClickListener {
            val videoCall = Intent(this, VideoCallActivity::class.java)
            startActivity(videoCall)
        }

        binding.btnVoice.setOnClickListener {
            val voiceIntent = Intent(this, VoiceCallActivity::class.java)
            startActivity(voiceIntent)
        }

    }
}