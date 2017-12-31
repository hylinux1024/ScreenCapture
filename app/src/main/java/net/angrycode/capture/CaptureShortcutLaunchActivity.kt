package net.angrycode.capture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Created by pc on 2017/12/31.
 */
class CaptureShortcutLaunchActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireScreenCaptureIntent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (!handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
        finish()
    }

    override fun onStop() {
        if (!isFinishing) {
            finish()
        }
        super.onStop()
    }

    companion object {
        private val KEY_ACTION = "launch-action"

        fun createQuickTileIntent(context: Context): Intent {
            val intent = Intent(context, CaptureShortcutLaunchActivity::class.java)
//            intent.putExtra(KEY_ACTION, Analytics.ACTION_QUICK_TILE_LAUNCHED)
            return intent
        }
    }

}