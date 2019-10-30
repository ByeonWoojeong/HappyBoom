package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class JoinCEOActivity extends AppCompatActivity {
    private static String TAG = "JoinCEOActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    JoinDialog joinDialog;
    TwoBtnDialog twoBtnDialog;

    SharedPreferences get_token;
    String getToken, getType;

    FrameLayout back_con, ok_con;
    LinearLayout sns_con;
    ImageView back, overlap, send, verify, attach;
    ScrollView scrollView;
    EditText id, password, password2, phone, my_number;
    CheckBox check1, check2, check3, check_all;
    TextView business_card, agree1, agree2, agree3, ok;

    boolean isId, isPhone, isAttach, isCheck1, isCheck2, isCheck3, isVerify;

    ArrayList<Bitmap> originalBitmap = new ArrayList<Bitmap>();
    ArrayList<Bitmap> resizeBitmap = new ArrayList<Bitmap>();
    ArrayList<String> imagePath = new ArrayList<String>();
    ArrayList<File> file = new ArrayList<File>();
    String filePath;
    Uri fileUri;
    int CAMERA = 700, GALLERY = 800, totalImageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_ceo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(JoinCEOActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ipmm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        getType = getIntent().getStringExtra("type");

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

        ok_con = findViewById(R.id.ok_con);
        sns_con = findViewById(R.id.sns_con);

        if("sns".equals(getType)){
            sns_con.setVisibility(View.GONE);
        }else {
            sns_con.setVisibility(View.VISIBLE);
        }

        overlap = findViewById(R.id.overlap);
        send = findViewById(R.id.send);
        verify = findViewById(R.id.verify);
        attach = findViewById(R.id.attach);

        id = findViewById(R.id.id);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        phone = findViewById(R.id.phone);
        my_number = findViewById(R.id.my_number);
        business_card = findViewById(R.id.business_card);

        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        check_all = findViewById(R.id.check_all);

        agree1 = findViewById(R.id.agree1);
        agree2 = findViewById(R.id.agree2);
        agree3 = findViewById(R.id.agree3);
        ok = findViewById(R.id.ok);

        overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(id.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "아이디를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }
                idDuplication();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(phone.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "핸드폰 번호를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if (phone.length() < 11 && 11 < phone.length()) {
                    oneBtnDialog = new OneBtnDialog(context, "핸드폰 번호를\n정확하게 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if (!phone.getText().toString().startsWith("010")) {
                    oneBtnDialog = new OneBtnDialog(context, "핸드폰 번호를\n정확하게 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }
                sendCertSMS();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(my_number.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "인증 번호를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if (phone.length() < 4 && 4 < phone.length()) {
                    oneBtnDialog = new OneBtnDialog(context, "인증 번호를\n정확하게 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }

                verifySMS();
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipmm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
                twoBtnDialog = new TwoBtnDialog(JoinCEOActivity.this);
                twoBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                twoBtnDialog.show();
            }
        });

        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheck1 = true;

                    if (check2.isChecked() && check3.isChecked()) {
                        check_all.setChecked(true);
                    }
                } else {
                    isCheck1 = false;
                }
            }
        });

        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheck2 = true;
                    if (check1.isChecked() && check3.isChecked()) {
                        check_all.setChecked(true);
                    }
                } else {
                    isCheck2 = false;
                }
            }
        });

        check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheck3 = true;
                    if (check1.isChecked() && check2.isChecked()) {
                        check_all.setChecked(true);
                    }
                } else {
                    isCheck3 = false;
                }
            }
        });

        check_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheck1 = true;
                    isCheck2 = true;
                    isCheck3 = true;
                    check1.setChecked(true);
                    check2.setChecked(true);
                    check3.setChecked(true);
                } else {
                    isCheck1 = false;
                    isCheck2 = false;
                    isCheck3 = false;
                    check1.setChecked(false);
                    check2.setChecked(false);
                    check3.setChecked(false);
                }
            }
        });

        agree1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinCEOActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 1);
                startActivity(intent);
            }
        });
        agree2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinCEOActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 2);
                startActivity(intent);
            }
        });
        agree3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinCEOActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 3);
                startActivity(intent);
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
                if("sns".equals(getType)){
                    if ("".equals(business_card.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "사업자 등록증을 첨부해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if (!isCheck1 || !isCheck2 || !isCheck3) {
                        oneBtnDialog = new OneBtnDialog(context, "동의 사항을 동의해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    }
                    joinSNS();
                }else {
                    if (!isId || "".equals(id.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "아이디 중복 확인을 해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if ("".equals(password.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "비밀 번호를 입력해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if ("".equals(password2.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "비밀 번호 확인을 입력해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if (!isPhone || "".equals(phone.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "핸드폰 번호를\n인증해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if ("".equals(my_number.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "인증 번호를 입력해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if (!isVerify) {
                        oneBtnDialog = new OneBtnDialog(context, "인증 번호를\n다시 확인해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    }  else if ("".equals(business_card.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "사업자 등록증을 첨부해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if (!isCheck1 || !isCheck2 || !isCheck3) {
                        oneBtnDialog = new OneBtnDialog(context, "동의 사항을 동의해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    }
                    joinBiz();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult " + requestCode);
        switch (requestCode) {
            case 1:
                break;
            case 700:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    try {
                        if (Build.VERSION.SDK_INT < 21) {
                            Uri imgUri = data.getData();
                            imagePath.clear();
                            imagePath.add(getImageRealPathFromURI(JoinCEOActivity.this.getContentResolver(), imgUri));
                        }
                        ExifInterface exif = new ExifInterface(imagePath.get(totalImageCount));
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        originalBitmap.clear();
                        resizeBitmap.clear();
                        originalBitmap.add(rotate(BitmapFactory.decodeFile(imagePath.get(totalImageCount)), exifDegree));
                        resizeBitmap.add(rotate(resizeBitmap(imagePath.get(totalImageCount), 1080, 1920), exifDegree));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    business_card.setText(imagePath.get(totalImageCount));
                    isAttach = true;
                }
                break;
            case 800:
                if (resultCode == RESULT_CANCELED) {
                    return;
                } else {
                    try {
                        Uri imgUri = data.getData();
                        imagePath.clear();
                        imagePath.add(getImageRealPathFromURI(JoinCEOActivity.this.getContentResolver(), imgUri));
                        ExifInterface exif = new ExifInterface(imagePath.get(0));
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        originalBitmap.clear();
                        resizeBitmap.clear();
                        originalBitmap.add(rotate(BitmapFactory.decodeFile(imagePath.get(totalImageCount)), exifDegree));
                        resizeBitmap.add(rotate(resizeBitmap(imagePath.get(totalImageCount), 1080, 1920), exifDegree));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    business_card.setText(imagePath.get(totalImageCount).toString());
                    isAttach = true;
                }
                break;
            case 900:
                break;
            case RESULT_CANCELED:
                break;
        }
    }

    Uri getFileUri() {
        File dir = new File(getFilesDir(), "Picture");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, System.currentTimeMillis() + ".png");
        imagePath.add(file.getAbsolutePath() + "");
        return FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
    }

    String getImageRealPathFromURI(ContentResolver contentResolver, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            int path = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String tmp = cursor.getString(path);
            cursor.close();
            return tmp;
        }
    }

    int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {

            }
        }
        return bitmap;
    }

    Bitmap resizeBitmap(String file, int width, int height) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    //아이디 중복 체크
    void idDuplication() {
        final String url = ServerUrl.getBaseUrl() + "/join/id";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id.getText().toString());
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(JoinCEOActivity.this, "사용 가능한 ID 입니다.", Toast.LENGTH_SHORT).show();
                        isId = true;
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(JoinCEOActivity.this, "중복된 ID 입니다.", "확인");
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

    //휴대폰 인증번호 요청
    void sendCertSMS() {
        final String url = ServerUrl.getBaseUrl() + "/join/certsms";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("phone", phone.getText().toString());
        params.put("level", "7");
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(JoinCEOActivity.this, "인증 번호를 보냈습니다.", Toast.LENGTH_SHORT).show();
//                        if(null != jsonObject.getString("cert") && !"".equals(jsonObject.getString("cert"))){
//                            Toast.makeText(JoinCEOActivity.this, "인증 번호 : " + jsonObject.getString("cert"), Toast.LENGTH_SHORT).show();
//                        }
                        isPhone = true;
                    } else if (!jsonObject.getBoolean("return")) {
                        if("dup".equals(jsonObject.getString("type"))){
                            oneBtnDialog = new OneBtnDialog(JoinCEOActivity.this, "중복된 전화번호 입니다 !", "확인");
                            oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            oneBtnDialog.setCancelable(false);
                            oneBtnDialog.show();
                            return;
                        }
                        oneBtnDialog = new OneBtnDialog(JoinCEOActivity.this, "다시 시도해주세요 !", "확인");
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

    //휴대폰 인증번호 확인
    void verifySMS() {
        final String url = ServerUrl.getBaseUrl() + "/join/verifysms";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("phone", phone.getText().toString());
        params.put("cert", my_number.getText().toString());
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(JoinCEOActivity.this, "인증 번호가 확인되었습니다.", Toast.LENGTH_SHORT).show();
                        isVerify = true;
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(JoinCEOActivity.this, "인증 번호 확인을\n다시 시도해주세요 !", "확인");
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


    //회원 가입( 일반 )
    void joinBiz(){
        final String url = ServerUrl.getBaseUrl() + "/join/biz";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id.getText().toString());
        params.put("pass", password.getText().toString());
        file.clear();
        if (resizeBitmap.size() == 1) {
            fileUri = getImageUri(JoinCEOActivity.this, resizeBitmap.get(0));
            filePath = getImageRealPathFromURI(JoinCEOActivity.this.getContentResolver(), fileUri);
            File makeFile = new File(filePath);
            params.put("cert", makeFile);
        }

        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        joinDialog = new JoinDialog(context, "회원 가입을\n완료하였습니다.", "확인");
                        joinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        joinDialog.setCancelable(false);
                        joinDialog.show();
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(JoinCEOActivity.this, "회원 가입을\n실패 하였습니다.", "확인");
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

    //회원 가입( SNS )
    void joinSNS(){
        final String url = ServerUrl.getBaseUrl() + "/join/profile";
        Map<String, Object> params = new HashMap<String, Object>();
        file.clear();
        if (resizeBitmap.size() == 1) {
            fileUri = getImageUri(JoinCEOActivity.this, resizeBitmap.get(0));
            filePath = getImageRealPathFromURI(JoinCEOActivity.this.getContentResolver(), fileUri);
            File makeFile = new File(filePath);
            params.put("cert", makeFile);
        }

        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        joinDialog = new JoinDialog(context, "회원 가입을\n완료하였습니다.", "확인");
                        joinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        joinDialog.setCancelable(false);
                        joinDialog.show();
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(JoinCEOActivity.this, "회원 가입을\n실패 하였습니다.", "확인");
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

    public class JoinDialog extends Dialog {
        JoinDialog joinDialog = this;
        Context context;

        public JoinDialog(final Context context, final String text, final String btnText) {
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
                    joinDialog.dismiss();
                    Intent intent = new Intent(JoinCEOActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    public class TwoBtnDialog extends Dialog {
        TwoBtnDialog twoBtnDialog = this;
        Context context;

        public TwoBtnDialog(final Context context) {
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
            title1.setText("이미지 첨부방식을\n선택해 주세요 !");
            btn1.setText("카메라");
            btn2.setText("갤러리");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    twoBtnDialog.dismiss();
                    if (Build.VERSION.SDK_INT > 21) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri());
                        startActivityForResult(intent, CAMERA);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAMERA);
                    }
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    twoBtnDialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY);
                }
            });
        }
    }
}
