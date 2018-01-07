package net.angrycode.capture.base

import com.chad.library.adapter.base.BaseItemDraggableAdapter

/**
 * Created by pc on 2018/1/3.
 */
abstract class BaseAdapter<T, K : BaseViewHolder>(layoutId: Int, data: List<T>) : BaseItemDraggableAdapter<T, K>(layoutId, data)