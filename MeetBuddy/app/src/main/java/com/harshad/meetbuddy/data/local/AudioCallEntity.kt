package com.harshad.meetbuddy.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Audio_Call_Log")
data class AudioCallEntity(
    @ColumnInfo("remote_user_id") var remotedUserId: String,
    @ColumnInfo("a_call_time_stamp") var callTimeStamp: String,
    @ColumnInfo("a_call_duration") var callDuration: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "acId")
    var rId: Int = 0
}
