package com.izyver.gati.data.network

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

data class ScheduleNetworkDto(
        @SerializedName("id")
        @Expose
        var id: Int? = null,
        @SerializedName("title")
        @Expose
        var title: String? = null,
        @SerializedName("image")
        @Expose
        var image: String? = null,
        @SerializedName("type")
        @Expose
        var type: Int? = null,
        @SerializedName("date")
        @Expose
        var date: String? = null,
        @SerializedName("day_week")
        @Expose
        var dayWeek: String? = null
)