package mizpahsoft.apps.bplus.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.Model.NotificationsModel;
import mizpahsoft.apps.bplus.utils.CircleTransform;
import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.R;

/**
 * Created by Mizpah on 11/12/2016.
 */

public class UnreadNotificationsArrayAdapter extends BaseAdapter implements ListAdapter {

    public final Activity activity;
    private ArrayList<NotificationsModel> arrayList;

    public UnreadNotificationsArrayAdapter(Activity activity, ArrayList<NotificationsModel> arrayList) {

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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.blood_req_row, null);


        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        name.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView location = (TextView) convertView.findViewById(R.id.tv_location);
        location.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView age = (TextView) convertView.findViewById(R.id.tv_age);
        age.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView bloodGrp = (TextView) convertView.findViewById(R.id.tv_bloodgroup);
        bloodGrp.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView req_id = (TextView) convertView.findViewById(R.id.tv_req_id);
        req_id.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        ImageView userPic = (ImageView) convertView.findViewById(R.id.image_userpic);
        TextView tv_Date = (TextView) convertView.findViewById(R.id.tv_date);

        NotificationsModel nm = arrayList.get(position);

        name.setText(nm.getName());
        location.setText(nm.getAddress());
        bloodGrp.setText(nm.getBloodGroup());
        age.setText(nm.getAge());
        req_id.setText(nm.getB_req_id());
        tv_Date.setText(nm.getNotiCreatedTime());
        String profilePicture = nm.getProfilePicture();
        if (!(profilePicture.equals("")) && !(profilePicture.equals(null))) {
            String url = Global.Images_Base_URL + "uploads/profilePics/" + profilePicture;
            Picasso.with(activity).load(url).transform(new CircleTransform()).error(R.mipmap.no_image).into(userPic);
        }

        return convertView;
    }
}

