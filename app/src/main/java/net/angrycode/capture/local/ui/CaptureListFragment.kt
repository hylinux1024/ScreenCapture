package net.angrycode.capture.local.ui

import android.os.Bundle
import net.angrycode.capture.base.BaseAdapter
import net.angrycode.capture.base.BaseViewHolder
import net.angrycode.capture.base.CommonListFragment
import net.angrycode.capture.local.adapter.CaptureListAdapter
import net.angrycode.capture.local.entity.Video

/**
 * Capture video file list in local storage.
 * Created by pc on 2018/1/3.
 */
class CaptureListFragment : CommonListFragment<Video, BaseViewHolder>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override var adapter: BaseAdapter<Video, BaseViewHolder> = CaptureListAdapter()

}