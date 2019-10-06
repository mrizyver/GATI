package com.izyver.gati.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ScheduleObject(
        @SerializedName("day")
        @Expose
        var day: List<ScheduleNetworkDto> = arrayListOf(),
        @SerializedName("zao")
        @Expose
        var zao: List<ScheduleNetworkDto> = arrayListOf()
)