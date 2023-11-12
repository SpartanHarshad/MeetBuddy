package com.harshad.meetbuddy.ui


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.harshad.meetbuddy.R
import com.harshad.meetbuddy.databinding.ActivityVoiceCallBinding
import com.harshad.meetbuddy.utility.Constant
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig


class VoiceCallActivity : BaseActivity() {

    lateinit var binding: ActivityVoiceCallBinding
    private var channelName = "Test Voice Call"
    private var uid = 0
    private var isJoined = false
    private var agoraEngine: RtcEngine? = null
    private val PERMISSION_REQ_ID = 777
    private val REQUESTED_PERMISSIONS = arrayOf(
        android.Manifest.permission.RECORD_AUDIO
    )

    val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread { binding.tvHeading.text = "Remote user joined: $uid" }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            // Successfully joined a channel
            isJoined = true
            showMessage("Joined Channel $channel")
            runOnUiThread { binding.tvHeading.text = "Waiting for a remote user to join" }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("Remote user offline $uid $reason")
            if (isJoined)
                runOnUiThread { binding.tvHeading.text = "Waiting for a remote user to join" }
        }

        override fun onLeaveChannel(stats: RtcStats?) {
            runOnUiThread { binding.tvHeading.text = "Press the button to join a channel" }
            isJoined = false
        }

        override fun onError(err: Int) {
            super.onError(err)
            isJoined = false
            showMessage("error : $err")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_voice_call)
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        setupVoiceSDKEngine()
    }

    private fun showMessage(message: String) {
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkSelfPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSIONS[0]
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupVoiceSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = Constant.Agora_App_Id
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine!!.enableAudio()
        } catch (ex: Exception) {
            Log.d("AgoraAudioCalling", "exception $ex")
        }
    }

    private fun joinChannel() {
        val options = ChannelMediaOptions()
        options.autoSubscribeAudio = true
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        Log.d("Voice","")
        agoraEngine?.disableVideo()
        agoraEngine?.joinChannel(Constant.audioCallToken, channelName, uid, options)
    }

    fun joinLeaveChannel(view: View) {
        if (isJoined) {
            agoraEngine?.leaveChannel()
            binding.btnJoinLeave.text = "Join"
        } else {
            joinChannel()
            binding.btnJoinLeave.text = "Leave"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.leaveChannel()
        // Destroy the engine in a sub-thread to avoid congestion
        // Destroy the engine in a sub-thread to avoid congestion
        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }
}