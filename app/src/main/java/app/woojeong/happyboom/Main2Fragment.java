package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.woojeong.happyboom.DTO.Comment;
import app.woojeong.happyboom.DTO.VideoItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Main2Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Main2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */


//http://blog.naver.com/PostView.nhn?blogId=tpgns8488&logNo=220983389569&parentCategoryNo=&categoryNo=39&viewDate=&isShowPopularPosts=false&from=postView

public class Main2Fragment extends Fragment {
    private static final String TAG = "Main2Fragment";
    AQuery aQuery = null;
    Context context;
    View view;
    String token;
    SharedPreferences prefToken;
    OneBtnDialog oneBtnDialog;

    private OnFragmentInteractionListener mListener;

    RecyclerView recycler_view;
    ArrayList<VideoItem> data;
    VideoListAdapter videoListAdapter;
    GridLayoutManager gridLayoutManager;
    private int count = -1;

    public Main2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Main2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Main2Fragment newInstance(String param1, String param2) {
        Main2Fragment fragment = new Main2Fragment();
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

//        container.getContext();
        view = inflater.inflate(R.layout.fragment_main2, container, false);
        context = getContext();
        aQuery = new AQuery(context);
        prefToken = context.getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        token = prefToken.getString("Token", "");

        recycler_view = view.findViewById(R.id.recycler_view);
        gridLayoutManager = new GridLayoutManager(context, 2);
        recycler_view.setLayoutManager(gridLayoutManager);
        data = new ArrayList<VideoItem>();
        videoListAdapter = new VideoListAdapter(context, R.layout.list_video_item, data, recycler_view, "company");
        recycler_view.setAdapter(videoListAdapter);


        recycler_view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    ((MainActivity)getActivity()).tab_con.setVisibility(View.VISIBLE);
                } else {
                    ((MainActivity)getActivity()).tab_con.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showing();
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

    void showing() {
        data.clear();
        final String url = ServerUrl.getBaseUrl() + "/premium/list";
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
                        final JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                        if (jsonArray.length() == 0) {
                            recycler_view.setVisibility(View.GONE);
                        } else {
                            recycler_view.setVisibility(View.VISIBLE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject getJsonObject = jsonArray.getJSONObject(i);
                                data.add(new VideoItem(getJsonObject.getString("title"), getJsonObject.getString("image"), getJsonObject.getString("key")));
                            }
                            recycler_view.setAdapter(videoListAdapter);
                            videoListAdapter.notifyDataSetChanged();
                        }
                    } else if (!jsonObject.getBoolean("return")) {
                        recycler_view.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", token).header("User-Agent", "android"));
    }

    public void reload() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showing();
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
}
