package net.angrycode.capture.local.ui

import android.Manifest
import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_common_list.*
import net.angrycode.capture.base.BaseAdapter
import net.angrycode.capture.base.BaseViewHolder
import net.angrycode.capture.base.CommonListFragment
import net.angrycode.capture.ext.toast
import net.angrycode.capture.local.adapter.CaptureListAdapter
import net.angrycode.capture.local.entity.Video
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import android.support.v7.widget.helper.ItemTouchHelper
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import net.angrycode.capture.*
import timber.log.Timber


/**
 * Capture video file list in local storage.
 * Created by pc on 2018/1/3.
 */
class CaptureListFragment : CommonListFragment<Video, BaseViewHolder>() {

    override var adapter: BaseAdapter<Video, BaseViewHolder> = CaptureListAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        requestWithPermission()
    }

    private fun init() {
        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // open slide to delete
        adapter.enableSwipeItem()
        adapter.setOnItemSwipeListener(onItemSwipeListener)
        fab.setOnClickListener {
            when (activity) {
                is MainActivity -> {
                    (activity as MainActivity).captureWithPermission()
                }
                else -> {
                }
            }
        }
    }

    private var onItemSwipeListener: OnItemSwipeListener = object : OnItemSwipeListener {
        override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

        }

        override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
            val del = context?.deleteExternalFile(adapter.getItem(pos)?.path)
            Timber.d("file is deleted? $del")
        }

        override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
        }

        override fun onItemSwipeMoving(canvas: Canvas?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, isCurrentlyActive: Boolean) {
        }
    }

    @AfterPermissionGranted(REQUEST_CODE)
    private fun requestWithPermission() {
        val perms = Manifest.permission.READ_EXTERNAL_STORAGE
        if (context != null) {
            if (!EasyPermissions.hasPermissions(context!!, perms)) {
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_ask), REQUEST_CODE, perms)
            } else {
                doOnRefresh()
            }
        }
    }

    override fun doOnRefresh() {
        doAsync {
            val localList = context?.listLocalVideos()
            uiThread {
                adapter.setNewData(localList)
            }
        }
    }

    override fun doOnItemClick(view: View, position: Int) {
//        val video = adapter.getItem(position)
//        val intent = Intent(Intent.ACTION_VIEW)
//        val uri = Uri.parse("file://${video?.path}")
//        intent.setDataAndType(uri, "video/*")
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        if (context?.packageManager?.resolveActivity(intent, 0) != null) {
//            startActivity(intent)
//        } else {
//            toast("Cannot not found a video player.")
//        }
    }

    override fun getDividerDrawableRes(): Int {
        return 0
    }

    companion object {
        const val REQUEST_CODE = 200
    }

}