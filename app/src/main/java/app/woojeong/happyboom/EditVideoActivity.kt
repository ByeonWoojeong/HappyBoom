package app.woojeong.happyboom

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.databinding.DataBindingUtil
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.dev.hongsw.happyBoom.fragment.FirstFragment
import kotlinx.android.synthetic.main.activity_edit_video.*
import kotlinx.android.synthetic.main.dialog_add.view.*
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.forEach
import app.woojeong.happyboom.databinding.ActivityEditVideoBinding
import app.woojeong.happyboom.databinding.DialogAddBinding
import app.woojeong.happyboom.model.ListViewModel
import app.woojeong.happyboom.model.SettingUiModel
import app.woojeong.happyboom.subtitle.DefaultStyle
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.util.RunCallback
import com.bumptech.glide.Glide
import com.codekidlabs.storagechooser.StorageChooser
import com.dev.hongsw.happyBoom.adapter.ListViewAdapter
import com.dev.hongsw.happyBoom.adapter.VoiceListAdapter
import com.dev.hongsw.happyBoom.data.Config.Companion.TTS_DONE
import com.dev.hongsw.happyBoom.data.Config.Companion.TTS_ERROR
import com.dev.hongsw.happyBoom.data.Config.Companion.TTS_START
import com.dev.hongsw.happyBoom.data.Voice
import com.dev.hongsw.happyBoom.extensions.onClick
import com.dev.hongsw.happyBoom.fragment.SecondFragment
import com.dev.hongsw.happyBoom.subtitle.AssWriter
import com.dev.hongsw.happyBoom.subtitle.Subtitle
import com.dev.hongsw.happyBoom.util.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.google.android.material.snackbar.Snackbar
import gun0912.tedbottompicker.TedRxBottomPicker
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_edit_video.view.*
import kotlinx.android.synthetic.main.dialog_encoding.*
import java.util.*
import kotlin.collections.ArrayList
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 5. 29
 * @license Copyright 2019. H&S All rights reserved.
 **/

@Suppress("DEPRECATION")
class EditVideoActivity : AppCompatActivity() {

    companion object {
        const val TAG = "EditVideoActivity"
        val encodingDataList = ArrayList<ListViewModel>()
        val ttsVoiceList: ArrayList<Voice> = ArrayList()
        var mAsyncTask: AsyncCommandTask? = null
        fun executeAsync(runCallback: RunCallback, arguments: String) {
            mAsyncTask = AsyncCommandTask(runCallback)
            mAsyncTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arguments)
        }
    }

    lateinit var mDataSourceFactory: DefaultDataSourceFactory
    private lateinit var mBinding: ActivityEditVideoBinding
    private lateinit var mTts: TextToSpeech
    private lateinit var mOriginVideo: MediaSource
    private lateinit var mPreviewVideo: MediaSource
    private lateinit var mVideoFragment: SecondFragment
    private lateinit var mSettingFragment: FirstFragment
    private lateinit var mHandler: Handler
    private lateinit var mFirstImagePicker: Disposable
    private lateinit var mSecondImagePicker: Disposable
    private var mMusicFilePath: String? = null
    private var mRecFilePath: String? = null
    private val mUserImageFileList = arrayListOf<File?>(null, null)
    private var mTTSStatus = 1
    private var mPlayTime: Long = 0L
    private var mVideoWidth = 0
    private var mVideoHeight = 0

    private var mAgencyImage: File? = null
    private var mOutputPath: File? = null
    private var mInputPath: File? = null
    private var mEncodingVideo: Uri? = null
    private lateinit var mDefaultStyle: DefaultStyle
    lateinit var mSubtitleListAdapter: ListViewAdapter
    private var skip: Boolean = false
    private var community: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE)
        skip = preferences.getBoolean("skip", false)
        community = preferences.getBoolean("community", false)

        Log.i(TAG, " skip " + preferences.getBoolean("skip", false).toString())
        Log.i(TAG, " community " + preferences.getBoolean("community", false).toString())

        //트루면 skip


        // bind
        mBinding = DataBindingUtil.setContentView(this@EditVideoActivity, R.layout.activity_edit_video)

        mBinding.settingUiModel = SettingUiModel()

        // toolbar include
        setSupportActionBar(toolbar)

        // toolbar title disabled
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        bottomNavigationView.selectedItemId = R.id.tts

        mHandler = Handler()

        // 영상 경로, 아웃풋 경로, 첨부 이미지 경로, 받아서 에러처리
        intent.getBundleExtra("ffmpegData")?.let {
            mInputPath = it.getSerializable("input") as File?
            mOutputPath = it.getSerializable("output") as File?
            mAgencyImage = it.getSerializable("image") as File?
            mEncodingVideo = it.getParcelable("encodingVideo") as Uri
        }

        run {

            if ((mInputPath == null) or (mOutputPath == null)) {
                showErrorDialog("영상이 선택되지 않았습니다.") { it.dismiss(); this@EditVideoActivity.finish() }
                return@run
            }

            mInputPath?.let {
                if (it.exists()) {
                    val retriever = MediaMetadataRetriever()
                    try {
                        retriever.setDataSource(it.absolutePath)
                        mPlayTime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
                        mVideoWidth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()
                        mVideoHeight = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()
                    } catch (e: Exception) {
                        Log.d(TAG, "영상 메타데이터 추출 실패", e)
                        showErrorDialog("잘못된 영상 정보입니다.") { dialog -> dialog.dismiss(); this@EditVideoActivity.finish() }
                        retriever.release()
                        return@run
                    }
                    retriever.release()
                }
            }

            if ((mPlayTime == 0L) or (mVideoHeight == 0) or (mVideoWidth == 0)) {
                showErrorDialog("잘못된 영상 정보입니다.") { it.dismiss(); this@EditVideoActivity.finish() }
                return@run
            }

            mDefaultStyle = DefaultStyle(ViewWidth = mVideoWidth, ViewHeight = mVideoHeight)

            // mTts init
            mTts = TextToSpeech(this, TextToSpeech.OnInitListener { it ->
                if (it == TextToSpeech.ERROR) {
                    showErrorDialog("기기가 구글 음성 변환 서비스를 지원하지 않습니다.") { it.dismiss(); this@EditVideoActivity.finish() }
                    return@OnInitListener
                }
                if (mTts.isLanguageAvailable(Locale.KOREAN) != TextToSpeech.LANG_AVAILABLE) {
                    showErrorDialog("구글 음성 번역 서비스에서 한글을 설치해주세요.") { it.dismiss(); this@EditVideoActivity.finish() }
                    return@OnInitListener
                }

                mTts.setLanguage(Locale.KOREAN).run {
                    if (this == TextToSpeech.LANG_MISSING_DATA || this == TextToSpeech.LANG_NOT_SUPPORTED) {
                        mTts.stop()
                        mTts.shutdown()
                    }
                }

                mTts.setSpeechRate(0.8f)

                ttsVoiceList.clear()
                mTts.voices.filter { it.locale == Locale.KOREA }.forEachIndexed { index, voice ->
                    if (voice.locale == Locale.KOREA) {
                        ttsVoiceList.add(
                                Voice(
                                        String.format(
                                                "%d번 목소리 (%s)", index,
                                                if (voice.name.contains("#male")) "남성"
                                                else "여성"
                                        ), voice
                                )
                        )
                    }
                }
                mTts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onDone(utteranceId: String?) {
                        if (utteranceId == "test") return
                        Log.d(TAG, "tts 녹음 완료 : $utteranceId")
                        mTTSStatus = TTS_DONE
                        changePreviewAudioWarp(bottomNavigationView.selectedItemId)
                    }

                    override fun onError(utteranceId: String?) {
                        Log.d(TAG, "tts 녹음 에러 : $utteranceId")
                        mTTSStatus = TTS_ERROR
                    }

                    override fun onStart(utteranceId: String?) {
                        Log.d(TAG, "tts 녹음 시작 : $utteranceId")
                        mTTSStatus = TTS_START
                    }

                })
                Log.d(TAG, "TTS 초기화 성공")
            }, "com.google.android.tts")

            mSubtitleListAdapter = ListViewAdapter(this, encodingDataList)

            mSubtitleListAdapter.setModifyClickListener { listViewModel: ListViewModel, i: Int -> showDataAddSheet(this, listViewModel, i) }

            Log.d(TAG, "자막 리스트 뷰 초기화 및 아이템 리스너 등록 완료")

            mVideoFragment = SecondFragment()
            mSettingFragment = FirstFragment()

            supportFragmentManager.beginTransaction().add(R.id.videoContainer, mVideoFragment).add(R.id.settingContainer, mSettingFragment).commit()

            Log.d(TAG, "프래그먼트 컨테이너 뷰페이저 어뎁터 등록 및 변경 리스너 등록 완료")

            mDataSourceFactory = DefaultDataSourceFactory(
                    this,
                    getUserAgent(this, "HappyBom")
            )

            mOriginVideo = ProgressiveMediaSource.Factory(mDataSourceFactory).createMediaSource(mInputPath?.toUri())

            Log.d(TAG, "비디오 리소스 로딩")

            bottomNavigationView onClick bottomNavClicked()

            Log.d(TAG, "바텀 네비게이션 리스너 등록")

            // 아이템 변경에 따른 이벤트 리스너
            mVideoFragment.setVolumeChange {
                changePreviewAudioWarp(bottomNavigationView.selectedItemId)
            }

            mSubtitleListAdapter.setCheckItemChangeListener { changePreviewAudioWarp(bottomNavigationView.selectedItemId) }

            mBinding.startImageView onClick firstImagePcik()
            mBinding.endImageView onClick secondImagePick()
        }


    }

    override fun onStart() {
        super.onStart()

        if (::mVideoFragment.isInitialized) {
            if (::mPreviewVideo.isInitialized) {
                mVideoFragment.setVideo(mPreviewVideo, mAgencyImage)
            } else {
                mVideoFragment.setVideo(mOriginVideo, mAgencyImage)
            }

            mVideoFragment.playVideo()
        }

        if (skip) {
            encodingVideo(tts = encodingDataList)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.video_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.done -> {
                if (mTTSStatus == TTS_DONE) {
                    when (bottomNavigationView.selectedItemId) {
                        R.id.tts -> {
                            encodingVideo(tts = encodingDataList)
                        }
                        R.id.music -> {
                            encodingVideo(music = mMusicFilePath)
                        }
                        R.id.record -> {
                            encodingVideo(record = mRecFilePath)
                        }
                    }
                } else Toast.makeText(this, "Wait please", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // list item add dialog
    private fun showDataAddSheet(context: Context, item: ListViewModel?, position: Int) {
        val dialog = MaterialDialog(context, BottomSheet())
        // binding view
        val binding = DataBindingUtil.inflate<DialogAddBinding>(LayoutInflater.from(context), R.layout.dialog_add, null, false)

        // view inflate
        dialog.customView(view = binding.root, scrollable = false)
        dialog.setTitle("자막 설정")

        if (item != null) {
            binding.data = item
            dialog.positiveButton(text = "수정", click = addDialogPBClicked(binding, binding.data))
            dialog.negativeButton(text = "삭제") { mSubtitleListAdapter.removeItem(position); it.dismiss() }
        } else {
            binding.data = ListViewModel(subtitle = Subtitle(mDefaultStyle, mVideoHeight, mVideoWidth))
            binding.data?.setTimeHint(mPlayTime)

            try {
                binding.data?.tts = ttsVoiceList[0].name
            } catch (e: Exception) {
                Log.d(TAG, "tts 오류", e)
            }

            dialog.positiveButton(text = "Add", click = addDialogPBClicked(binding, null))
            dialog.negativeButton(android.R.string.cancel) { it.dismiss() }
        }

        binding.root.run {

            // tts volume Size
            this.ttsVolumeSlider.positionListener = { pos -> binding.data?.ttsVolume = pos }

            // dropdown set adapter
            this.dropdown.setAdapter(VoiceListAdapter(this@EditVideoActivity, R.layout.dropdown_tts, ttsVoiceList))

            // ttsVolumeSlider
            this.ttsVolumeSlider.beginTrackingListener = { this.ttsVolumeText.visibility = View.INVISIBLE }
            this.ttsVolumeSlider.endTrackingListener = { this.ttsVolumeText.visibility = VISIBLE }

            this.dropdown.setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(dialog.view.windowToken, 0)
            }

            this.testTTSBtn onClick {
                ttsVoiceList.find { it.name == binding.data?.tts }?.voice?.let { mTts.voice = it }
                val ttsBundle = Bundle()
                ttsBundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, binding.data!!.ttsVolume)
                mTts.speak("테스트 음성", TextToSpeech.QUEUE_FLUSH, ttsBundle, "test")
            }
        }

        dialog.show { this.noAutoDismiss() }
    }

    private fun addDialogPBClicked(binding: DialogAddBinding, item: ListViewModel?): (MaterialDialog) -> Unit = { it ->
        it.getActionButton(WhichButton.POSITIVE).isEnabled = false
        binding.data?.let { data ->
            data.isVirginComplete().let { i ->
                if (i == R.string.no_problem) {
                    ttsVoiceList.find { it.name == binding.data?.tts }?.voice?.let { mTts.voice = it }
                    val ttsBundle = Bundle()
                    val filename = binding.data?.subtitle?.Text.sha1()
                    ttsBundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, binding.data!!.ttsVolume)
                    val file = File(filesDir, "$filename.mp3")
                    if (file.exists()) file.delete()
                    if (TextToSpeech.SUCCESS != mTts.synthesizeToFile(
                                    binding.data?.subtitle?.Text,
                                    ttsBundle,
                                    file,
                                    binding.data?.subtitle?.Text
                            )
                    ) {
                        Toast.makeText(this@EditVideoActivity, "음성 변환 실패. 다시 시도해보세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        if (item == null) mSubtitleListAdapter.addItem(binding.data!!)
                    }
                    it.dismiss()
                } else {
                    it.getActionButton(WhichButton.POSITIVE).isEnabled = true
                    Toast.makeText(this@EditVideoActivity, getString(i), Toast.LENGTH_SHORT).show()
                    Log.i("Edit", " 123123" + i)
                }
            }
        }
    }

    // destroy lifecycle del tts
    override fun onDestroy() {
        if (::mTts.isInitialized) {
            mTts.stop()
            mTts.shutdown()
        }
        encodingDataList.clear()
//        ttsVoiceList.clear()

        if (::mVideoFragment.isInitialized) mVideoFragment.destroyThis()

        if (::mFirstImagePicker.isInitialized) {
            if (!mFirstImagePicker.isDisposed) {
                mFirstImagePicker.dispose()
            }
        }
        if (::mSecondImagePicker.isInitialized) {
            if (!mSecondImagePicker.isDisposed) {
                mSecondImagePicker.dispose()
            }
        }

        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        if (::mVideoFragment.isInitialized) mVideoFragment.stopVideo()
    }

    // error dialog
    private inline fun showErrorDialog(msg: String, crossinline func: (MaterialDialog) -> Unit) {
        val dialog = MaterialDialog(this)
        dialog.show {
            this.cancelable(false)
            this.title(text = "오류")
            this.message(text = msg)
            this.negativeButton { func(this) }
        }
    }


    fun writeAss(): Boolean {
        try {
            val assWriter = AssWriter(this@EditVideoActivity)
            assWriter.addPlayRec(mVideoWidth, mVideoHeight)
            assWriter.addStyle(mDefaultStyle)
            assWriter.addSubtitles(encodingDataList.map { it.subtitle })
            assWriter.endWrite()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun firstImagePcik(): (View: View) -> Unit = { view ->
        view as ImageView
        mFirstImagePicker = TedRxBottomPicker.with(this)
                .setPeekHeight(1600)
                .setSelectMaxCount(2)
                .showTitle(true)
                .setTitle("이미지 선택")
                .setCompleteButtonText("완료")
                .setSelectMaxCount(1)
                .setSelectMaxCountErrorText("하나만 선택해주세요.")
                .setDeSelectIcon(R.drawable.icon_pause)
                .showMultiImage()
                .subscribe { t1, t2 ->

                    if (t1.size != 0) {
                        try {
                            t1.forEachIndexed { _, it ->
                                contentResolver.openInputStream(it)?.let { input ->
                                    input.toWrite(this, it.toFile().name)?.let {
                                        mUserImageFileList[0] = it
                                        Glide.with(this).load(it).into(view)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "유저 이미지 픽 실패", e)
                        }
                    } else {
                        mUserImageFileList[0] = null
                        view.setImageResource(R.drawable.icon_plus)
                    }


                    t2?.printStackTrace()
                }
    }

    private fun secondImagePick(): (View: View) -> Unit = { view ->
        view as ImageView
        mSecondImagePicker = TedRxBottomPicker.with(this)
                .setPeekHeight(1600)
                .setSelectMaxCount(2)
                .showTitle(true)
                .setTitle("이미지 선택")
                .setCompleteButtonText("완료")
                .setSelectMaxCount(1)
                .setSelectMaxCountErrorText("하나만 선택해주세요.")
                .setDeSelectIcon(R.drawable.icon_pause)
                .showMultiImage()
                .subscribe { t1, t2 ->

                    if (t1.size != 0) {
                        try {
                            t1.forEachIndexed { _, it ->
                                contentResolver.openInputStream(it)?.let { input ->
                                    input.toWrite(this, it.toFile().name)?.let {
                                        mUserImageFileList[1] = it
                                        Glide.with(this).load(it).into(view)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "유저 이미지 픽 실패", e)
                        }
                    } else {
                        mUserImageFileList[1] = null
                        view.setImageResource(R.drawable.icon_plus)
                    }


                    t2?.printStackTrace()
                }
    }

    private fun changePreviewAudioWarp(id: Int) {
        when (id) {
            R.id.tts -> {
                chagnePreviewAudio(tts = encodingDataList)
            }
            R.id.music -> {
                chagnePreviewAudio(music = mMusicFilePath)
            }
            R.id.record -> {
                chagnePreviewAudio(record = mRecFilePath)
            }
        }
    }

    // bottom Nav controller
    private fun bottomNavClicked(): (item: MenuItem) -> Boolean = { it ->
        when (it.itemId) {
            R.id.add -> {
                showDataAddSheet(this, null, -1)
                false
            }
            R.id.tts -> {
                FFmpeg.cancel()
                changePreviewAudioWarp(R.id.tts)
                true
            }
            R.id.music -> {
                val lastId = bottomNavigationView.selectedItemId
                val builder = StorageChooser.Builder()
                builder.withActivity(this@EditVideoActivity).withFragmentManager(fragmentManager)
                builder.withMemoryBar(true)
                builder.setType(StorageChooser.FILE_PICKER)
                builder.allowCustomPath(true)
                val theme = StorageChooser.Theme(this@EditVideoActivity)
                theme.scheme = resources.getIntArray(R.array.paranoid_theme)
                builder.setTheme(theme)
                val picker = builder.build()

                picker.setOnCancelListener {
                    mMusicFilePath = null
                    bottomNavigationView.setOnNavigationItemSelectedListener { true }
                    bottomNavigationView.selectedItemId = lastId
                    bottomNavigationView onClick bottomNavClicked()
                }
                picker.setOnSelectListener {
                    if (isAudioFile(it)) {
                        mMusicFilePath = it
                        chagnePreviewAudio(music = mMusicFilePath)
                    } else {
                        Toast.makeText(this, "Invalid audio format", Toast.LENGTH_SHORT).show()
                        bottomNavigationView.setOnNavigationItemSelectedListener { true }
                        bottomNavigationView.selectedItemId = lastId
                        bottomNavigationView onClick bottomNavClicked()
                    }
                }
                picker.show()
                true
            }
            R.id.record -> {
                FFmpeg.cancel()

                bottomNavigationView.menu.forEach { it.isEnabled = false }
                startImageView.isEnabled = false
                endImageView.isEnabled = false

                val snackBar = Snackbar.make(mBinding.root.snackBarView, "음성 녹음 중입니다.", Snackbar.LENGTH_INDEFINITE)

                snackBar.setBackgroundTint(ContextCompat.getColor(this, android.R.color.black))
                snackBar.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                val recorder = MediaRecorder()
                val output = File(cacheDir, "rec.aac")
                if (output.exists()) output.delete()
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                recorder.setOutputFile(output.absolutePath)
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mVideoFragment.setSeek(0)
                mVideoFragment.setVolume(0f)
                try {
                    recorder.prepare()
                    recorder.start()
                } catch (e: Exception) {
                    Log.d(TAG, "녹음 오류", e)
                    snackBar.setText("녹음 시작 실패")
                    mRecFilePath = null
                    recorder.stop()
                    recorder.release()
                    snackBar.dismiss()
                    mVideoFragment.setVolume(1f)
                    bottomNavigationView.menu.forEach { it.isEnabled = true }
                    startImageView.isEnabled = true
                    endImageView.isEnabled = true
                }
                snackBar.setAction("완료") {
                    mRecFilePath = output.absolutePath
                    recorder.stop()
                    recorder.release()
                    snackBar.dismiss()
                    mVideoFragment.setVolume(1f)
                    bottomNavigationView.menu.forEach { it.isEnabled = true }
                    startImageView.isEnabled = true
                    endImageView.isEnabled = true
                    chagnePreviewAudio(record = mRecFilePath)
                }
                snackBar.show()
                true
            }
            else -> false
        }
    }

    private fun chagnePreviewAudio(tts: ArrayList<ListViewModel>? = null, music: String? = null, record: String? = null) {
        mAsyncTask?.cancel(true)
        FFmpeg.cancel()

        tts?.let { Log.d(TAG, "tts") }
        music?.let { Log.d(TAG, "music") }
        record?.let { Log.d(TAG, "record") }

        val snackBar = Snackbar.make(mBinding.root.snackBarView, "음성 합성 중입니다.", Snackbar.LENGTH_INDEFINITE)
        val animation = AnimationUtils.loadAnimation(this, R.anim.blink)

        snackBar.view.animation = animation
        snackBar.setBackgroundTint(ContextCompat.getColor(this, R.color.colorGray))
        snackBar.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        snackBar.show()

        val audioMixCommand = getSoundMixCommand(
                this,
                mInputPath!!,
                tts,
                music,
                record,
                File(filesDir, "preview.mp4"),
                1f - (mVideoFragment.getVideoVolume().toFloat() / 100),
                0f + (mVideoFragment.getVideoVolume().toFloat() / 40),
                mPlayTime.toSeconds().toInt()
        ).joinToString("<!@#>")

        executeAsync(RunCallback {
            if (it == 0) {
                mPreviewVideo =
                        ProgressiveMediaSource.Factory(mDataSourceFactory)
                                .createMediaSource(File(filesDir, "preview.mp4").toUri())
                mHandler.post {
                    mVideoFragment.setVideo(mPreviewVideo, mAgencyImage)
                }
            }
            snackBar.dismiss()
        }, audioMixCommand)

    }

    private fun encodingVideo(tts: ArrayList<ListViewModel>? = null, music: String? = null, record: String? = null) {
        visibleView.visibility = View.VISIBLE

        FFmpeg.cancel()
        Log.i(TAG, "111111111")
        mVideoFragment.stopVideo()
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_encoding)
        dialog.encodingAdView.run {
            this.setMediaController(MediaController(this@EditVideoActivity))
            this.requestFocus()
            this.setVideoURI(mEncodingVideo)
            this.setOnPreparedListener {
                it.seekTo(0)
                Log.i(TAG, "22222222222")
                it.start()
            }
            this.setOnCompletionListener { mp -> mp.start() }
            this.setOnErrorListener { mp, _, _ -> mp.release(); true }
        }
        dialog.setCancelable(false)
        dialog.show()

        writeAss()

        val encodingCommand = getEncodingCommand(
                mVideoWidth,
                mVideoHeight,
                File(filesDir, "preview.mp4"),
                mAgencyImage,
                File(cacheDir, "H.ass"),
                mOutputPath!!,
                mUserImageFileList,
                skip
        ).joinToString("<!@#>")

        val audioMixCommand = getSoundMixCommand(
                this,
                mInputPath!!,
                tts,
                music,
                record,
                File(filesDir, "preview.mp4"),
                2f - (mVideoFragment.getVideoVolume().toFloat() / 50), 0f + (mVideoFragment.getVideoVolume().toFloat() / 35) * (if (record != null) 2 else 1),
                mPlayTime.toSeconds().toInt()
        ).joinToString("<!@#>")


        executeAsync(RunCallback {
            if (it == 0) {
                // FFmpeg 진행률
                dialog.progressBar.max = mPlayTime.toSeconds().toInt()
                dialog.encodingText.text = "인코딩 중.."
                Config.enableStatisticsCallback { data ->
                    dialog.progressBar.progress = TimeUnit.MILLISECONDS.toSeconds(data.time.toLong()).toInt()
                }
                executeAsync(RunCallback { result ->
                    if (it == 0) {
                        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        intent.setData(Uri.parse("file:" + mOutputPath))
                        sendBroadcast(intent)
                        Toast.makeText(applicationContext, "영상을 최적화하였습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()

                        val preferences = getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE)
                        val skip = preferences.getBoolean("skip", false)
                        val community = preferences.getBoolean("community", false)
                        val editor = preferences.edit()
                        val name = preferences.getString("filename", "")
                        editor.putBoolean("skip", false)
                        editor.putBoolean("finish", true)
                        editor.putBoolean("community", false)
                        editor.commit()
                        //1. 커뮤니티 (finish) - 공유하기x
                        //2. 프리미엄 영상 다운 후 편집 > 공유하기
                        //3. 내영상 편집하기 (인코딩 하고 다시 돌아와서 편집뷰로)
                         //3-1. 최적화 후 3-2로 넘기기
                         //3-2. 편집 후 공유하기로 넘어가기
                        //4. (기업)프리미엄 업로드 - 공유하기x

                        //프리미엄에서 다운받은 영상을 편집하는 것
                        if (skip) {
                            //1, 3
                            // finish를 할지 bridge로 넘길지

                            if (!community) {
                                val intent = Intent(applicationContext, BridgeActivity::class.java)
                                intent.putExtra("title", name)
                                intent.putExtra("path", "file://" + Environment.getExternalStorageDirectory().toString() + File.separator + "HappyBoom/" + name + ".mp4")
//                                startActivityForResult(intent, REQUEST_CODE)
                                startActivity(intent)
                            } else {
                                //community
                            }

                        } else {
                            editor.putBoolean("encoding", false)
                            editor.putBoolean("finish", false)
                            editor.remove("filename")
                            val intent = Intent(applicationContext, Send1Activity::class.java)
                            intent.putExtra("path", "file://" + Environment.getExternalStorageDirectory().toString() + File.separator + "HappyBoom/" + name + ".mp4")
                            startActivity(intent)
                        }
                        finish()



//                        dialog.dismiss()
//
//                        MaterialDialog(this).show {
//                            this.cancelable(false)
//                            this.title(text = "인코딩 결과")
//                            this.message(text = if (result == 0) "인코딩 성공" else "인코딩 실패")
//                            this.positiveButton(text = "확인") {
//                                if (result == 0) {
//                                    setResult(Activity.RESULT_OK)
//
//                                } else setResult(Activity.RESULT_CANCELED)
//                                finish()
//                            }
//                        }
                    }
                }, encodingCommand)
            }
        }, audioMixCommand)
    }

}