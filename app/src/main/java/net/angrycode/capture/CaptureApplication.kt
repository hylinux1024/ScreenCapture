package net.angrycode.capture

import android.app.Application
import timber.log.Timber

/**
 * Created by wecodexyz@gmail.com on 2017/12/28.
 */

class CaptureApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}