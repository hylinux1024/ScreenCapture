package net.angrycode.capture.local.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import net.angrycode.capture.base.ContainerActivity

/**
 * Created by pc on 2018/1/3.
 */
class CaptureListActivity : ContainerActivity() {

    override var contentFragment: Fragment = CaptureListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun isShowHomeAsUpIndicator(): Boolean {
        return true
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context,CaptureListActivity::class.java)
            context.startActivity(intent)
        }
    }
}