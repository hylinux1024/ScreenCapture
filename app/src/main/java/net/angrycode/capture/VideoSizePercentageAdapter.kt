package net.angrycode.capture

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
 * Created by pc on 2017/12/28.
 */
class VideoSizePercentageAdapter(context: Context) : BaseAdapter() {

    val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var tv: TextView? = null
        if (convertView == null) {
            tv = layoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false) as TextView
        } else {
            tv = convertView as TextView
        }
        var text = "${getItem(position)}%"
        tv.text = text
        return tv
    }

    override fun getItem(position: Int): Any {
        return when (position) {
            0 -> 100
            1 -> 75
            2 -> 50
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int = 3

    companion object {

        fun getSelectedPosition(value: Int): Int {
            return when (value) {
                100 -> 0
                75 -> 1
                50 -> 2
                else -> 0
            }
        }
    }
}