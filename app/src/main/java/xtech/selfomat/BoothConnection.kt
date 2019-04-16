package xtech.selfomat

import android.util.Log
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class BoothConnection {
    private val client = OkHttpClient()

    private val backendSubject: Subject<Setting<out Any>> = PublishSubject.create()

    private inline fun convertSetting(setting: Any): Setting<out Any>? = when (setting) {
        is Api.ListSetting -> {
            val values = if (setting.valuesList.isNotEmpty()) {
                setting.valuesList
            } else {
                listOf(setting.currentIndex.toString())
            }
            ListSetting(setting.name, setting.currentIndex, values, setting.updateUrl)
        }
        is Api.BoolSetting -> BoolSetting(setting.name, setting.currentValue, setting.updateUrl)
        is Api.FloatSetting -> FloatSetting(setting.name, setting.currentValue, setting.updateUrl)
        is Api.IntSetting -> LongSetting(setting.name, setting.currentValue, setting.updateUrl)
        is Api.ReadOnlySetting -> Setting(setting.name, setting.value, setting.value)
        else -> {
            Log.d("selfomat", "Unknown setting type: ${setting.javaClass.name}")
            null
        }
    }?.apply {
        this.backendSubject = this@BoothConnection.backendSubject
    }

    fun loadCameraSettings(): List<Setting<out Any>> {
        val request = Request.Builder()
            .url("http://192.168.178.139:9080/camera_settings")
            .get()
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            // try to parse the camera settings
            val settings: Api.CameraSettings = Api.CameraSettings.parseFrom(response.body()?.byteStream())
            return settings.allFields.map {
                convertSetting(it.value)
            }.filterNotNull()
        }

        return emptyList()
    }

    fun loadBoothSettings(): List<Setting<out Any>> {

        val request = Request.Builder()
            .url("http://192.168.178.139:9080/booth_settings")
            .get()
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            // try to parse the camera settings
            val settings: Api.BoothSettings = Api.BoothSettings.parseFrom(response.body()?.byteStream())
            return settings.allFields.map {
                convertSetting(it.value)
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

    fun updateSetting(setting: Setting<out Any>) {
        setting.updateURL ?: return
        when (setting.updateValue) {
            is Float -> updateSetting(setting.updateURL, setting.updateValue)
            is Int -> updateSetting(setting.updateURL, setting.updateValue)
            is Long -> updateSetting(setting.updateURL, setting.updateValue)
            is Boolean -> updateSetting(setting.updateURL, setting.updateValue)
            else -> Log.d("selfomat", "setting with unknownt type: ${setting.updateValue.javaClass.simpleName}")
        }
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