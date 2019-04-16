package xtech.selfomat

import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

open class Setting<T : Any>(val name: String, val updateValue: T, val updateURL: String?) {

    var backendSubject: Subject<Setting<out Any>>? = null

    private val updateSubject: Subject<T> = PublishSubject.create<T>().apply {
        doOnNext {
            liveUpdateValue.postValue(it)
            liveDisplayValue.postValue(toDisplayValue(it))
        }
        subscribeOn(Schedulers.io())
    }

    val liveDisplayValue: MutableLiveData<String> = MutableLiveData()
    val liveUpdateValue: MutableLiveData<T> = MutableLiveData()

    init {
        updateSubject.subscribe {
            // Send it to the backend
            backendSubject?.onNext(this@Setting)
        }
    }

    fun update(newValue: T) {
        updateSubject.onNext(newValue)
    }

    open fun toDisplayValue(updateValue: T) = updateValue.toString()
}

class ListSetting(name: String, val currentIndex: Int, val possibleValues: List<String>, update: String?) :
    Setting<Int>(name, currentIndex, update) {

    override fun toDisplayValue(updateValue: Int) = possibleValues[updateValue]
}


class BoolSetting(name: String, currentValue: Boolean, update: String?) :
    Setting<Boolean>(name, currentValue, update) {
    fun toggle() = update(!(updateValue as Boolean))
}

class LongSetting(name: String, currentValue: Long, update: String?) :
    Setting<Long>(name, currentValue, update)

class FloatSetting(name: String, currentValue: Float, update: String?) :
    Setting<Float>(name, currentValue, update)
