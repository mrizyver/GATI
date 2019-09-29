package com.izyver.gati.data

import com.izyver.gati.bussines.models.Days
import com.izyver.gati.data.database.ImageEntity

class TestRepository: ImageRepository {
    override fun getAllImages(): List<ImageEntity> {
    }

    override fun getImageBy(day: Days): ImageEntity {

    }

    override fun saveImages(images: List<ImageEntity>) {

    }
}