package net.angrycode.capture

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber

/**
 * Created by pc on 2017/12/30.
 */
class DeleteRecordingBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(RecordingSession.NOTIFICATION_ID)
        val uri = intent?.data
        val contentResolver = context.contentResolver
        doAsync {
            val rowsDeleted = contentResolver.delete(uri, null, null)
            if (rowsDeleted == 1) {
                Timber.i("Deleted recording.")
            } else {
                Timber.e("Error deleting recording.")
            }
            uiThread {
                Timber.d("Do nothing.")
            }
        }
    }
}