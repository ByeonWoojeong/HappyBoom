package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.woojeong.happyboom.DTO.MainVideo;
import app.woojeong.happyboom.DTO.VideoItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Contents3Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Contents3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contents3Fragment extends Fragment {
    private static final String TAG = "Contents3Fragment";
    AQuery aQuery = null;
    Context context;
    View view;
    String getToken;
    SharedPreferences get_token;
    OneBtnDialog oneBtnDialog;

    private OnFragmentInteractionListener mListener;

    RecyclerView recycler_view;
    ArrayList<VideoItem> data;
    VideoListAdapter videoListAdapter;
    GridLayoutManager gridLayoutManager;
    private int count = -1;

    public Contents3Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Contents3Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Contents3Fragment newInstance(String param1, String param2) {
        Contents3Fragment fragment = new Contents3Fragment();
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
        view = inflater.inflate(R.layout.fragment_contents3, container, false);
        context = getContext();
        aQuery = new AQuery(context);
        get_token = context.getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        recycler_view = view.findViewById(R.id.recycler_view);
        gridLayoutManager = new GridLayoutManager(context, 2);
        recycler_view.setLayoutManager(gridLayoutManager);
        data = new ArrayList<VideoItem>();
        videoListAdapter = new VideoListAdapter(context, R.layout.list_video_item, data, recycler_view, "company");

        listing();

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

    void listing(){
        data.clear();
        final String url = ServerUrl.getBaseUrl() + "/setting/mylike";
        Map<String, Object> params = new HashMap<String, Object>();
        aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String jsonString, AjaxStatus status) {
                Log.i(TAG, " jsonString " + jsonString);

                try {
                    JSONObject  jsonObject = new JSONObject(jsonString);

                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                        if (jsonArray.length() == 0) {
                            recycler_view.setVisibility(View.GONE);
                            Toast.makeText(context, "게시글이 없습니다.",Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            recycler_view.setVisibility(View.VISIBLE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject getJsonObject = jsonArray.getJSONObject(i);
                                data.add(new VideoItem(getJsonObject.getString("title"), getJsonObject.getString("image"), getJsonObject.getString("key")));
                            }
                            recycler_view.setAdapter(videoListAdapter);
                            videoListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        videoListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
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
