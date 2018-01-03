package net.angrycode.capture.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Base Fragment.
 * Created by lancelot on 2018/1/3.
 */
abstract class BaseFragment : Fragment() {

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val resLayoutId = getLayoutResource()
        if (resLayoutId != 0) {
            val view = inflater.inflate(resLayoutId, container, false)
            return view
        }
        return null
    }

    abstract fun getLayoutResource(): Int
}