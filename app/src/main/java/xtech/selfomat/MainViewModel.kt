package xtech.selfomat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {
    val liveCameraSettings = MutableLiveData<List<Setting<out Any>>>()
    val liveBoothSettings = MutableLiveData<List<Setting<out Any>>>()
    private val connection = BoothConnection()


    private var requestObservable =
        Single.fromCallable {
            Pair(connection.loadCameraSettings(), connection.loadBoothSettings())
        }.subscribeOn(Schedulers.io())
        .doOnSuccess {
            val completeList = mutableListOf<Setting<out Any>>()
            completeList.addAll(it.first)
            completeList.addAll(it.second)
            liveCameraSettings.postValue(completeList)
        }


    fun loadSettings() {
        requestObservable.subscribe()
    }

    fun updateSetting(updated: Setting<out Any>) {
        updated.updateURL ?: return
        Single.fromCallable { connection.updateSetting(updated) }
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                val currentList = liveCameraSettings.value ?: return@doOnSuccess
                liveCameraSettings.postValue(currentList.map {
                    if (it.updateURL == updated.updateURL) updated else it
                })
            }
            .subscribe()
    }

    fun triggerCapture() {
        Single.fromCallable { connection.triggerCapture() }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}