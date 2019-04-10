package xtech.selfomat

import android.app.Application
import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins

class SelfomatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Set the global error handler for all errors which might occur during reactive streams. If we don't do this the app will crash
        // even if everything is allright (e.g. data becomes available after the user has closed the app)
        RxJavaPlugins.setErrorHandler { e ->
            e ?: return@setErrorHandler
            Log.e("selfomat", e.message, e)
            when(e) {
                is UndeliverableException -> {
                    // We don't care
                }
                is IllegalStateException,
                is IllegalArgumentException,
                is NullPointerException -> {
                    // We still don't care
                }
                else -> {
                    // We don't care
                }
            }
        }
    }
}