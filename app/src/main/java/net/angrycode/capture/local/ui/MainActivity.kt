package net.angrycode.capture.local.ui

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
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import net.angrycode.capture.BuildConfig
import net.angrycode.capture.R
import net.angrycode.capture.base.BaseActivity
import net.angrycode.capture.ext.hideFromRecents
import net.angrycode.capture.fireScreenCaptureIntent
import net.angrycode.capture.handleActivityResult
import org.jetbrains.anko.doFromSdk
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

/**
 * Created by pc on 2018/1/3.
 */
class MainActivity : BaseActivity() {
//    private lateinit var showDemoModeSetting: DemoModeHelper.ShowDemoModeSetting

    private val appName by lazy { resources.getString(R.string.app_name) }

    private val primaryNormal by lazy { ContextCompat.getColor(applicationContext, R.color.primary_normal) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        supportFragmentManager.beginTransaction().replace(R.id.container, CaptureListFragment()).commitAllowingStateLoss()

    }

    private fun init() {
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        doFromSdk(Build.VERSION_CODES.LOLLIPOP) {
            setTaskDescription(ActivityManager.TaskDescription(appName, rasterizeTaskIcon(), primaryNormal))
        }
        val tvVersion = navigation.getHeaderView(0).findViewById<TextView>(R.id.tvVersion)
        val version = "V${BuildConfig.VERSION_NAME}"
        tvVersion.text = version

//        showDemoModeSetting = object : DemoModeHelper.ShowDemoModeSetting {
//            override fun show() {
//            }
//
//            override fun hide() {
//            }
//        }
//        DemoModeHelper.showDemoModeSetting(this, showDemoModeSetting)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
                if (!handleActivityResult(requestCode, resultCode, data) /*&&
                        !DemoModeHelper.handleActivityResult(this, requestCode, showDemoModeSetting)*/) {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
    fun captureWithPermission() {

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


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDestroy() {
        super.onDestroy()
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

}