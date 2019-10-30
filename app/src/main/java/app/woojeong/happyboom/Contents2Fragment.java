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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.woojeong.happyboom.DTO.MainVideo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Contents2Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Contents2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contents2Fragment extends Fragment {
    private static final String TAG = "Contents2Fragment";
    AQuery aQuery = null;
    Context context;
    View view;
    String getToken, getIdx, getImage, getProfile, getContent, getLikeCnt, getShareCnt, getReplyCnt, getDate, getNick, getIsLike;
    SharedPreferences get_token;
    OneBtnDialog oneBtnDialog;

    private OnFragmentInteractionListener mListener;

    ListView list_view;
    ArrayList<MainVideo> data;
    MainVideoAdapter mainVideoAdapter;

    public Contents2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Contents2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Contents2Fragment newInstance(String param1, String param2) {
        Contents2Fragment fragment = new Contents2Fragment();
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
        view = inflater.inflate(R.layout.fragment_contents2, container, false);
        context = getContext();
        aQuery = new AQuery(context);
        get_token = context.getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        list_view = view.findViewById(R.id.list_view);
        data = new ArrayList<MainVideo>();
        mainVideoAdapter = new MainVideoAdapter(context, R.layout.list_main_item, data, list_view);

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
        final String url = ServerUrl.getBaseUrl() + "/setting/mycommunity";
        Map<String, Object> params = new HashMap<String, Object>();
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                        if (jsonArray.length() == 0) {
                            list_view.setVisibility(View.GONE);
                            Toast.makeText(context, "게시글이 없습니다.",Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            list_view.setVisibility(View.VISIBLE);
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
                            list_view.setAdapter(mainVideoAdapter);
                            mainVideoAdapter.notifyDataSetChanged();
                        }
                    } else if (!jsonObject.getBoolean("return")) {

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