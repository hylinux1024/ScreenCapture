package net.angrycode.capture

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport
import timber.log.Timber

/**
 * Created by wecodexyz@gmail.com on 2017/12/28.
 */

class CaptureApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        CrashReport.initCrashReport(this, BuildConfig.BUGLY_APP_ID, BuildConfig.DEBUG)
    }
}