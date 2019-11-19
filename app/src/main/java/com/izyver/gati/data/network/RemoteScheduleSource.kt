package com.izyver.gati.data.network

import com.izyver.gati.data.network.models.ScheduleNetworkDto
import com.izyver.gati.utils.buildUrlForImage
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL

abstract class RemoteScheduleSource : IRemoteScheduleDataSource {
    override suspend fun download(schedule: ScheduleNetworkDto): ByteArray? {
        val imageNAme = schedule.image ?: return null
        return try {
            val url = buildUrlForImage(imageNAme)
            val inputStream = BufferedInputStream(URL(url).openStream())
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead = 0
            val outputStream = ByteArrayOutputStream()
            while (true) {
                bytesRead = inputStream.read(buffer, 0, BUFFER_SIZE)
                if (bytesRead == -1) break
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.toByteArray()
        } catch (e: IOException) {
            null
        }
    }

    companion object {
        private const val BUFFER_SIZE = 1024
    }
}