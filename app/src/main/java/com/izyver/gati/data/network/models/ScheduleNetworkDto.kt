package com.izyver.gati.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ScheduleNetworkDto(
        @SerializedName("id")
        @Expose
        var id: Int,
        @SerializedName("title")
        @Expose
        var title: String? = null,
        @SerializedName("image")
        @Expose
        var image: String? = null,
        @SerializedName("type")
        @Expose
        var type: Int,
        @SerializedName("date")
        @Expose
        var date: String? = null,
        @SerializedName("day_week")
        @Expose
        var dayWeek: String? = null
)