package com.izyver.gati.data

import androidx.annotation.WorkerThread
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.data.database.ImageEntity

interface ImageRepository {

    @WorkerThread
    fun getAllImages(): List<ImageEntity>

    @WorkerThread
    fun getImageBy(day: Days): ImageEntity

    @WorkerThread
    fun saveImages(images: List<ImageEntity>)

}