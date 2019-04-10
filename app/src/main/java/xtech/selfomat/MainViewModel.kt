package xtech.selfomat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {
    val liveList = MutableLiveData<List<Setting>>()
    private val connection = BoothConnection()

    private var requestObservable = Single.fromCallable {
        Log.d("self-o-mat", "querying data")
        connection.loadCurrentSettings()
    }.subscribeOn(Schedulers.io())
        .doOnError { }
        .doOnSuccess { liveList.postValue(it) }

    fun updateSetting(setting: Setting, newValue: String) {
        val currentList = liveList.value ?: return
        liveList.postValue(currentList.map { if (setting == it) it.copy(value = newValue) else it })
    }

    fun loadSettings() {
        requestObservable.subscribe()
    }
}