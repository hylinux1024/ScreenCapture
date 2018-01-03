package net.angrycode.capture.base

import android.os.Bundle
import android.support.v4.app.Fragment
import net.angrycode.capture.R

/**
 * Activity with toolbar.
 * Created by wecodexyz@gmail.com on 2018/1/3.
 */
abstract class ContainerActivity : BaseActivity() {
    protected abstract var contentFragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        supportFragmentManager.beginTransaction().replace(R.id.container, contentFragment, TAG_CONTENT).commitAllowingStateLoss()
    }

    companion object {
        val TAG_CONTENT = "content-fragment"
    }
}