package net.angrycode.capture

import android.app.Activity
import android.content.Context.MEDIA_PROJECTION_SERVICE
import android.content.Intent
import android.media.projection.MediaProjectionManager
import timber.log.Timber

/**
 * capture helper
 * Created by wecodexyz@gmail.com on 2017/12/30.
 */

val CREATE_SCREEN_CAPTURE = 4242

fun Activity.fireScreenCaptureIntent() {
    val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    val intent = manager.createScreenCaptureIntent()
    startActivityForResult(intent, CREATE_SCREEN_CAPTURE)
}

fun Activity.handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (requestCode != CREATE_SCREEN_CAPTURE) {
        return false
    }
    if (resultCode == Activity.RESULT_OK) {
        Timber.d("Acquired permission to screen capture. Starting service.")
        startService(CaptureService.newIntent(this, resultCode, data))
    } else {
        Timber.d("Failed to acquire permission to screen capture.")
    }
    return true
}