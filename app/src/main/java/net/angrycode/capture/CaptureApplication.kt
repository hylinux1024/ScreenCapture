package net.angrycode.capture

import android.app.Activity
import android.app.Application
import timber.log.Timber
import java.util.*

/**
 * Created by wecodexyz@gmail.com on 2017/12/28.
 */

class CaptureApplication : Application() {
    val stacks by lazy { Stack<Activity>() }
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    fun getCurrentActivity(): Activity {
        return stacks.peek()
    }

}