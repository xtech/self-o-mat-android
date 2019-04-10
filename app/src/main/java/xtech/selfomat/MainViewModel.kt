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
        .doOnSuccess {
            liveList.postValue(it)
        }


    fun loadSettings() {
        requestObservable.subscribe()
    }

    fun updateSetting(url: String, value: Int) {
        Single.fromCallable { connection.updateSetting(url, value) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun triggerCapture() {
        Single.fromCallable { connection.triggerCapture() }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}