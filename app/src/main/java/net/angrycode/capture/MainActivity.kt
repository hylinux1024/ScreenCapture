package net.angrycode.capture

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import net.angrycode.capture.ext.*
import timber.log.Timber

/**
 * This Project is Fork from https://github.com/JakeWharton/Telecine,
 * and rewrite it by Kotlin.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var videoSizePercentageAdapter: VideoSizePercentageAdapter
    private lateinit var showDemoModeSetting: DemoModeHelper.ShowDemoModeSetting

    private val appName by lazy { resources.getString(R.string.app_name) }

    private val primaryNormal by lazy { ContextCompat.getColor(applicationContext, R.color.primary_normal) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getApp().stacks.push(this)

        setTaskDescription(ActivityManager.TaskDescription(appName, rasterizeTaskIcon(), primaryNormal))
        initView()
        setListener()
    }

    private fun initView() {
        videoSizePercentageAdapter = VideoSizePercentageAdapter(this)
        spinner_video_size_percentage.adapter = videoSizePercentageAdapter
        spinner_video_size_percentage.setSelection(VideoSizePercentageAdapter.getSelectedPosition(videoSize))

        switch_show_countdown.isChecked = showCountDown
        switch_hide_from_recents.isChecked = hideFromRecents
        switch_recording_notification.isChecked = showRecordingNotification
        switch_show_touches.isChecked = showTouches
        switch_use_demo_mode.isChecked = showDemoMode

        showDemoModeSetting = object : DemoModeHelper.ShowDemoModeSetting {
            override fun show() {
                switch_use_demo_mode.visibility = (View.VISIBLE)
            }

            override fun hide() {
                switch_use_demo_mode.isChecked = (false)
                container_use_demo_mode.visibility = (View.GONE)
            }
        }
        DemoModeHelper.showDemoModeSetting(this, showDemoModeSetting)
    }

    private fun setListener() {
        spinner_video_size_percentage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newValue = videoSizePercentageAdapter.getItem(position) as Int
                val oldValue = videoSize
                if (newValue != oldValue) {
                    videoSize = newValue
                }
            }
        }
        switch_show_countdown.setOnCheckedChangeListener { _, isChecked ->
            if (showCountDown != isChecked) showCountDown = isChecked
        }
        switch_hide_from_recents.setOnCheckedChangeListener { _, isChecked -> if (hideFromRecents != isChecked) hideFromRecents = isChecked }
        switch_recording_notification.setOnCheckedChangeListener { _, isChecked -> if (showRecordingNotification != isChecked) showRecordingNotification = isChecked }
        switch_show_touches.setOnCheckedChangeListener { _, isChecked -> if (showTouches != isChecked) showTouches = isChecked }
        switch_use_demo_mode.setOnCheckedChangeListener { _, isChecked -> if (showDemoMode != isChecked) showDemoMode = isChecked }

        launch.setOnClickListener { _ ->
            Timber.d("Attempting to acquire permission to screen capture.")
            fireScreenCaptureIntent()
        }

    }

    private fun rasterizeTaskIcon(): Bitmap {
        val drawable = resources.getDrawable(R.drawable.ic_videocam_white_24dp, theme)

        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val size = am.launcherLargeIconSize
        val icon = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(icon)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)

        return icon
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!handleActivityResult(requestCode, resultCode, data) &&
                !DemoModeHelper.handleActivityResult(this, requestCode, showDemoModeSetting)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStop() {
        super.onStop()
        if (hideFromRecents && !isChangingConfigurations) {
            Timber.d("Removing task because hide from recents preference was enabled.")
            finishAndRemoveTask()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getApp().stacks.remove(this)
    }
}
