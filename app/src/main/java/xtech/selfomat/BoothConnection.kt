package xtech.selfomat

import android.util.Log
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class BoothConnection {
    private val client = OkHttpClient()

    fun loadCurrentSettings(): List<ListSetting> {
        val request = Request.Builder()
            .url("http://192.168.178.139:9080/camera_settings")
            .get()
            .build()

        val response = client.newCall(request).execute()

        if(response.isSuccessful) {
            // try to parse the camera settings
            val settings = Api.CameraSettings.parseFrom(response.body()?.byteStream())
            return settings.allFields.map {
                if (it.value is Api.ListSetting) {
                    val setting = it.value as Api.ListSetting
                    val values = if(setting.valuesList.isNotEmpty()) {
                        setting.valuesList
                    } else {
                        listOf(setting.currentIndex.toString())
                    }
                    ListSetting(setting.name, setting.currentIndex, values, setting.updateUrl)
                } else {
                    null
                }
            }.filterNotNull()
        }

        return emptyList()
    }

    fun updateSetting(url: String, value: Int) {
        val updatePayload = Api.IntUpdate.newBuilder()
            .setValue(value.toLong()).build()

        val request = Request.Builder()
            .url("http://192.168.178.139:9080$url")
            .post(RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), updatePayload.toByteArray()))
            .build()

        client.newCall(request).execute()
    }

    fun updateSetting(url: String, value: Long) {
        val updatePayload = Api.IntUpdate.newBuilder().setValue(value).build()

        val request = Request.Builder()
            .url("http://192.168.178.139:9080$url")
            .post(RequestBody.create(null, updatePayload.toByteArray()))
            .build()

        client.newCall(request).execute()
    }

    fun updateSetting(url: String, value: Boolean) {
        val updatePayload = Api.BoolUpdate.newBuilder().setValue(value).build()

        val request = Request.Builder()
            .url("http://192.168.178.139:9080$url")
            .post(RequestBody.create(null, updatePayload.toByteArray()))
            .build()

        client.newCall(request).execute()
    }

    fun updateSetting(url: String, value: Float) {
        val updatePayload = Api.FloatUpdate.newBuilder().setValue(value).build()

        val request = Request.Builder()
            .url("http://192.168.178.139:9080$url")
            .post(RequestBody.create(null, updatePayload.toByteArray()))
            .build()

        client.newCall(request).execute()
    }

    fun triggerCapture() {
        val request = Request.Builder()
            .url("http://192.168.178.139:9080/trigger")
            .post(RequestBody.create(null, ""))
            .build()

        client.newCall(request).execute()
    }

    fun triggerFocus() {
        val request = Request.Builder()
            .url("http://192.168.178.139:9080/focus")
            .post(RequestBody.create(null, ""))
            .build()

        client.newCall(request).execute()
    }




}