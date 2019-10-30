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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.woojeong.happyboom.DTO.MainVideo;
import app.woojeong.happyboom.DTO.VideoItem;
import kotlin.jvm.JvmOverloads;

import static app.woojeong.happyboom.HappyBoommm.REQUEST_CODE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Main1Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Main1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Main1Fragment extends Fragment {
    private static final String TAG = "Main1Fragment";
    AQuery aQuery = null;
    Context context;
    View view;
    String token, mode, getIdx, getImage, getProfile, getContent, getLikeCnt, getShareCnt, getReplyCnt, getDate, getNick, getIsLike;
    SharedPreferences prefToken;
    OneBtnDialog oneBtnDialog;
    TitleDialog titleDialog;

    private OnFragmentInteractionListener mListener;

    Animation button_open, button_close;
    ImageView write, community, edit_video;
    ListView main_list;
    ArrayList<MainVideo> data;
    MainVideoAdapter mainVideoAdapter;

    boolean isFloating;

    boolean encoding;
    Uri videoUri;

    public Main1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Main1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Main1Fragment newInstance(String param1, String param2) {
        Main1Fragment fragment = new Main1Fragment();
        Bundle args = new Bundle();
        args.putString("some_int", param1);
        args.putString("some_string", param2);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main1, container, false);
        context = getContext();
        aQuery = new AQuery(context);
        prefToken = context.getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        token = prefToken.getString("Token", "");
        Log.i(TAG, " token " + token);
        SharedPreferences HappyBoom = context.getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mode = HappyBoom.getString("Mode", "");
                Log.i(TAG, " mode " + mode);
            }
        }, 300);



        write = view.findViewById(R.id.write);
        community = view.findViewById(R.id.community);
        edit_video = view.findViewById(R.id.edit_video);

        button_open = AnimationUtils.loadAnimation(context, R.anim.button_open);
        button_close = AnimationUtils.loadAnimation(context, R.anim.button_close);

        main_list = view.findViewById(R.id.main_list);
        data = new ArrayList<MainVideo>();
        mainVideoAdapter = new MainVideoAdapter(context, R.layout.list_main_item, data, main_list);

        isFloating = false;
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, WriteActivity.class);
//                context.startActivity(intent);

                if ("normal".equals(mode)) {
                    if (isFloating) {
                        community.startAnimation(button_close);
                        edit_video.startAnimation(button_close);
                        community.setVisibility(View.INVISIBLE);
                        edit_video.setVisibility(View.INVISIBLE);
                        isFloating = false;
                    } else {
                        community.startAnimation(button_open);
                        edit_video.startAnimation(button_open);
                        community.setVisibility(View.VISIBLE);
                        edit_video.setVisibility(View.VISIBLE);
                        isFloating = true;
                    }
                } else {
                    Intent intent = new Intent(context, WriteActivity.class);
                    context.startActivity(intent);
                }

            }
        });

        edit_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ContentsActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("video/*");
                startActivityForResult(Intent.createChooser(intent, "video"), 1);
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WriteActivity.class);
                context.startActivity(intent);
            }
        });


        main_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (main_list.canScrollVertically(-1)) {
                    ((MainActivity) getActivity()).tab_con.setVisibility(View.GONE);
                } else if (main_list.canScrollVertically(1)) {
                    ((MainActivity) getActivity()).tab_con.setVisibility(View.VISIBLE);
                } else {
                    ((MainActivity) getActivity()).tab_con.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        listing();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, " zzzzzzzzzzzzzzzzzzzzzzz " + mainVideoAdapter.selectPosition);


        SharedPreferences preferences = context.getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (preferences.getBoolean("reload", false)) {
            listing();
            editor.putBoolean("reload", false);
        }
        if (mainVideoAdapter.selectPosition != -1) {
            if (preferences.getBoolean("delete", false)) {

                editor.putBoolean("delete", false);

                data.remove(mainVideoAdapter.selectPosition);
                mainVideoAdapter.notifyDataSetChanged();
            } else {
                final String url = ServerUrl.getBaseUrl() + "/community/listitem";
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("key", data.get(mainVideoAdapter.selectPosition).getIdx());

                Log.i(TAG, " params " + params);
                aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String jsonStr, AjaxStatus status) {
                        Log.i(TAG, " jsonStr " + jsonStr);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonStr);
                            if (jsonObject.getBoolean("return")) {    //return이 true 면?
                                JSONObject getJsonObject = new JSONObject(jsonObject.getString("data"));

                                getIdx = getJsonObject.getString("idx");
                                getImage = getJsonObject.getString("image");
                                getProfile = getJsonObject.getString("profile");
                                getContent = getJsonObject.getString("content");
                                getLikeCnt = getJsonObject.getString("like");
                                getShareCnt = getJsonObject.getString("share");
                                getReplyCnt = getJsonObject.getString("reply");
                                getDate = getJsonObject.getString("date");
                                getNick = getJsonObject.getString("nick");
                                getIsLike = getJsonObject.getString("islike");
                                data.set(mainVideoAdapter.selectPosition, new MainVideo(getIdx, getImage, getProfile, getNick, getDate, getContent, getLikeCnt, getShareCnt, getReplyCnt, getIsLike));
                                mainVideoAdapter.notifyDataSetChanged();

                            } else if (!jsonObject.getBoolean("return")) {
                                main_list.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.header("epoch-agent", token).header("User-Agent", "android"));
            }

        }

        encoding = preferences.getBoolean("encoding", false);
        String name = preferences.getString("filename", "");
        if (preferences.getBoolean("finish", false)) {
            Log.i(TAG, "encoding" + encoding);
            if (encoding) {
                editor.putBoolean("encoding", false);
                editor.putBoolean("finish", false);
                editor.remove("filename");
                Intent intent = new Intent(context, Send1Activity.class);
                intent.putExtra("path", "file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name + ".mp4");
                startActivity(intent);
            } else {
                editor.putBoolean("encoding", true);
                Intent intent = new Intent(context, BridgeActivity.class);
                intent.putExtra("title", name);
                intent.putExtra("path", "file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name + ".mp4");
                startActivityForResult(intent, REQUEST_CODE);
            }
        } else {
            editor.putBoolean("encoding", false);
        }
        editor.commit();
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

    void listing() {
        data.clear();

        final String url = ServerUrl.getBaseUrl() + "/community/list";
        Map<String, Object> params = new HashMap<String, Object>();

        String search = ((MainActivity) context).searchVideo;
        if (!"".equals(search)) {
            params.put("search", search);
        }
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                        if (jsonArray.length() == 0) {
                            main_list.setVisibility(View.GONE);
                            Toast.makeText(context, "게시글이 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            main_list.setVisibility(View.VISIBLE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject getJsonObject = jsonArray.getJSONObject(i);
                                getIdx = getJsonObject.getString("idx");
                                getImage = getJsonObject.getString("image");
                                getProfile = getJsonObject.getString("profile");
                                getContent = getJsonObject.getString("content");
                                getLikeCnt = getJsonObject.getString("like");
                                getShareCnt = getJsonObject.getString("share");
                                getReplyCnt = getJsonObject.getString("reply");
                                getDate = getJsonObject.getString("date");
                                getNick = getJsonObject.getString("nick");
                                getIsLike = getJsonObject.getString("islike");
                                data.add(new MainVideo(getIdx, getImage, getProfile, getNick, getDate, getContent, getLikeCnt, getShareCnt, getReplyCnt, getIsLike));
                            }
                            main_list.setAdapter(mainVideoAdapter);
                            mainVideoAdapter.notifyDataSetChanged();
                        }
                    } else if (!jsonObject.getBoolean("return")) {
                        main_list.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", token).header("User-Agent", "android"));
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

    public void reload() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listing();
            }
        }, 400);
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
