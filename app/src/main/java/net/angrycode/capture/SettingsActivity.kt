package net.angrycode.capture

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_settings.*
import net.angrycode.capture.base.BaseActivity
import net.angrycode.capture.ext.*


/**
 * This Project is Fork from https://github.com/JakeWharton/Telecine,
 * and rewrite it by Kotlin.
 */
class SettingsActivity : BaseActivity() {

    private lateinit var videoSizePercentageAdapter: VideoSizePercentageAdapter
    private lateinit var showDemoModeSetting: DemoModeHelper.ShowDemoModeSetting

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        CheatSheet.setup(launch)

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

//        launch.setOnClickListener { _ ->
//            captureWithPermission()
////            fireScreenCaptureIntent()
//        }

//        videoListBtn.setOnClickListener {
//            CaptureListActivity.start(this)
//        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!handleActivityResult(requestCode, resultCode, data) &&
                !DemoModeHelper.handleActivityResult(this, requestCode, showDemoModeSetting)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
