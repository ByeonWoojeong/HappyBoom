package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.woojeong.happyboom.DTO.Comment;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class CommunityActivity extends AppCompatActivity {
    private static String TAG = "CommunityActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    ReportDialog reportDialog;
    MoreDialog moreDialog;
    DeleteDialog deleteDialog;

    SharedPreferences get_token;
    String getToken = "", idx = "", target = "", getKey = "", commentMode = "write_comment", videoPath = "", getContent = "", thumbnailFileUrl = "", getLike = "", getMember = "", getLevel = "";

    FrameLayout back_con, more_con, like_con, share_con, comment_con, report_con, ok_con;
    ImageView back, more, profile_img, share, comment, report;
    ScrollView scrollView;
    TextView nickname1, date1, content1, like_cnt, share_cnt, comment_cnt, to_re_nick, ok;
    EditText edit_comment;
    CheckBox like;

    WebView video_view;
    MediaController controller;
    Bitmap thumbnail;

    ListView comment_list;
    CommentListAdapter commentListAdapter;
    ArrayList<Comment> data;

    boolean isRecomment, isShare, isRoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(CommunityActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ipmm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        checkMode();
        Intent getIntent = getIntent();
        if (Intent.ACTION_VIEW.equals(getIntent.getAction())) {
            Uri uri = getIntent.getData();
            idx = uri.getQueryParameter("key");
        } else {
            idx = getIntent().getStringExtra("idx");
        }

        back_con = findViewById(R.id.back_con);
        back = findViewById(R.id.back);
        back_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.callOnClick();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scrollView = findViewById(R.id.scrollView);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        }, 100);

        more_con = findViewById(R.id.more_con);
        like_con = findViewById(R.id.like_con);
        share_con = findViewById(R.id.share_con);
        comment_con = findViewById(R.id.comment_con);
        report_con = findViewById(R.id.report_con);
        ok_con = findViewById(R.id.ok_con);

        more = findViewById(R.id.more);
        profile_img = findViewById(R.id.profile_img);
        like = findViewById(R.id.like);
        share = findViewById(R.id.share);
        comment = findViewById(R.id.comment);
        report = findViewById(R.id.report);

        nickname1 = findViewById(R.id.nickname1);
        date1 = findViewById(R.id.date1);
        content1 = findViewById(R.id.content1);
        like_cnt = findViewById(R.id.like_cnt);
        share_cnt = findViewById(R.id.share_cnt);
        comment_cnt = findViewById(R.id.comment_cnt);
        to_re_nick = findViewById(R.id.to_re_nick);
        ok = findViewById(R.id.ok);

        edit_comment = findViewById(R.id.edit_comment);

        comment_list = findViewById(R.id.comment_list);
        data = new ArrayList<Comment>();
        commentListAdapter = new CommentListAdapter(CommunityActivity.this, R.layout.list_comment_item, data, comment_list);

        video_view = findViewById(R.id.video_view);
        video_view.setWebChromeClient(new FullscreenableChromeClient(CommunityActivity.this));
//        controller = new MediaController(CommunityActivity.this);
//        video_view.setMediaController(controller);


        more_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more.callOnClick();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreDialog = new MoreDialog(context);
                moreDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                moreDialog.setCancelable(true);
                moreDialog.show();
            }
        });

        nickname1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if ("7".equals(getLevel)) {
                    intent = new Intent(CommunityActivity.this, CompanyActivity.class);
                    intent.putExtra("idx", getMember);
                } else {
                    intent = new Intent(CommunityActivity.this, ProfileActivity.class);
                    intent.putExtra("member", getMember);
                }
                startActivity(intent);
            }
        });

        like_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like.callOnClick();
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "like");
                final String url = ServerUrl.getBaseUrl() + "/community/like";
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("key", getKey);
                Log.i(TAG, " params " + params);
                aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                        Log.i(TAG, " jsonObject " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("return")) {    //return이 true 면?

                                getLike = jsonObject.getString("data");
                                Log.i(TAG, " getLike " + getLike);
                            } else if (!jsonObject.getBoolean("return")) {
                                Toast.makeText(CommunityActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                            showing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.header("epoch-agent", getToken).header("User-Agent", "android"));
            }
        });

        share_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share.callOnClick();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, ServerUrl.getBaseUrl() + "/share/community/" + idx);
                Intent shareIntent = Intent.createChooser(intent, "해피붐 게시글 공유하기");
                startActivity(shareIntent);

                shareCount();
            }
        });

        report_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report.callOnClick();
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CommunityActivity.this, "report", Toast.LENGTH_SHORT).show();
                reportDialog = new ReportDialog(context, "영상 신고 사유", idx);
                reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reportDialog.setCancelable(false);
                reportDialog.show();
            }
        });

        to_re_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_re_nick.setVisibility(View.GONE);
                commentMode = "write_comment";
            }
        });

        ok_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok.callOnClick();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ipmm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);

                if ("".equals(edit_comment.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "댓글 내용을 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }

                if ("write_comment".equals(commentMode)) {
                    //댓글 쓰기
                    writeComment(false);
                    edit_comment.setText("");
                } else if ("write_recomment".equals(commentMode)) {
                    //대댓글 쓰기
                    writeComment(true);
                    edit_comment.setText("");
                } else if ("edit_comment".equals(commentMode)) {
                    //댓글 수정
                    editComment();
                } else {
                    writeComment(false);
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        showing(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    void checkMode() {
        SharedPreferences HappyBoom = getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
        SharedPreferences.Editor HappyBoomEditor = HappyBoom.edit();
        final String url = ServerUrl.getBaseUrl() + "/main/loginCheck";
        Map<String, Object> params = new HashMap<String, Object>();
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        if ("1".equals(jsonObject.getString("level"))) {
                            HappyBoomEditor.putString("Mode", "normal");
                            HappyBoomEditor.commit();
                        } else {
                            HappyBoomEditor.putString("Mode", "ceo");
                            HappyBoomEditor.commit();
                        }
                        Log.i(TAG, " Mode " + HappyBoom.getString("Mode", ""));
                    } else if (!jsonObject.getBoolean("return")) {
                        Toast.makeText(CommunityActivity.this, "로그인 후 확인해주세요 !", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    void showing(boolean isReroad) {
        isRoad = isReroad;
        data.clear();
        final String url = ServerUrl.getBaseUrl() + "/community/detail";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("idx", idx);
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        JSONObject jsonData = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));

                        getKey = jsonData.getString("key");
                        videoPath = "video=" + jsonData.getString("video");
                        videoPath += "&image=" + jsonData.getString("image");
                        getContent = jsonData.getString("content");
                        String getProfileImg = jsonData.getString("profile");
                        if (isRoad) {
                            video_view.postUrl(ServerUrl.getBaseUrl() + "/video", videoPath.getBytes());
                        }

//                        video_view.setVideoURI(Uri.parse(videoPath));
//                        video_view.seekTo( 1 );

                        putImage(profile_img, ServerUrl.getBaseUrl() + "/uploads/images/origin/" + getProfileImg);

                        nickname1.setText(jsonData.getString("nick"));
                        date1.setText(jsonData.getString("date"));
                        content1.setText(getContent);
                        like_cnt.setText(jsonData.getString("like"));
                        share_cnt.setText(jsonData.getString("share"));
                        comment_cnt.setText(jsonData.getString("reply"));
                        getMember = jsonData.getString("member");
                        getLevel = jsonData.getString("level");

                        if ("0".equals(jsonData.getString("isfavor"))) {
                            like.setChecked(false);
                        } else {
                            like.setChecked(true);
                        }

                        if ("0".equals(jsonData.getString("ismy"))) {
                            more_con.setVisibility(View.GONE);
                        } else {
                            more_con.setVisibility(View.VISIBLE);
                        }

                        if (jsonArray.length() == 0) {
                            comment_list.setVisibility(View.GONE);
                        } else {
                            comment_list.setVisibility(View.VISIBLE);

//                            comment_list.setAdapter(commentListAdapter);

//                            AsyncTask asyncTask = new AsyncTask() {
//                                @Override
//                                protected Object doInBackground(Object[] objects) {
//
//                                    try{
//                                        for (int i = 0; i < jsonArray.length(); i++) {
//                                            JSONObject getJsonObject = jsonArray.getJSONObject(i);
//                                            data.add(new Comment(getJsonObject.getString("key"), getJsonObject.getString("nick"), getJsonObject.getString("date"), getJsonObject.getString("content"), getJsonObject.getString("ismy"), getJsonObject.getString("isreply"), getJsonObject.getString("member")));
//                                        }
//                                    } catch (JSONException e){
//
//                                    }
//                            commentListAdapter.notifyDataSetChanged();
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setListViewHeightBasedOnChildren(comment_list);
//                                }
//                            });
//                                    return null;
//                                }
//                            }.execute();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject getJsonObject = jsonArray.getJSONObject(i);
                                data.add(new Comment(getJsonObject.getString("key"), getJsonObject.getString("nick"), getJsonObject.getString("date"), getJsonObject.getString("content"), getJsonObject.getString("ismy"), getJsonObject.getString("isreply"), getJsonObject.getString("member")));
                            }
                            comment_list.setAdapter(commentListAdapter);
                            commentListAdapter.notifyDataSetChanged();
                            setListViewHeightBasedOnChildren(comment_list);
                        }
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(CommunityActivity.this, "게시글 불러오기 실패", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    void putImage(ImageView imageView, String getImg) {
        Glide.with(this)
                .load(getImg)
                .into(imageView);
    }

    void shareCount() {
        final String url = ServerUrl.getBaseUrl() + "/community/share";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("key", idx);
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonString);
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?

                    } else if (!jsonObject.getBoolean("return")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    void writeComment(boolean isRecomment) {
        final String url = ServerUrl.getBaseUrl() + "/community/rinsert";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("content", edit_comment.getText().toString());
        params.put("idx", idx);
        if (isRecomment) {
            params.put("target", target);
        }
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?

                        showing(false);

                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(CommunityActivity.this, "다시 시도해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    void editComment() {
        final String url = ServerUrl.getBaseUrl() + "/community/rupdate";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("idx", getKey);
        params.put("content", edit_comment.getText().toString());
        Log.i(TAG, " url " + url);
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(CommunityActivity.this, "댓글을 수정하였습니다.", Toast.LENGTH_SHORT).show();
                        edit_comment.setText("");
                        showing(false);
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(CommunityActivity.this, "다시 시도해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String saveBitmapToJpeg(Context context, Bitmap bitmap) {
        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로
        String fileName = "thumbnail.png";  // 파일이름은 마음대로!
        File tempFile = new File(storage, fileName);
        try {
            tempFile.createNewFile();  // 파일을 생성해주고
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌
            out.close(); // 마무리로 닫아줍니다.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile.getAbsolutePath();   // 임시파일 저장경로를 리턴해주면 끝!
    }

    void getThumbnail() {
        thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        thumbnailFileUrl = saveBitmapToJpeg(CommunityActivity.this, thumbnail);
    }

    public class FullscreenableChromeClient extends WebChromeClient {
        private Activity mActivity = null;

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;

        private FrameLayout mFullscreenContainer;

        private final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        public FullscreenableChromeClient(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }

                mOriginalOrientation = mActivity.getRequestedOrientation();
                FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
                mFullscreenContainer = new FullscreenHolder(mActivity);
                mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
                decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
                mCustomView = view;
                setFullscreen(true);
                mCustomViewCallback = callback;
//          mActivity.setRequestedOrientation(requestedOrientation);
            }

            super.onShowCustomView(view, callback);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
            this.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }

            setFullscreen(false);
            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            decor.removeView(mFullscreenContainer);
            mFullscreenContainer = null;
            mCustomView = null;
            mCustomViewCallback.onCustomViewHidden();
            mActivity.setRequestedOrientation(mOriginalOrientation);
        }

        private void setFullscreen(boolean enabled) {
            Window win = mActivity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            if (enabled) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                winParams.flags |= bits;
            } else {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                winParams.flags &= ~bits;
                if (mCustomView != null) {
                    mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
            win.setAttributes(winParams);
        }

        private class FullscreenHolder extends FrameLayout {
            public FullscreenHolder(Context ctx) {
                super(ctx);
                setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black));
            }

            @Override
            public boolean onTouchEvent(MotionEvent evt) {
                return true;
            }
        }
    }

    public class OneBtnDialog extends Dialog {
        OneBtnDialog oneBtnDialog = this;
        Context context;

        public OneBtnDialog(final Context context, final String text, final String btnText) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_one_btn);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            TextView title1 = (TextView) findViewById(R.id.title1);
            TextView title2 = (TextView) findViewById(R.id.title2);
            TextView btn1 = (TextView) findViewById(R.id.btn1);
            title2.setVisibility(View.GONE);
            title1.setText(text);
            btn1.setText(btnText);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oneBtnDialog.dismiss();
                }
            });
        }
    }

    public class ReportDialog extends Dialog {
        ReportDialog reportDialog = this;
        Context context;

        public ReportDialog(final Context context, final String title, final String idx) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_report);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            TextView title1 = findViewById(R.id.title1);
            EditText content = findViewById(R.id.content);
            final TextView btn1 = (TextView) findViewById(R.id.btn1);
            TextView btn2 = (TextView) findViewById(R.id.btn2);
            title1.setText(title);
            btn1.setText("취소");
            btn2.setText("확인");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportDialog.dismiss();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String url = ServerUrl.getBaseUrl() + "/community/singo";
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("idx", idx);
                    params.put("content", content.getText().toString());
                    aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                            Log.i(TAG, " " + jsonObject);
                            try {
                                if (jsonObject.getBoolean("return")) {    //return이 true 면?
                                    reportDialog.dismiss();
                                    Toast.makeText(context, "해당 게시글을 신고하였습니다.", Toast.LENGTH_SHORT).show();
                                } else if (!jsonObject.getBoolean("return")) {
                                    Toast.makeText(context, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.header("epoch-agent", getToken).header("User-Agent", "android"));
                }
            });
        }
    }

    public class MoreDialog extends Dialog {
        MoreDialog moreDialog = this;
        Context context;

        public MoreDialog(final Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_community);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            final TextView btn1 = (TextView) findViewById(R.id.btn1);
            TextView btn2 = (TextView) findViewById(R.id.btn2);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreDialog.dismiss();
                    //삭제
                    deleteDialog = new DeleteDialog(CommunityActivity.this);
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    deleteDialog.setCancelable(false);
                    deleteDialog.show();

                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreDialog.dismiss();

                    Intent intent = new Intent(CommunityActivity.this, EditActivity.class);
                    intent.putExtra("idx", idx);
                    intent.putExtra("content", getContent);
                    intent.putExtra("video", videoPath);
                    startActivity(intent);
                }
            });
        }
    }

    public class DeleteDialog extends Dialog {
        DeleteDialog deleteDialog = this;
        Context context;

        public DeleteDialog(final Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_two_btn);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            TextView title1 = (TextView) findViewById(R.id.title1);
            TextView title2 = (TextView) findViewById(R.id.title2);
            TextView btn1 = (TextView) findViewById(R.id.btn1);
            TextView btn2 = (TextView) findViewById(R.id.btn2);
            title2.setVisibility(View.GONE);
            title1.setText("영상을 삭제하시겠습니까?");
            btn1.setText("아니요");
            btn2.setText("네");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                    final String url = ServerUrl.getBaseUrl() + "/community/delete";
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("idx", idx);
                    aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                            Log.i(TAG, " " + jsonObject);
                            try {
                                if (jsonObject.getBoolean("return")) {    //return이 true 면?
                                    Toast.makeText(CommunityActivity.this, "해당 글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                    SharedPreferences HappyBoom = getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = HappyBoom.edit();
                                    editor.putBoolean("delete", true);
                                    editor.commit();
                                    finish();
                                } else if (!jsonObject.getBoolean("return")) {
                                    Toast.makeText(CommunityActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.header("epoch-agent", getToken).header("User-Agent", "android"));
                }
            });
        }
    }
}
