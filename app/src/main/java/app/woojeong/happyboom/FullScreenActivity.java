package app.woojeong.happyboom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.androidquery.AQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class FullScreenActivity extends AppCompatActivity {

    String url, video_name;
    int video_idx, pause_time, study_time;
    CustomVideoView videoView;
    AQuery aQuery;
    Map<String, Object> params = new HashMap<>();
    boolean timer_resume;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<Integer> video_idx_arr = new ArrayList<>();
    ArrayList<Integer> video_time_arr = new ArrayList<>();
    float touch_x, touch_y;
    // 버튼 딜레이(3분)
    private static final long MIN_CLICK_INTERVAL = 1000 * 60 * 3;
    private long mLastClickTime;
    // 터치 방향 구분자 (초기화 : 9, 좌/우 : 1, 상/하 : 2)
    int touch_idx;
    // 볼륨 조절
    private AudioManager audio;
    LinearLayout controller;
    ImageView bt_backward, bt_control, bt_forward;
    SeekBar seekBar;
    Handler handler = new Handler();
    int controller_time = 0;
    boolean threadBn = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        video_idx_arr = getIdxArrayPref(FullScreenActivity.this, "video_idx");
        video_time_arr = getTimeArrayPref(FullScreenActivity.this, "video_time");

        aQuery = new AQuery(FullScreenActivity.this);
        Intent getIntent = getIntent();
        url = getIntent.getStringExtra("url");
        video_idx = getIntent.getIntExtra("idx", -9999);
        if (video_idx != -9999) {
            video_name = getIntent.getStringExtra("name");
        }
        Log.i("ffff", url);
        Log.i("ffff", video_idx + "");
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        controller =  findViewById(R.id.controller);
        bt_backward =  findViewById(R.id.bt_backward);
        bt_control =  findViewById(R.id.bt_control);
        bt_forward =  findViewById(R.id.bt_forward);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    // this is when actually seekbar has been seeked to a new position
                    videoView.seekTo(progress);
                }
            }
        });

        bt_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.seekTo(videoView.getCurrentPosition() - 5000);
//                seekBar.post(onEverySecond);
            }
        });
        bt_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.seekTo(videoView.getCurrentPosition() + 5000);
//                seekBar.post(onEverySecond);
            }
        });

        bt_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    bt_control.setImageResource(R.drawable.play_icon);
                    videoView.pause();
                } else {
                    bt_control.setImageResource(R.drawable.pause_icon);
                    videoView.start();
                }
            }
        });

//        controllerThread.start();
        videoView = (CustomVideoView) findViewById(R.id.videoView);
        videoView.setVideoPath(url);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                seekBar.setMax(videoView.getDuration());
                Log.e("aaaa", "setOnPreparedListener");
                study_time = 0;
                try {
                    timerThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        videoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
            @Override
            public void onPlay() {
                Log.e("control", "play: " + study_time);
                try {
                    timer_resume = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPause() {
                Log.e("control", "pause: " + study_time);
                timer_resume = false;
            }
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("eee", motionEvent.getAction() + "");
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (controller.getVisibility() == View.GONE) {
                        controller.setVisibility(View.VISIBLE);
                    } else {
                        controller.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // 비디오 재생 끝났을 때
                int secs = (int) (study_time / 1000);
                Log.e("time", secs + "");
                if (video_idx != -9999) {
                    for (int i = 0; i < video_idx_arr.size(); i++) {
                        if (video_idx_arr.get(i) == video_idx) {
                            study_time = study_time + video_time_arr.get(i);
                        }
                    }
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;

                    if (elapsedTime <= MIN_CLICK_INTERVAL) {
                        //중복클릭일 때
                        Log.e("params_video", "fail");
                        return;
                    } else {
                        //중복클릭이 아닐때
                        Log.e("params_video", "send");
                        sendTime();
                    }
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        pause_time = videoView.getCurrentPosition();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            videoView.seekTo(pause_time);
            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Thread timerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (threadBn) {
                SystemClock.sleep(100);
                controller_time = controller_time + 100;
                if (timer_resume) {
                    study_time += 100;
                }
                if (seekBar != null) {
                    seekBar.setProgress(videoView.getCurrentPosition());
                }
                Log.e("aaa", controller_time + "");
                if (controller.getVisibility() == View.VISIBLE) {
                    if (controller_time >= 2000) {
                        try {
                            controller.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            controller_time = 0;
                        }
                    }
                } else {
                    controller_time = 0;
                }
            }
        }
    });

    Thread controllerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (controller.getVisibility() == View.VISIBLE) {
                    SystemClock.sleep(5000);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            controller.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    });

    private void sendTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 재생시간 전송
                String timeStr;
                int secs = (int) (study_time / 1000);
                int mins = secs / 60;
                int hour = mins / 60;
                secs = secs % 60;
                mins = mins % 60;
                String minStr, secStr;
                if (mins < 10) {
                    minStr = "0" + mins;
                } else {
                    minStr = mins + "";
                }
                if (secs < 10) {
                    secStr = "0" + secs;
                } else {
                    secStr = secs + "";
                }
                if (hour == 0) {
                    timeStr = minStr + ":" + secStr;
                } else {
                    timeStr = hour + ":" + minStr + ":" + secStr;
                }
                Log.e("fff", timeStr);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        Log.e("VideoView", "finish");
//        sendTime();
        if (controller.getVisibility() == View.VISIBLE) {
            controller.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < video_idx_arr.size(); i++) {
                if (video_idx_arr.get(i) == video_idx) {
                    study_time = study_time + video_time_arr.get(i);
                    video_idx_arr.remove(i);
                    video_time_arr.remove(i);
                }
            }
            video_idx_arr.add(video_idx);
            video_time_arr.add(study_time);
            setStudyArrayPref(FullScreenActivity.this, "video_idx", video_idx_arr, "video_time", video_time_arr);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            threadBn = false;
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_x = event.getX();
                touch_y = event.getY();
                touch_idx = 9;
                break;
            case MotionEvent.ACTION_MOVE:
                if (touch_idx == 9) {
                    if (touch_x + 50 < x) {
                        Log.e("gesture", "right");
//                    Toast.makeText(VideoActivity.this, "UP!", Toast.LENGTH_SHORT).show();
                        videoView.seekTo(videoView.getCurrentPosition() + 1000);
                        controller.setVisibility(View.VISIBLE);
                        touch_x = x;
                        touch_y = y;
                        touch_idx = 1;
                    } else if (touch_x - 50 > x) {
                        Log.e("gesture", "left");
//                    Toast.makeText(VideoActivity.this, "DOWN!", Toast.LENGTH_SHORT).show();
                        videoView.seekTo(videoView.getCurrentPosition() - 1000);
                        controller.setVisibility(View.VISIBLE);
                        touch_x = x;
                        touch_y = y;
                        touch_idx = 1;
                    } else if (touch_y + 50 < y) {
                        Log.e("gesture", "down");
//                    Toast.makeText(VideoActivity.this, "RIGHT!", Toast.LENGTH_SHORT).show();
                        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                        touch_x = x;
                        touch_y = y;
                        touch_idx = 2;
                    } else if (touch_y - 50 > y) {
                        Log.e("gesture", "up");
//                    Toast.makeText(VideoActivity.this, "LEFT!", Toast.LENGTH_SHORT).show();
                        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                        touch_x = x;
                        touch_y = y;
                        touch_idx = 2;
                    }
                } else if (touch_idx == 1) {
                    if (touch_x + 50 < x) {
                        Log.e("gesture", "right");
//                    Toast.makeText(VideoActivity.this, "UP!", Toast.LENGTH_SHORT).show();
                        videoView.seekTo(videoView.getCurrentPosition() + 1000);
                        controller.setVisibility(View.VISIBLE);
                        touch_x = x;
                        touch_y = y;
                        touch_idx = 1;
                    } else if (touch_x - 50 > x) {
                        Log.e("gesture", "left");
//                    Toast.makeText(VideoActivity.this, "DOWN!", Toast.LENGTH_SHORT).show();
                        videoView.seekTo(videoView.getCurrentPosition() - 1000);
                        controller.setVisibility(View.VISIBLE);
                        touch_x = x;
                        touch_y = y;
                        touch_idx = 1;
                    }
                } else if (touch_idx == 2) {
                    if (touch_y + 50 < y) {
                        Log.e("gesture", "down");
//                    Toast.makeText(VideoActivity.this, "RIGHT!", Toast.LENGTH_SHORT).show();
                        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                        touch_x = x;
                        touch_y = y;
                        touch_idx = 2;
                    } else if (touch_y - 50 > y) {
                        Log.e("gesture", "up");
//                    Toast.makeText(VideoActivity.this, "LEFT!", Toast.LENGTH_SHORT).show();
                        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                        touch_x = x;
                        touch_y = y;
                        touch_idx = 2;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                touch_idx = 9;
                break;
        }
        return false;
    }

    private void setStudyArrayPref(Context context, String key1, ArrayList<Integer> video_idx_arr
            , String key2, ArrayList<Integer> video_time_arr) {
        // 영상 idx, time 각각 배열로 저장
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray video_idx = new JSONArray();
        for (int i = 0; i < video_idx_arr.size(); i++) {
            video_idx.put(video_idx_arr.get(i));
        }
        if (!video_idx_arr.isEmpty()) {
            editor.putString(key1, video_idx.toString());
        } else {
            editor.putString(key1, null);
        }
        JSONArray video_time = new JSONArray();
        for (int i = 0; i < video_time_arr.size(); i++) {
            video_time.put(video_time_arr.get(i));
        }
        if (!video_time_arr.isEmpty()) {
            editor.putString(key2, video_time.toString());
        } else {
            editor.putString(key2, null);
        }
        editor.commit();
        editor.apply();
    }

    private ArrayList<Integer> getIdxArrayPref(Context context, String key) {
        // 영상 idx 가져오기
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<Integer> idx_arr = new ArrayList<Integer>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    int url = a.optInt(i);
                    idx_arr.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return idx_arr;
    }

    private ArrayList<Integer> getTimeArrayPref(Context context, String key) {
        // 영상 time 가져오기
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<Integer> time_arr = new ArrayList<Integer>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    int url = a.optInt(i);
                    time_arr.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return time_arr;
    }

}
