package net.angrycode.capture.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.annotation.DimenRes
import android.support.v4.app.Fragment
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import net.angrycode.capture.CaptureApplication

/**
 * Some utilities
 * Created by wecodexyz@gmail.com on 2017/12/29.
 */

fun View.dp2px(dp: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

fun View.dimen(dimenRes: Int): Float = resources.getDimension(dimenRes)

fun Context.getApp(): CaptureApplication = applicationContext as CaptureApplication

fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Fragment.toast(message: CharSequence) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

/**
 * screen width in pixels
 */
val Context.screenWidth
    get() = resources.displayMetrics.widthPixels

/**
 * screen height in pixels
 */
val Context.screenHeight
    get() = resources.displayMetrics.heightPixels

val Context.inputMethodManager: InputMethodManager?
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

/**
 * hide soft input
 */
fun Context.hideSoftInput(view: View) {
    inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.start(clazz: Class<*>) {
    val intent = Intent(this, clazz)
    if (this !is Activity) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}
