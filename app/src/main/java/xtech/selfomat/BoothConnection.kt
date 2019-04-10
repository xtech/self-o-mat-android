package xtech.selfomat

import okhttp3.OkHttpClient
import okhttp3.Request

class BoothConnection {
    private val client = OkHttpClient()

    fun loadCurrentSettings(): List<Setting> {
        val request = Request.Builder()
            .url("http://192.168.178.138:9080/camera_settings")
            .get()
            .build()

        val response = client.newCall(request).execute()

        if(response.isSuccessful) {
            // try to parse the camera settings
            return Api.CameraSettings.parseFrom(response.body()?.byteStream()).allFields.map {
                Setting(it.key.name, 0, listOf(it.value.toString()))
            }
        }

        return emptyList()
    }
}