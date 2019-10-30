package app.woojeong.happyboom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidquery.AQuery;

import java.io.File;
import java.util.ArrayList;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class Send2Activity extends AppCompatActivity {
    private static String TAG = "Send2Activity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;

    SharedPreferences get_token;
    String getToken = "", getPhone = "", getName = "", getPath = "";

    FrameLayout back_con, share_con, contact_con, ok_con;
    ImageView back, share, contact;
    TextView ok;

    MediaController controller;
    VideoView video_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(Send2Activity.this, true);
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

        share_con = findViewById(R.id.share_con);
        contact_con = findViewById(R.id.contact_con);
        ok_con = findViewById(R.id.ok_con);
        share = findViewById(R.id.share);
        contact = findViewById(R.id.contact);
        ok = findViewById(R.id.ok);

        video_view = findViewById(R.id.video_view);
        controller = new MediaController(Send2Activity.this);
        video_view.setMediaController(controller);
        video_view.setVideoURI(Uri.parse(getPath));
        video_view.seekTo(1);

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
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(getPath));
                Intent shareIntent = Intent.createChooser(intent, "해피붐 영상 공유");  //공유 타이틀
                startActivity(shareIntent);
            }
        });

        contact_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact.callOnClick();
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 0);

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
                //문자 보내기
//                if ("".equals(getPhone)) {
//                    oneBtnDialog = new OneBtnDialog(Send2Activity.this, "공유할 연락처를 선택해주세요 !", "확인");
//                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    oneBtnDialog.setCancelable(false);
//                    oneBtnDialog.show();
//                    return;
//                }
//
//                ArrayList<String> videoList = new ArrayList<>();
//                videoList.add(getPath.replace("file://", ""));
//                SendMMS(context, videoList);
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(getPath));
                Intent shareIntent = Intent.createChooser(intent, "해피붐 영상 공유");  //공유 타이틀
                startActivity(shareIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
            cursor.moveToFirst();

            getName = cursor.getString(0);
            Log.i(TAG, " 이름 " + getName); //이름 얻어오기

            getPhone = cursor.getString(1);
            Log.i(TAG, " 번호 " + getPhone); //번호 얻어오기

            Toast.makeText(Send2Activity.this, getName + " 님을 선택하셨습니다.\n(번호: " + getPhone + " )", Toast.LENGTH_SHORT).show();
            cursor.close();
        }
    }

    private void SendMMS(Context context, ArrayList<String> urlString) {
        boolean exceptionCheck = false;
        Intent sendIntent = new Intent();

        // Selection count
        if (urlString.size() > 1) {
            sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        } else if (urlString.size() == 1) {
            sendIntent.setAction(Intent.ACTION_SEND);
        } else {
            Toast.makeText(this, "Please Check the Image.", Toast.LENGTH_LONG).show();
            exceptionCheck = true;
        }
        if (!exceptionCheck) {
            sendIntent.setData(Uri.parse("mmsto:" + getPhone));
            sendIntent.addCategory("android.intent.category.DEFAULT");

            ArrayList<Uri> uris = new ArrayList<Uri>();
            for (String file : urlString) {
                File fileIn = new File("content://" + file);
                Uri u = Uri.fromFile(fileIn);
                uris.add(u);
            }
            sendIntent.setType("video/mp4");
            if (urlString.size() > 1) {
                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            } else {
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + urlString.get(0)));
            }
        }
        try {
            startActivity(sendIntent);
        } catch (Exception e) {
            Log.i(TAG, " eeee " + e);
            Toast.makeText(this, "전송 실패", Toast.LENGTH_LONG).show();
        }
    }

    public void sendMMSIntent(String number, Uri videoUri){
        try{
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra("address", number);
            sendIntent.putExtra("subject", "해피붐");
            sendIntent.putExtra("sms_body", "이 동영상을 공유합니다!");
            sendIntent.setType("video/*");
            sendIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
            startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.app_name)));
        }catch (Exception e){
            e.printStackTrace();
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
}
