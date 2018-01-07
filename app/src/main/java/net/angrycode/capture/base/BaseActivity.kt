package net.angrycode.capture.base

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import net.angrycode.capture.R
import pub.devrel.easypermissions.EasyPermissions

/**
 * Base Activity.
 * Created by wecodexyz@gmail.com on 2018/1/3.
 */
abstract class BaseActivity : AppCompatActivity() {

    protected var toolbar: Toolbar? = null

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            if (isShowHomeAsUpIndicator()) {
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            toolbar?.setNavigationOnClickListener { onBackPressed() }
        }
    }

    /**
     * return true to show back btn,else hide it.
     */
    open protected fun isShowHomeAsUpIndicator(): Boolean {
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}