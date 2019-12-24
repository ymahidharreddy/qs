package mizpahsoft.apps.bplus.Adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.Model.NotificationsModel;
import mizpahsoft.apps.bplus.R;

/**
 * Created by Mizpah on 11/10/2016.
 */

public class RecentBloodReqst_Adapter extends BaseAdapter implements ListAdapter {

    public Activity activity;
    private ArrayList<NotificationsModel> arrayList;


    public RecentBloodReqst_Adapter(Activity activity, ArrayList<NotificationsModel> arrayList) {

        this.arrayList = arrayList;
        this.activity = activity;
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
            convertView = activity.getLayoutInflater().inflate(R.layout.recent_bldreq_row, null);

        TextView Name = (TextView) convertView.findViewById(R.id.tv_name);
        Name.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView Location = (TextView) convertView.findViewById(R.id.tv_location);
        Location.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView Message = (TextView) convertView.findViewById(R.id.tv_message);
        Message.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView Time = (TextView) convertView.findViewById(R.id.tv_time);
        Time.setTypeface(Global.setFont(activity,Global.LIGHTFONT));

        NotificationsModel nm = arrayList.get(position);
        Name.setText(nm.getName());
        Message.setText(nm.getMessage());
        Time.setText(nm.getCreatedTime());
        Location.setText(nm.getAddress());

        return convertView;
    }

}

