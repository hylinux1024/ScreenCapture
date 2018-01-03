package net.angrycode.capture

import android.Manifest
import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import net.angrycode.capture.base.BaseActivity
import net.angrycode.capture.ext.*
import net.angrycode.capture.local.ui.CaptureListActivity
import org.jetbrains.anko.doFromSdk
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber


/**
 * This Project is Fork from https://github.com/JakeWharton/Telecine,
 * and rewrite it by Kotlin.
 */
class MainActivity : BaseActivity() {

    private lateinit var videoSizePercentageAdapter: VideoSizePercentageAdapter
    private lateinit var showDemoModeSetting: DemoModeHelper.ShowDemoModeSetting

    private val appName by lazy { resources.getString(R.string.app_name) }

    private val primaryNormal by lazy { ContextCompat.getColor(applicationContext, R.color.primary_normal) }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CheatSheet.setup(launch)
        doFromSdk(Build.VERSION_CODES.LOLLIPOP) {
            setTaskDescription(ActivityManager.TaskDescription(appName, rasterizeTaskIcon(), primaryNormal))
        }
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
            captureWithPermission()
//            fireScreenCaptureIntent()
        }

        videoListBtn.setOnClickListener {
            CaptureListActivity.start(this)
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_OVERLAY_PERMISSIONS -> {
                if (!Settings.canDrawOverlays(this)) {
                    promptShowDialog(getString(R.string.rationale_ask_again), { requestOverlayPermission() })
                } else {
                    if (Settings.System.canWrite(this)) {
                        fireScreenCaptureIntent()
                    } else {
                        promptShowDialog(getString(R.string.rationale_ask), { requestWriteSettingsPermission() })
                    }
                }
            }
            REQUEST_CODE_WRITE_SETTINGS_PERMISSIONS -> {
                if (!Settings.System.canWrite(this)) {
                    promptShowDialog(getString(R.string.rationale_ask_again), { requestOverlayPermission() })
                } else {
                    if (Settings.canDrawOverlays(this)) {
                        fireScreenCaptureIntent()
                    } else {
                        promptShowDialog(getString(R.string.rationale_ask), { requestWriteSettingsPermission() })
                    }
                }
            }
            else -> {
                if (!handleActivityResult(requestCode, resultCode, data) &&
                        !DemoModeHelper.handleActivityResult(this, requestCode, showDemoModeSetting)) {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }


    }

    private fun rasterizeTaskIcon(): Bitmap {
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_videocam_white_24dp)

        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val size = am.launcherLargeIconSize
        val icon = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(icon)
        drawable?.setBounds(0, 0, size, size)
        drawable?.draw(canvas)

        return icon
    }

    @TargetApi(Build.VERSION_CODES.M)
    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
    private fun captureWithPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val perms = Manifest.permission.READ_EXTERNAL_STORAGE
            if (!EasyPermissions.hasPermissions(this, perms)) {
                Timber.d("Attempting to acquire external storage permission.")
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_ask), REQUEST_CODE_PERMISSIONS, perms)
            } else {
                val canDraw = Settings.canDrawOverlays(this)
                val canWrite = Settings.System.canWrite(this)
                if (canDraw && canWrite) {
                    Timber.d("Attempting to acquire permission to screen capture.")
                    fireScreenCaptureIntent()
                } else {
                    if (!canDraw) {
                        Timber.d("Attempting to acquire draw overlay permission.")
                        promptShowDialog(getString(R.string.rationale_ask), { requestOverlayPermission() })
                    } else if (!canWrite) {
                        Timber.d("Attempting to acquire write settings permission.")
                        promptShowDialog(getString(R.string.rationale_ask), { requestWriteSettingsPermission() })
                    }
                }
            }
        } else {
            Timber.d("Attempting to acquire permission to screen capture.")
            fireScreenCaptureIntent()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSIONS)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestWriteSettingsPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName"))
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS_PERMISSIONS)
    }

    private fun promptShowDialog(message: String, request: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setNegativeButton(R.string.cancel, null)
        builder.setPositiveButton(R.string.acquire_permission, { _, _ ->
            request()
        })
        builder.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStop() {
        super.onStop()
        if (hideFromRecents && !isChangingConfigurations) {
            Timber.d("Removing task because hide from recents preference was enabled.")
            doFromSdk(Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 100
        const val REQUEST_CODE_OVERLAY_PERMISSIONS = 101
        const val REQUEST_CODE_WRITE_SETTINGS_PERMISSIONS = 102
    }
}
