package net.angrycode.capture.base

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import net.angrycode.capture.R

/**
 * Base Activity.
 * Created by wecodexyz@gmail.com on 2018/1/3.
 */
abstract class BaseActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            if (isShowHomeAsUpIndicator()) {
                supportActionBar?.setHomeButtonEnabled(true)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            toolbar?.setOnClickListener { onBackPressed() }
        }
    }

    /**
     * return true to show back btn,else hide it.
     */
    protected fun isShowHomeAsUpIndicator(): Boolean {
        return false
    }
}