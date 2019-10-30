package com.dev.hongsw.happyBoom.fragment

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.arthenica.mobileffmpeg.FFmpeg
import com.bumptech.glide.Glide
import com.dev.hongsw.happyBoom.util.scaleDown
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.text.CaptionStyleCompat
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.video.VideoListener
import java.io.File
import android.util.TypedValue
import android.graphics.Color
import app.woojeong.happyboom.EditVideoActivity
import app.woojeong.happyboom.R
import app.woojeong.happyboom.databinding.FragmentVideo2Binding


/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 2
 * @license Copyright 2019. H&S All rights reserved.
 **/

class SecondFragment: Fragment() {

    private lateinit var mPlayer: Player
    private lateinit var mBinding: FragmentVideo2Binding
    private lateinit var rootActivity: EditVideoActivity
    private var mWaterMark: File? = null
    private lateinit var mVolumeChangedEvent: () -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootActivity = activity as EditVideoActivity

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_video2, container, false)

        mPlayer = ExoPlayerFactory.newSimpleInstance(this.context) as SimpleExoPlayer

        mBinding.playerView.player = mPlayer

        mBinding.volumeSlider.progress = 50

        mBinding.playerView.subtitleView.setApplyEmbeddedStyles(false)
        mBinding.playerView.subtitleView.setApplyEmbeddedFontSizes(false)
        mBinding.playerView.subtitleView.setStyle(
            CaptionStyleCompat(
                Color.WHITE,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                CaptionStyleCompat.EDGE_TYPE_OUTLINE,
                Color.BLACK,
                null
            )
        )
        mBinding.playerView.subtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_PX, 45f)

        mBinding.volumeSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (::mVolumeChangedEvent.isInitialized) mVolumeChangedEvent()
            }
        })

        (mPlayer as SimpleExoPlayer).addVideoListener(object : VideoListener {

            override fun onSurfaceSizeChanged(width: Int, height: Int) {
                mBinding.imageView.layoutParams.height = height
                mBinding.imageView.layoutParams.width = width
                mBinding.imageView.layoutParams
                mBinding.imageView.requestLayout()

                mWaterMark?.let {
                    Glide.with(this@SecondFragment).load(scaleDown(BitmapFactory.decodeFile(it.path), width)).into(mBinding.imageView)
                    mBinding.imageView.alpha = 0.6f
                }

                super.onSurfaceSizeChanged(width, height)
            }
        })

        return mBinding.root
    }


    fun getVideoVolume(): Int = mBinding.volumeSlider.progress

    fun setVideo(media: MediaSource, waterMark: File?, resetPosition: Boolean = false, resetState: Boolean = false) {
        if (rootActivity.writeAss()) {
            val subtitleSource = SingleSampleMediaSource(
                File(rootActivity.cacheDir, "H.ass").toUri(),
                rootActivity.mDataSourceFactory,
                Format.createTextSampleFormat(null, MimeTypes.TEXT_SSA, Format.NO_VALUE, "ko", null),
                C.TIME_UNSET)

            val mediaSource = MergingMediaSource(media, subtitleSource)
            (mPlayer as SimpleExoPlayer).prepare(mediaSource,resetPosition, resetState)
        } else {
            (mPlayer as SimpleExoPlayer).prepare(media,resetPosition, resetState)
        }
        mWaterMark = waterMark

    }

    fun setVolumeChange(function : () -> Unit) {
        mVolumeChangedEvent = function
    }

    fun setSeek(time: Long) {
        mPlayer.seekTo(time)
        mPlayer.playWhenReady = true
    }

    fun setVolume(volume: Float) {
        mPlayer.audioComponent?.volume = volume
    }

    fun playVideo() {
        mPlayer.playWhenReady = true
        mBinding.playerView.hideController()
    }

    fun stopVideo() {
        (mPlayer as SimpleExoPlayer).playWhenReady = false
        mPlayer.stop(false)
    }

    fun destroyThis() {
        mPlayer.release()
        FFmpeg.cancel()
    }
}