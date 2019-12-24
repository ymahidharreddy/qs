package mizpahsoft.apps.bplus.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import mizpahsoft.apps.bplus.utils.CircleTransform;
import mizpahsoft.apps.bplus.Activities.DetailIndividualOne;
import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.Model.EventsModel;
import mizpahsoft.apps.bplus.R;

/**
 * Created by Mizpah-DEV on 23-Nov-16.
 */

public class AdapterIndividualEvent extends BaseAdapter implements ListAdapter {
    public final Activity activity;
    private ArrayList<EventsModel> arrayList;
    public final String userid;

    public AdapterIndividualEvent(Activity activity, ArrayList<EventsModel> arrayList, String userid) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.userid = userid;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.adapter_individual_event, null);


        TextView Name = (TextView) convertView.findViewById(R.id.tv_name);
        Name.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView tv_status = (TextView) convertView.findViewById(R.id.tv_status);
        tv_status.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView Location = (TextView) convertView.findViewById(R.id.tv_location);
        Location.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView Time = (TextView) convertView.findViewById(R.id.tv_time);
        Time.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        ImageView iv_image = (ImageView) convertView.findViewById(R.id.list_image);
        LinearLayout ll_layout = (LinearLayout) convertView.findViewById(R.id.ll_layout);
        EventsModel em = arrayList.get(position);
        try {
            Name.setText(em.getEvent());
            Time.setText(em.getEventDate());
            Location.setText(em.getEventAddress());
            String img = em.getEventImage();
            String val = em.getIsActive();
            if (val.equalsIgnoreCase("0")) {
                tv_status.setText("Status : Pending");
            } else if (val.equalsIgnoreCase("1")) {
                tv_status.setText("Status : Approved");
            } else {
                tv_status.setText("Status : Rejected");
            }
            String url = Global.Events_image + img;
            Picasso.with(activity).load(url).transform(new CircleTransform()).error(R.mipmap.no_image).into(iv_image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ll_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, DetailIndividualOne.class);
                i.putExtra("eventid", arrayList.get(position).getEventId());
                i.putExtra("from", "myevents");
                activity.startActivityForResult(i, Global.ACTIVITY_FOR_RESULT);
                activity.finish();
            }
        });
        return convertView;
    }
}
