package net.angrycode.capture.local.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
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
        Glide.with(mContext).load(item?.thumb).into(imageView)
        helper?.setText(R.id.tv_title, item?.path)
    }
}