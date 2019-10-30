package app.woojeong.happyboom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.woojeong.happyboom.DTO.One;
import app.woojeong.happyboom.DTO.Two;

public class EventAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Two> data;
    ListView listView;
    AQuery aQuery = null;
    EventAdapter eventAdapter = this;

    public EventAdapter(Context context, int layout, ArrayList<Two> data, ListView listView) {
        this.context = context;
        this.layout = layout;
        this.data = data;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        aQuery = new AQuery(context);

        if (convertView == null) {
            convertView = View.inflate(context, layout, null);
            vh = new ViewHolder();
            vh.event = convertView.findViewById(R.id.image);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Two item = data.get(position);

       final ViewHolder finalVh = vh;

        putImage(vh.event, ServerUrl.getBaseUrl() + "/uploads/images/origin/" + item.getText2());
        vh.event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("key", item.getText1());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder{
        ImageView event;
    }

    void putImage(ImageView imageView, String getImg){
        Glide.with(context)
                .load(getImg)
                .into(imageView);
    }

}
