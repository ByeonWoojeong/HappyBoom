package app.woojeong.happyboom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidquery.AQuery;

import java.io.File;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class Send1Activity extends AppCompatActivity {
    private static String TAG = "Send1Activity";
    static Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    DeleteDialog deleteDialog;

    SharedPreferences get_token;
    String getToken = "", getPath;

    FrameLayout back_con, edit_con;
    ImageView back;
    TextView edit, delete, send;

    MediaController controller;
    VideoView video_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(Send1Activity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        getPath = getIntent().getStringExtra("path");
        Log.i(TAG, " getPath " + getPath);

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

//        edit_con = findViewById(R.id.edit_con);
//        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.delete);
        send = findViewById(R.id.send);

        video_view = findViewById(R.id.video_view);
        controller = new MediaController(Send1Activity.this);
        video_view.setMediaController(controller);
        video_view.setVideoURI(Uri.parse(getPath));
        video_view.seekTo(1);

//        edit_con.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                edit.callOnClick();
//            }
//        });
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(Send1Activity.this, "영상 제목 수정", Toast.LENGTH_SHORT).show();
//            }
//        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog = new DeleteDialog(Send1Activity.this);
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.setCancelable(false);
                deleteDialog.show();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Send1Activity.this, Send2Activity.class);
//                intent.putExtra("path", getPath);
//                startActivity(intent);
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "app.woojeong.happyboom.fileprovider",new File(getPath.replace("file://", ""))));
                Intent shareIntent = Intent.createChooser(intent, "해피붐 영상 공유");  //공유 타이틀
                startActivity(shareIntent);
            }
        });
    }

    public static boolean fileDelete(String filePath){
        //filePath : 파일경로 및 파일명이 포함된 경로입니다.

        try {
            Log.i(TAG, "  filePath " + filePath);
            File file = new File(filePath.replace("file://", ""));
            // 파일이 존재 하는지 체크
            if(file.exists()) {
                file.delete();
                Toast.makeText(context,"해당 영상 파일을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                return true;  // 파일 삭제 성공여부를 리턴값으로 반환해줄 수 도 있습니다.
            } else {
                Log.i(TAG, "123");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
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
                    boolean finish = fileDelete(getPath);
                    if(finish){
                        finish();
                    }
                    deleteDialog.dismiss();
                }
            });
        }
    }
}
