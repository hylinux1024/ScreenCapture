package net.angrycode.capture

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat.TRANSLUCENT
import android.os.Build
import android.text.TextUtils
import android.text.TextUtils.getLayoutDirectionFromLocale
import android.view.Gravity
import android.view.View
import android.view.ViewAnimationUtils.createCircularReveal
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.overlay_view.view.*
import net.angrycode.capture.ext.dimen
import net.angrycode.capture.ext.dp2px
import java.util.*

/**
 * Created by pc on 2017/12/30.
 */
@SuppressLint("ViewConstructor")  // Lint, in this case, I am smarter than you.
class OverlayView(context: Context, private var listener: Listener, private var showCountDown: Boolean) : FrameLayout(context) {

    private var animationWidth: Int = 0

    init {
        View.inflate(context, R.layout.overlay_view, this)
        animationWidth = dp2px(dimen(R.dimen.overlay_width)).toInt()
        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL) {
            animationWidth = -animationWidth // Account for animating in from the other side of screen.
        }
        CheatSheet.setup(record_overlay_cancel)
        CheatSheet.setup(record_overlay_start)

        record_overlay_cancel.setOnClickListener { onCancelClicked() }
        record_overlay_start.setOnClickListener { onStartClicked() }
    }

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        val lp = layoutParams
        lp.height = if (insets?.systemWindowInsetTop != null) insets.systemWindowInsetTop else 0

        listener.onResize()

        return if (insets?.consumeSystemWindowInsets() != null) insets.consumeSystemWindowInsets() else super.onApplyWindowInsets(insets)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        translationX = animationWidth.toFloat()
        animate().translationX(0f)
                .setDuration(DURATION_ENTER_EXIT.toLong()).interpolator = DecelerateInterpolator()
    }

    private fun onCancelClicked() {
        animate().translationX(animationWidth.toFloat())
                .setDuration(DURATION_ENTER_EXIT.toLong())
                .setInterpolator(AccelerateInterpolator())
                .withEndAction { listener.onCancel() }
    }

    private fun onStartClicked() {
        record_overlay_recording.visibility = View.VISIBLE
        val centerX = (record_overlay_start.x + record_overlay_start.width / 2).toInt()
        val centerY = (record_overlay_start.y + record_overlay_start.height / 2).toInt()
        val reveal = createCircularReveal(record_overlay_recording, centerX, centerY, 0f, width / 2f)
        reveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                record_overlay_buttons.visibility = View.GONE
            }
        })
        reveal.start()

        postDelayed({
            if (showCountDown) {
                showCountDown()
            } else {
                countdownComplete()
            }
        }, (if (showCountDown) COUNTDOWN_DELAY else NON_COUNTDOWN_DELAY).toLong())
    }

    private fun startRecording() {
        record_overlay_recording.visibility = View.INVISIBLE
        record_overlay_stop.visibility = View.VISIBLE
        record_overlay_stop.setOnClickListener({ listener.onStop() })
        listener.onStart()
    }

    private fun showCountDown() {
        val countdown = resources.getStringArray(R.array.countdown)
        countdown(countdown, 0) // array resource must not be empty
    }

    private fun countdownComplete() {
        listener.onPrepare()
        record_overlay_recording.animate()
                .alpha(0f)
                .setDuration(COUNTDOWN_DELAY.toLong())
                .withEndAction({ startRecording() })
    }

    private fun countdown(countdownArr: Array<String>, index: Int) {
        postDelayed({
            record_overlay_recording.text = countdownArr[index]
            if (index < countdownArr.size - 1) {
                countdown(countdownArr, index + 1)
            } else {
                countdownComplete()
            }
        }, COUNTDOWN_DELAY.toLong())
    }

    companion object {
        private val COUNTDOWN_DELAY = 1000
        private val NON_COUNTDOWN_DELAY = 500
        private val DURATION_ENTER_EXIT = 300

        fun create(context: Context, listener: Listener, showCountDown: Boolean): OverlayView {
            return OverlayView(context, listener, showCountDown)
        }

        fun createLayoutParams(context: Context): WindowManager.LayoutParams {
            val width = context.resources.getDimensionPixelSize(R.dimen.overlay_width)

            val params = WindowManager.LayoutParams(width, WindowManager.LayoutParams.WRAP_CONTENT, TYPE_SYSTEM_ERROR, FLAG_NOT_FOCUSABLE
                    or FLAG_NOT_TOUCH_MODAL
                    or FLAG_LAYOUT_NO_LIMITS
                    or FLAG_LAYOUT_INSET_DECOR
                    or FLAG_LAYOUT_IN_SCREEN, TRANSLUCENT)
            if (Build.VERSION.SDK_INT >= 22) {
                params.type = TYPE_TOAST
            } else if (Build.VERSION.SDK_INT >= 26) {
                params.type = TYPE_APPLICATION_OVERLAY
            }
            params.gravity = Gravity.TOP or gravityEndLocaleHack()

            return params
        }

        @SuppressLint("RtlHardcoded") // Gravity.END is not honored by WindowManager for added views.
        private fun gravityEndLocaleHack(): Int {
            val direction = getLayoutDirectionFromLocale(Locale.getDefault())
            return if (direction == View.LAYOUT_DIRECTION_RTL) Gravity.LEFT else Gravity.RIGHT
        }
    }

    interface Listener {
        /** Called when cancel is clicked. This view is unusable once this callback is invoked.  */
        fun onCancel()

        /**
         * Called when start is clicked and the view is animating itself out,
         * before [.onStart].
         */
        fun onPrepare()

        /**
         * Called when start is clicked and it is appropriate to start recording. This view will hide
         * itself completely before invoking this callback.
         */
        fun onStart()

        /** Called when stop is clicked. This view is unusable once this callback is invoked.  */
        fun onStop()

        /** Called when the size or layout params of this view have changed and require a relayout.  */
        fun onResize()
    }
}