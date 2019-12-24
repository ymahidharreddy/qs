package mizpahsoft.apps.bplus.Adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.R;

/**
 * Created by Mizpah on 11/10/2016.
 */

public class TipsAdapter extends BaseAdapter implements ListAdapter {

    public final Activity activity;
    private ArrayList<String> arrayList;

    public TipsAdapter(Activity activity, ArrayList<String> arrayList) {
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
            convertView = activity.getLayoutInflater().inflate(R.layout.tips_row, null);


        TextView tip = (TextView) convertView.findViewById(R.id.tips);
        tip.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        tip.setText(arrayList.get(position));

        return convertView;
    }
}

