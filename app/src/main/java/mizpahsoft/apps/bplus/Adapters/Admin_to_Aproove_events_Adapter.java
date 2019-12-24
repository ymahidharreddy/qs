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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import mizpahsoft.apps.bplus.utils.CircleTransform;
import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.Activities.Individual_PendingEvent_Activity;
import mizpahsoft.apps.bplus.R;

/**
 * Created by Mizpah on 1/12/2017.
 */

public class Admin_to_Aproove_events_Adapter extends BaseAdapter implements ListAdapter {

    public final Activity activity;
    public final JSONArray jsonArray;

    public Admin_to_Aproove_events_Adapter(Activity activity, JSONArray jsonArray) {
        assert activity != null;
        assert jsonArray != null;

        this.jsonArray = jsonArray;
        this.activity = activity;
    }


    @Override
    public int getCount() {
        if (null == jsonArray)
            return 0;
        else
            return jsonArray.length();
    }

    @Override
    public JSONObject getItem(int position) {
        if (null == jsonArray) return null;
        else
            return jsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        JSONObject jsonObject = getItem(position);

        return jsonObject.optLong("id");
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.pendingevents_row, null);


        final TextView Name = (TextView) convertView.findViewById(R.id.tv_name);
        Name.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView Location = (TextView) convertView.findViewById(R.id.tv_location);
        Location.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView Time = (TextView) convertView.findViewById(R.id.tv_time);
        Time.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        ImageView iv_image = (ImageView) convertView.findViewById(R.id.list_image);
        LinearLayout iv_Next = (LinearLayout) convertView.findViewById(R.id.ll_layout);
        JSONObject json_data = getItem(position);

        if (null != json_data) {
            try {

                Name.setText(json_data.getString("event"));
                Name.setTag(json_data.getString("eventId"));
                Time.setText(json_data.getString("eventDate"));
                Location.setText(json_data.getString("eventAddress"));
                String img = json_data.getString("eventImage");
                if (!(img.equals("")) && !(img.equals(null))) {
                    String url = Global.Events_image + img;
                    Picasso.with(activity).load(url).transform(new CircleTransform()).error(R.mipmap.no_image).into(iv_image);
                }
            } catch (Exception e) {
                Toast.makeText(activity, "Raised adapter" + e, Toast.LENGTH_SHORT).show();
            }

            iv_Next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(activity.getApplicationContext(), Individual_PendingEvent_Activity.class);
                    i.putExtra("EvntId",Name.getTag().toString());
                    activity.startActivity(i);
                }
            });
        }
        return convertView;
    }


}
