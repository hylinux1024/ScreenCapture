package net.angrycode.capture

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 * Created by pc on 2017/12/31.
 */
class CaptureShortcutConfigureActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        analytics.send(HitBuilders.EventBuilder() //
//                .setCategory(Analytics.CATEGORY_SHORTCUT) //
//                .setAction(Analytics.ACTION_SHORTCUT_ADDED) //
//                .build())

        val launchIntent = Intent(this, CaptureShortcutLaunchActivity::class.java)
        val icon = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher)

        val intent = Intent()
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut_name))
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon)
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}