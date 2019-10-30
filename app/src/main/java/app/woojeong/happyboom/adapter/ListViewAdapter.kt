package com.dev.hongsw.happyBoom.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import app.woojeong.happyboom.databinding.ListVideoCellBinding
import app.woojeong.happyboom.model.ListViewModel

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 1
 * @license Copyright 2019. H&S All rights reserved.
 **/

class ListViewAdapter(private val context: Context,  private val mEncodingMetaData: ArrayList<ListViewModel>) : BaseAdapter() {

    private var mModifyListener: (ListViewModel, Int) -> Unit = { _: ListViewModel, _: Int -> }
    private var mCheckItemChangeListener: () -> Unit = {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val binding: ListVideoCellBinding
        if (convertView == null) {
            binding = ListVideoCellBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ListVideoCellBinding
        }
        binding.listViewModel = getItem(position) as ListViewModel
        binding.materialCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (binding.listViewModel?.subtitle?.available!! == isChecked) {
                binding.listViewModel?.subtitle?.available = !isChecked
                notifyDataSetChanged()
                mCheckItemChangeListener()
            } else {
                binding.listViewModel?.subtitle?.available = !isChecked
                notifyDataSetChanged()
            }
        }
        binding.modifyBtn.setOnClickListener {
            mModifyListener(binding.listViewModel!!, position)
        }

        return binding.root
    }

    override fun getItem(position: Int): Any = mEncodingMetaData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = mEncodingMetaData.size

    fun addItem(item: ListViewModel) {
        mEncodingMetaData.add(item)
        mEncodingMetaData.sortBy { it.startTime.toInt() }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        mEncodingMetaData.removeAt(position)
        mEncodingMetaData.sortBy { it.startTime.toInt() }
        notifyDataSetChanged()
        mCheckItemChangeListener()
    }
    fun setModifyClickListener(function: (ListViewModel, Int) -> Unit) {
        mModifyListener = function
    }

    fun setCheckItemChangeListener(function: () -> Unit) {
        mCheckItemChangeListener = function
    }
}