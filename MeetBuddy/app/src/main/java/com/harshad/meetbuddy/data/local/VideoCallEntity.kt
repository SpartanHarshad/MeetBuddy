package com.harshad.meetbuddy.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Video_Call_Log")
data class VideoCallEntity(
    @ColumnInfo("remote_user_id") var remotedUserId: String,
    @ColumnInfo("v_call_time_stamp") var callTimeStamp: String,
    @ColumnInfo("v_call_duration") var callDuration: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "vcId")
    var rId: Int = 0
}
