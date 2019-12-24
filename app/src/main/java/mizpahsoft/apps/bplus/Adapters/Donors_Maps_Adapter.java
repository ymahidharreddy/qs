package mizpahsoft.apps.bplus.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.Activities.IndividualDetails;
import mizpahsoft.apps.bplus.Activities.MapsActivity;
import mizpahsoft.apps.bplus.Activities.Model;
import mizpahsoft.apps.bplus.Model.SearchModel;
import mizpahsoft.apps.bplus.utils.CircleTransform;
import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.R;

/**
 * Created by Mizpah on 11/8/2016.
 */

public class Donors_Maps_Adapter extends BaseAdapter implements ListAdapter {

    public final Activity activity;
    ArrayList<SearchModel> arrayList;

    public Donors_Maps_Adapter(Activity activity, ArrayList<SearchModel> arrayList) {
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
            convertView = activity.getLayoutInflater().inflate(R.layout.search_donor_list_item, null);

        TextView Name = (TextView) convertView.findViewById(R.id.tv_name);
        Name.setTypeface(Global.setFont(activity, Global.LIGHTFONT));
        TextView Location = (TextView) convertView.findViewById(R.id.tv_address);
        Location.setTypeface(Global.setFont(activity, Global.LIGHTFONT));
        TextView textView18 = (TextView) convertView.findViewById(R.id.textView18);
        textView18.setTypeface(Global.setFont(activity, Global.LIGHTFONT));
        TextView textView19 = (TextView) convertView.findViewById(R.id.textView19);
        textView19.setTypeface(Global.setFont(activity, Global.LIGHTFONT));
        TextView Bllodgrp = (TextView) convertView.findViewById(R.id.textView20);
        Bllodgrp.setTypeface(Global.setFont(activity, Global.LIGHTFONT));
        TextView Distance = (TextView) convertView.findViewById(R.id.textView21);
        Distance.setTypeface(Global.setFont(activity, Global.LIGHTFONT));
        ImageView userPic = (ImageView) convertView.findViewById(R.id.iv_profile_pic);
        Button btn_next= (Button) convertView.findViewById(R.id.btn_next);

        final SearchModel sm = arrayList.get(position);

        try {
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(activity, IndividualDetails.class);
                    i.putExtra("USER_ID",sm.getUserId());
                    activity.startActivity(i);
                }

            });
            Name.setText(sm.getName());
            Location.setText(sm.getAddress());
            Bllodgrp.setText(sm.getBloodgroup());
            Distance.setText(sm.getDistance());
            String profilePicture = sm.getProfilePicture();
            if (!(profilePicture.equals("")) && !(profilePicture.equals(null))) {
                String url = Global.Images_Base_URL + "uploads/profilePics/" + profilePicture;
                Picasso.with(activity).load(url).transform(new CircleTransform()).error(R.mipmap.no_image).resize(200,200).into(userPic);
            }
        } catch (Exception e) {
            TastyToast.makeText(activity, "" + e, TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }

        return convertView;
    }
}
