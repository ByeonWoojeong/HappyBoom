package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.LogCallback;
import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.dev.hongsw.happyBoom.subtitle.AssWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.woojeong.happyboom.DTO.VideoItem;
import app.woojeong.happyboom.model.ListViewModel;
import app.woojeong.happyboom.subtitle.DefaultStyle;
import kotlin.jvm.JvmOverloads;

import static app.woojeong.happyboom.HappyBoommm.REQUEST_CODE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Contents1Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Contents1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contents1Fragment extends Fragment {
    private static final String TAG = "Contents1Fragment";
    AQuery aQuery = null;
    static Context context;
    View view;
    String getToken, mode;
    SharedPreferences get_token, HappyBoom;
    OneBtnDialog oneBtnDialog;
    TitleDialog titleDialog;

    private OnFragmentInteractionListener mListener;

    ImageView write, choice_back;

    RecyclerView recycler_view;
    ArrayList<VideoItem> data;
    VideoListAdapter videoListAdapter;
    GridLayoutManager gridLayoutManager;
    private int count = -1;

    HappyBoommm happyBoommm;
    File mInputFile, mImageFile, mOutput;
    Uri mEncodingVideo;
    int mVideoWidth = 0, mVideoHeight = 0;
    long mPlayTime = 0L;
    DefaultStyle mDefaultStyle;
    ArrayList<ListViewModel> encodingDataList;
    boolean encoding;

    Uri videoUri;

    public Contents1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Contents1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Contents1Fragment newInstance(String param1, String param2) {
        Contents1Fragment fragment = new Contents1Fragment();
        Bundle args = new Bundle();
        args.putString("some_int", param1);
        args.putString("some_title", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "  onResume");
        // 파일 이름이 있을 때 :
        // 인코드가 false -> 인코드를 true로 바꿔주고, 브릿지액티비티로 넘겨준다
        // 인코드가 true -> 인코드를 false로 바꿔주고, 발송하기 화면으로 넘겨준다
        // 발송하기 화면에서는 파일이름 리무브

        reload();

        SharedPreferences preferences = context.getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

//        encoding = preferences.getBoolean("encoding", false);
//        String name = preferences.getString("filename", "");
//
//        Log.i(TAG, "encoding " + encoding);
//        Log.i(TAG, "finish " + preferences.getBoolean("finish", false));
//        Log.i(TAG, "filename " + name);
//        if (preferences.getBoolean("finish", false)) {
//            if (encoding) {
//                editor.putBoolean("encoding", false);
//                editor.putBoolean("finish", false);
//                editor.remove("filename");
//                Intent intent = new Intent(context, Send1Activity.class);
//                intent.putExtra("path", "file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name + ".mp4");
//                startActivity(intent);
//            } else {
//                editor.putBoolean("encoding", true);
//                Intent intent = new Intent(context, BridgeActivity.class);
//                intent.putExtra("title", name);
//                intent.putExtra("path", "file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name + ".mp4");
//                startActivityForResult(intent, REQUEST_CODE);
//            }
//        } else {
//            editor.putBoolean("encoding", false);
//        }
//        editor.apply();
//        editor.commit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contents1, container, false);

        context = getContext();
        aQuery = new AQuery(context);
        get_token = context.getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");
        HappyBoom = context.getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
        mode = HappyBoom.getString("Mode", ""); // normal , ceo

        choice_back = view.findViewById(R.id.choice_back);
        write = view.findViewById(R.id.write);

        if ("normal".equals(mode)) {
            write.setBackground(getResources().getDrawable(R.drawable.edit_video));
        } else {
            write.setBackground(getResources().getDrawable(R.drawable.write));
        }
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("normal".equals(mode)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("video/*");
                    startActivityForResult(Intent.createChooser(intent, "video"), 1);
                } else {
                    Intent intent = new Intent(context, UploadVideoActivity.class);
                    startActivity(intent);
                }
            }
        });

        recycler_view = view.findViewById(R.id.recycler_view);
        gridLayoutManager = new GridLayoutManager(context, 2);
        recycler_view.setLayoutManager(gridLayoutManager);
        data = new ArrayList<VideoItem>();
        videoListAdapter = new VideoListAdapter(context, R.layout.list_video_item, data, recycler_view, "video");

        if ("normal".equals(mode)) {
            choice_back.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);
        } else {
            choice_back.setVisibility(View.GONE);
            recycler_view.setVisibility(View.VISIBLE);
        }

//        reload();

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    @JvmOverloads
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {

                if (data != null) {
                    videoUri = data.getData();
                    String videoPath = getRealPathFromURI(data.getData());

                    Log.i(TAG, " data(File Path, Uri) " + data.getData());
                    Log.i(TAG, " 절대경로(String) " + Environment.getExternalStorageDirectory().getAbsolutePath());    // 절대경로 : /storage/emulated/0

                    // 영상 선택 하면, 영상 제목 입력할 다이얼로그 띄우기 : dialog_input
                    titleDialog = new TitleDialog(context, "영상 제목 입력");
                    titleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    titleDialog.setCancelable(false);
                    titleDialog.show();
                }

            } else if (requestCode == REQUEST_CODE) {

            }
        }

        // when - switch
        // !! - nullable이면 오류가 발생
    }

    void companyOnly() {

        data.clear();
        final String url = ServerUrl.getBaseUrl() + "/setting/mypremium";
        Map<String, Object> params = new HashMap<String, Object>();
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        final JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                        if (jsonArray.length() == 0) {
                            recycler_view.setVisibility(View.GONE);
                        } else {
                            recycler_view.setVisibility(View.VISIBLE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject getJsonObject = jsonArray.getJSONObject(i);
                                data.add(new VideoItem(getJsonObject.getString("title"), getJsonObject.getString("image"), getJsonObject.getString("idx")));
                            }
                            recycler_view.setAdapter(videoListAdapter);
                            videoListAdapter.notifyDataSetChanged();
                        }
                    } else if (!jsonObject.getBoolean("return")) {
                        Toast.makeText(context, "게시글이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    void reload() {
        if (!"normal".equals(mode)) {
            companyOnly();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

        cursor.close();
        return path;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    boolean writeAss() {

        try {
            AssWriter assWriter = new AssWriter(context);
            assWriter.addPlayRec(mVideoWidth, mVideoHeight);
            assWriter.addStyle(mDefaultStyle);
//        assWriter.addSubtitles();
            assWriter.endWrite();
        } catch (Exception e) {
            return false;
        }

        return true;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class OneBtnDialog extends Dialog {
        OneBtnDialog oneBtnDialog = this;
        Context context;

        public OneBtnDialog(final Context context, final String text, final String btnText, final boolean encoding) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_one_btn);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            TextView title1 = (TextView) findViewById(R.id.title1);
            TextView title2 = (TextView) findViewById(R.id.title2);
            TextView btn1 = (TextView) findViewById(R.id.btn1);
            title1.setText(text);
            if (encoding) {
                title2.setText("인코딩 성공");
            } else {
                title2.setText("인코딩 실패");
            }
            btn1.setText(btnText);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oneBtnDialog.dismiss();
                }
            });
        }
    }

    public class TitleDialog extends Dialog {
        TitleDialog titleDialog = this;
        Context context;

        public TitleDialog(final Context context, final String title) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_input);
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
                    titleDialog.dismiss();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleDialog.dismiss();

                    // 일반 사용자 용 (영상 편집 액티비티로) - HappyBoommm 으로 보내는데 추가 파라미터가 없음 (기업용은 파라미터를 따로 전달해서 받음)
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), content.getText().toString());

                    Intent intent = new Intent(context, BridgeActivity.class);
                    intent.putExtra("title", content.getText().toString());
                    intent.putExtra("path", videoUri.toString());
                    intent.putExtra("skip", true);
//                    intent.putExtra("premium", false);
                    startActivityForResult(intent, REQUEST_CODE);


                }
            });
        }
    }


}
