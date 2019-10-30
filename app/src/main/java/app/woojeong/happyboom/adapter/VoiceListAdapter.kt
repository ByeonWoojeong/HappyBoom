package com.dev.hongsw.happyBoom.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.dev.hongsw.happyBoom.data.Voice

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 1
 * @license Copyright 2019. H&S All rights reserved.
 **/

class VoiceListAdapter(context: Context, layout: Int, private val mVoiceList: ArrayList<Voice>) : ArrayAdapter<String>(context, layout) {
    private val mLayout = layout

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        var view = convertView
        if (view == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(context).inflate(mLayout, parent, false)
            viewHolder.textView = view as TextView
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.textView?.text = mVoiceList[position].name

        return view
    }

    override fun getItem(position: Int): String = mVoiceList[position].name

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = mVoiceList.size

    inner class ViewHolder {

        var textView: TextView? = null

    }
}