package com.harshad.meetbuddy.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.harshad.meetbuddy.data.local.VideoCallEntity
import com.harshad.meetbuddy.databinding.LayoutItemCardBinding

class VideoCallLogAdapter(private var videoList: List<VideoCallEntity>) :
    RecyclerView.Adapter<VideoCallLogAdapter.VideoCallHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoCallHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemCardBinding.inflate(inflater, parent, false)
        return VideoCallHolder(binding)
    }

    override fun getItemCount(): Int {
       return videoList.size
    }

    fun updateList(newVideoList: List<VideoCallEntity>){
        videoList = newVideoList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: VideoCallHolder, position: Int) {
        holder.setData(videoList[position])
    }

    inner class VideoCallHolder(private val binding: LayoutItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(videoCallEntity: VideoCallEntity) {
            binding.tvDuration.text = videoCallEntity.callDuration
            binding.tvTimeStamp.text = videoCallEntity.callTimeStamp
            binding.tvRemoteUserId.text = videoCallEntity.remotedUserId
        }
    }
}