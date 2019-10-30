package app.woojeong.happyboom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidquery.AQuery;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.woojeong.happyboom.DTO.VideoItem;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {
    Context context;
    int layout;
    ArrayList<VideoItem> data;
    ListView listView;
    AQuery aQuery = null;
    VideoListAdapter videoListAdapter = this;
    String intent;

    public VideoListAdapter(Context context, int layout, ArrayList<VideoItem> data, RecyclerView recyclerView, String intent) {
        this.context = context;
        this.layout = layout;
        this.data = data;
        this.listView = listView;
        this.intent = intent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.title.setText(data.get(i).getTitle());

        putImage(viewHolder.company_image, ServerUrl.getBaseUrl() + "/uploads/images/origin/" + data.get(i).getImage());

        viewHolder.image_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.company_image.callOnClick();
            }
        });

        viewHolder.company_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("video".equals(intent)){
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("idx", data.get(i).getKey());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, CompanyActivity.class);
                    intent.putExtra("idx", data.get(i).getKey());
                    context.startActivity(intent);
                }

            }
        });
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              viewHolder.company_image.callOnClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != data ? data.size() : 0);
    }

    class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView company_image;
        TextView title;
        FrameLayout image_con;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.company_image = view.findViewById(R.id.company_image);
            this.title = view.findViewById(R.id.company_name);
            this.image_con = view.findViewById(R.id.image_con);
        }
    }

    void putImage(ImageView imageView, String getImg){
        Glide.with(context)
                .load(getImg)
                .into(imageView);
    }



}
