package net.angrycode.capture.ext

import android.content.Context

/**
 * Created by pc on 2017/12/29.
 */

val SETTINGS = "settings"
val VIDEO_SIZE = "video_size"
val SHOW_COUNT_DOWN = "show_count_down"
val HIDE_FROM_RECENTS = "switch_hide_from_recents"
val RECORDING_NOTIFICATION = "switch_recording_notification"
val SHOW_TOUCHES = "switch_show_touches"
val SHOW_DEMO_MODE = "switch_show_demo_mode"

val Context.sharedPreferences
    get() = applicationContext.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)

var Context.videoSize
    get() = sharedPreferences.getInt(VIDEO_SIZE, 100)
    set(value) = sharedPreferences.edit().putInt(VIDEO_SIZE, value).apply()

var Context.showCountDown
    get() = sharedPreferences.getBoolean(SHOW_COUNT_DOWN, false)
    set(value) = sharedPreferences.edit().putBoolean(SHOW_COUNT_DOWN, value).apply()

var Context.hideFromRecents
    get() = sharedPreferences.getBoolean(HIDE_FROM_RECENTS, false)
    set(value) = sharedPreferences.edit().putBoolean(HIDE_FROM_RECENTS, value).apply()

var Context.showRecordingNotification
    get() = sharedPreferences.getBoolean(RECORDING_NOTIFICATION, false)
    set(value) = sharedPreferences.edit().putBoolean(RECORDING_NOTIFICATION, value).apply()

var Context.showTouches
    get() = sharedPreferences.getBoolean(SHOW_TOUCHES, false)
    set(value) = sharedPreferences.edit().putBoolean(SHOW_TOUCHES, value).apply()

var Context.showDemoMode
    get() = sharedPreferences.getBoolean(SHOW_DEMO_MODE, false)
    set(value) = sharedPreferences.edit().putBoolean(SHOW_DEMO_MODE, value).apply()

