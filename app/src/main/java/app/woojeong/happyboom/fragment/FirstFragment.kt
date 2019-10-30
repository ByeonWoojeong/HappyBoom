package com.dev.hongsw.happyBoom.fragment

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.woojeong.happyboom.EditVideoActivity
import app.woojeong.happyboom.R
import app.woojeong.happyboom.databinding.FragmentVideo1Binding
import kotlinx.android.synthetic.main.fragment_video1.view.*

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 2
 * @license Copyright 2019. H&S All rights reserved.
 **/

class FirstFragment : Fragment() {

    private lateinit var mBinding: FragmentVideo1Binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_video1, container, false)

        mBinding.root.listView.adapter = (activity as EditVideoActivity).mSubtitleListAdapter

        return mBinding.root
    }

}