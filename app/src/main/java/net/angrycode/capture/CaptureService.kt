package net.angrycode.capture

import android.app.Notification
import android.app.Notification.PRIORITY_MIN
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.provider.Settings
import android.support.v4.content.ContextCompat
import com.nightlynexus.demomode.*
import net.angrycode.capture.ext.*
import timber.log.Timber
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

/**
 * capture service
 * Created by wecodexyz@gmail.com on 2017/12/30.
 */
class CaptureService : Service() {
    private var running = false
    private lateinit var recordingSession: RecordingSession

    private val listener = object : RecordingSession.Listener {
        override fun onPrepare() {
            if (showDemoMode) {
                sendBroadcast(BarsBuilder().mode(BarsBuilder.BarsMode.TRANSPARENT).build())
                sendBroadcast(BatteryBuilder().level(100).plugged(FALSE).build())
                sendBroadcast(ClockBuilder().setTimeInHoursAndMinutes("1200").build())
                sendBroadcast(NetworkBuilder().airplane(FALSE)
                        .carrierNetworkChange(FALSE)
                        .mobile(TRUE, NetworkBuilder.Datatype.LTE, 0, 4)
                        .nosim(FALSE)
                        .build())
                sendBroadcast(NotificationsBuilder().visible(FALSE).build())
                sendBroadcast(SystemIconsBuilder().alarm(FALSE)
                        .bluetooth(SystemIconsBuilder.BluetoothMode.HIDE)
                        .cast(FALSE)
                        .hotspot(FALSE)
                        .location(FALSE)
                        .mute(FALSE)
                        .speakerphone(FALSE)
                        .tty(FALSE)
                        .vibrate(FALSE)
                        .zen(SystemIconsBuilder.ZenMode.HIDE)
                        .build())
                sendBroadcast(WifiBuilder().fully(TRUE).wifi(TRUE, 4).build())
            }
        }

        override fun onStart() {
            if (showTouches) {
                Settings.System.putInt(contentResolver, SHOW_TOUCHES, 1)
            }

            if (!showRecordingNotification) {
                return  // No running notification was requested.
            }

            val context = applicationContext
            val title = context.getString(R.string.notification_recording_title)
            val subtitle = context.getString(R.string.notification_recording_subtitle)
            val notification = Notification.Builder(context) //
                    .setContentTitle(title)
                    .setContentText(subtitle)
                    .setSmallIcon(R.drawable.ic_videocam_white_24dp)
                    .setColor(ContextCompat.getColor(context, R.color.primary_normal))
                    .setAutoCancel(true)
                    .setPriority(PRIORITY_MIN)
                    .build()

            Timber.d("Moving service into the foreground with recording notification.")
            startForeground(NOTIFICATION_ID, notification)
        }

        override fun onStop() {
            if (showTouches) {
                Settings.System.putInt(contentResolver, SHOW_TOUCHES, 0)
            }
            if (showDemoMode) {
                sendBroadcast(DemoMode.buildExit())
            }
        }

        override fun onEnd() {
            Timber.d("Shutting down.")
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (running) {
            Timber.d("Already running! Ignoring...")
            return Service.START_NOT_STICKY
        }
        Timber.d("Starting up!")
        running = true

        val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0)
        val data = intent.getParcelableExtra<Intent>(EXTRA_DATA)
        if (resultCode == 0 || data == null) {
            throw IllegalStateException("Result code or data missing.")
        }

        recordingSession = RecordingSession(this, listener, resultCode, data, showCountDown, videoSize)
        recordingSession.showOverlay()
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private val EXTRA_RESULT_CODE = "result-code"
        private val EXTRA_DATA = "data"
        private val NOTIFICATION_ID = 99118822
        private val SHOW_TOUCHES = "show_touches"
        fun newIntent(context: Context, resultCode: Int, data: Intent?): Intent {
            val intent = Intent(context, CaptureService::class.java)
            intent.putExtra(EXTRA_DATA, data)
            intent.putExtra(EXTRA_RESULT_CODE, resultCode)
            return intent
        }
    }
}