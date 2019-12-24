package mizpahsoft.apps.bplus.Adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.Model.BloodBanksModel;
import mizpahsoft.apps.bplus.R;

/**
 * Created by Mizpah on 11/10/2016.
 */

public class BloodBanksAdapter extends BaseAdapter implements ListAdapter {

    public final Activity activity;
    ArrayList<BloodBanksModel> arrayList;


    public BloodBanksAdapter(Activity activity, ArrayList<BloodBanksModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
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
            convertView = activity.getLayoutInflater().inflate(R.layout.banks_row, null);


        TextView Name = (TextView) convertView.findViewById(R.id.tv_name);
        Name.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView Location = (TextView) convertView.findViewById(R.id.tv_location);
        Location.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView PhnNumber = (TextView) convertView.findViewById(R.id.bank_phn);
        PhnNumber.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView Email = (TextView) convertView.findViewById(R.id.tv_email);
        Email.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView site = (TextView) convertView.findViewById(R.id.tv_site);
        site.setTypeface(Global.setFont(activity,Global.LIGHTFONT));

        BloodBanksModel bb = arrayList.get(position);

        Name.setText(bb.getB_bankName());
        Location.setText(bb.getAddress());
        Email.setText(bb.getB_bank_emailId());
        site.setText(bb.getB_bank_websitUrl());
        PhnNumber.setText("Ph: " + bb.getB_bank_phoneNo());

        return convertView;
    }
}

