package net.angrycode.capture

import android.app.Activity
import android.content.Context
import android.content.Context.MEDIA_PROJECTION_SERVICE
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Environment
import android.os.Environment.DIRECTORY_MOVIES
import android.provider.MediaStore
import net.angrycode.capture.local.entity.Video
import timber.log.Timber
import java.io.File

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

fun getRootDir(): File {
    val picturesDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES)
    return File(picturesDir, "ScreenCapture")
}

fun Context.listLocalVideos(): List<Video> {
    val root = getRootDir()
    val cursor = contentResolver?.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER)
    val localList = arrayListOf<Video>()
    while (cursor != null && cursor.moveToNext()) {
        val title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
        val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
        val createTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))
        val thumb = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
        val dir = File(path)

        if (dir.parentFile != root || !path.endsWith(".mp4")) {
            continue
        }

        val video = Video(title, path, thumb, createTime, duration)
        localList.add(video)
    }
    cursor?.close()
    return localList
}