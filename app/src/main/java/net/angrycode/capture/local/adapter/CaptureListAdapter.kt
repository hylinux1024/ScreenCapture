package net.angrycode.capture.local.adapter

import android.widget.ImageView
import net.angrycode.capture.R
import net.angrycode.capture.base.BaseAdapter
import net.angrycode.capture.base.BaseViewHolder
import net.angrycode.capture.local.entity.Video

/**
 * Created by pc on 2018/1/3.
 */
class CaptureListAdapter : BaseAdapter<Video, BaseViewHolder>(R.layout.item_capture_list, listOf()) {

    override fun convert(helper: BaseViewHolder?, item: Video?) {
        val imageView = helper?.getView<ImageView>(R.id.iv_video_thumbnail)
    }
}