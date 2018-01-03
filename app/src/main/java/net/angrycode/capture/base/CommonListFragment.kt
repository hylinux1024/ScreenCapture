package net.angrycode.capture.base

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_common_list.*
import net.angrycode.capture.R

/**
 * common list
 * Created by pc on 2018/1/3.
 */
abstract class CommonListFragment<T, V : BaseViewHolder> : BaseFragment() {

    protected abstract var adapter: BaseAdapter<T, V>

    override fun getLayoutResource(): Int {
        return R.layout.fragment_common_list
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        val layout = LinearLayoutManager(context)
        layout.orientation = LinearLayoutManager.VERTICAL
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        if (context != null) {
            val drawable = ContextCompat.getDrawable(context!!, getDividerDrawableRes())
            if (drawable != null) {
                itemDecoration.setDrawable(drawable)
            }
            recyclerView.addItemDecoration(itemDecoration)
            recyclerView.layoutManager = layout
            recyclerView.adapter = adapter
            if (isSupportRefresh()) {
                refreshLayout.isEnabled = true
                refreshLayout.setOnRefreshListener({ doOnRefresh() })
            } else {
                refreshLayout.isEnabled = false
            }

            if (isSupportLoadMore()) {
                adapter.setOnLoadMoreListener({ doOnLoadMore() }, recyclerView)
            }

            adapter.setOnItemClickListener({ _, view, position -> doOnItemClick(view, position) })

            val emptyView = layoutInflater.inflate(R.layout.simple_empty_view, null) as TextView
            emptyView.text = getEmptyText()
            adapter.emptyView = emptyView
        }
    }

    /**
     * list item divider color.
     */
    open protected fun getDividerDrawableRes(): Int {
        return R.drawable.common_divider
    }

    open protected fun isSupportRefresh(): Boolean {
        return false
    }

    open protected fun isSupportLoadMore(): Boolean {
        return false
    }

    open protected fun doOnRefresh() {

    }

    open protected fun doOnLoadMore() {

    }

    open protected fun doOnItemClick(view: View, position: Int) {

    }

    open protected fun getEmptyText(): String {
        return resources.getString(R.string.empty_text)
    }

}