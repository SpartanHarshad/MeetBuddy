package com.harshad.meetbuddy.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.harshad.meetbuddy.BuddyApplication
import com.harshad.meetbuddy.R
import com.harshad.meetbuddy.data.local.VideoCallEntity
import com.harshad.meetbuddy.databinding.ActivityVideoCallBinding
import com.harshad.meetbuddy.utility.Constant
import com.harshad.meetbuddy.utility.Util
import com.harshad.meetbuddy.utility.Util.getDurationForMilliseconds
import com.harshad.meetbuddy.utility.Util.getTimeStamp
import com.harshad.meetbuddy.viewmodel.MeetViewModel
import com.harshad.meetbuddy.viewmodel.MeetViewModelFactory
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas


class VideoCallActivity : BaseActivity() {

    private lateinit var binding: ActivityVideoCallBinding
    private var channelName = "Test Video Call"
    private var uid = 0
    private var isJoined = false
    private var agoraEngine: RtcEngine? = null
    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null
    private val PERMISSION_REQ_ID = 777
    var callStartTime: Long = 0L
    var callEndTime: Long = 0L
    var callDuration: Long = 0L
    var remoteUserId = "NA"
    val TAG = "VideoCallActivity"

    private lateinit var meetViewModel: MeetViewModel
    private val REQUESTED_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
    )


    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        //Listen for the remote host joining the channel to get the uid of the host.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            remoteUserId = "$uid"
            showMessage("Remote User joined $uid")
            //Set the remote video view
            runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            isJoined = true
            callStartTime = System.currentTimeMillis()
            Log.d(TAG, "join time : ${getTimeStamp(callStartTime)}")
            showMessage("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            //super.onUserOffline(uid, reason)
            showMessage("Remote user offline $uid $reason")
            runOnUiThread { remoteSurfaceView!!.visibility = View.VISIBLE }
        }

        override fun onError(err: Int) {
            super.onError(err)
            //Constants.ERR_INVALID_TOKEN
            showMessage("error code $err ")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_call)
        initViewModel()
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        setUpVideoSDKEngine()
    }

    private fun initViewModel() {
        val meetApp = this.application as BuddyApplication
        val meetRepo = meetApp.meetRepository
        val meetViewModelFactory = MeetViewModelFactory(meetRepo)
        meetViewModel = ViewModelProvider(this, meetViewModelFactory)[MeetViewModel::class.java]

        binding.btnCallLogs.setOnClickListener {
            if (!isJoined){
                val viewLogs = Intent(this, ViewVideoLogsActivity::class.java)
                startActivity(viewLogs)
            }else{
             showMessage("You can't see call logs while call is on..")
            }
        }

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

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContainer.addView(remoteSurfaceView)
        agoraEngine?.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        remoteSurfaceView!!.visibility = View.VISIBLE
    }

    private fun setUpVideoSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = Constant.Agora_App_Id
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine?.enableVideo()
        } catch (exc: Exception) {
            showMessage(exc.toString())
        }
    }

    private fun setUpLocalVideo() {
        localSurfaceView = SurfaceView(baseContext)
        localSurfaceView!!.setZOrderMediaOverlay(true)
        binding.localVideoViewContainer.addView(localSurfaceView)
        agoraEngine?.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

    fun joinChannel(view: View) {
        if (checkSelfPermission()) {
            val options = ChannelMediaOptions()
            // For a Video call, set the channel profile as COMMUNICATION.
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            // Display LocalSurfaceView.
            setUpLocalVideo()
            localSurfaceView!!.visibility = View.VISIBLE
            // Start local preview.
            agoraEngine?.startPreview()
            // Join the channel with a temp token.
            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
            agoraEngine?.joinChannel(Constant.videoCallToken, channelName, uid, options)
        } else {
            Toast.makeText(applicationContext, "Permissions was not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun leaveChannel(view: View) {
        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            agoraEngine?.leaveChannel()
            showMessage("You left the channel")
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
            isJoined = false
            saveCallDetailsIntoLocalDb()
            Log.d(TAG, "videco call duration ${getDurationForMilliseconds(callDuration)}")
        }
    }

    private fun saveCallDetailsIntoLocalDb() {
        callEndTime = System.currentTimeMillis()
        callDuration = callEndTime - callStartTime
        val timeStamp = getTimeStamp(callStartTime)
        val duration = getDurationForMilliseconds(callDuration)
        val videoCallDetails = VideoCallEntity(remoteUserId, timeStamp, duration)
        meetViewModel.saveVideoCallDetails(videoCallDetails)
    }


    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.stopPreview()
        agoraEngine?.leaveChannel()
        saveCallDetailsIntoLocalDb()
        Log.d(TAG, "on destroy call duration ${getDurationForMilliseconds(callDuration)}")
        // Destroy the engine in a sub-thread to avoid congestion
        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSIONS[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    REQUESTED_PERMISSIONS[1]
                ) != PackageManager.PERMISSION_GRANTED)
    }
}