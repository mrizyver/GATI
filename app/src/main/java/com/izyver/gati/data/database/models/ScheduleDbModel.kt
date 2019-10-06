package com.izyver.gati.data.database.models

interface ScheduleDbModel {
    var key: String?
    var id: Int?
    var date: String?
    var type: Int?
    var title: String?
    var dayWeek: String?
}