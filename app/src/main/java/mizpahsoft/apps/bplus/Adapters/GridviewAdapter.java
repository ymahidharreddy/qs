package mizpahsoft.apps.bplus.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mizpahsoft.apps.bplus.Activities.AddDonor;
import mizpahsoft.apps.bplus.Activities.BloodBanks_Activity;
import mizpahsoft.apps.bplus.Activities.BloodUnitStatus;
import mizpahsoft.apps.bplus.Activities.EventRelatedClass;
import mizpahsoft.apps.bplus.Activities.SaveBloodUnitActivity;
import mizpahsoft.apps.bplus.Activities.SearchDonorActivity;
import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.Activities.MainActivity;
import mizpahsoft.apps.bplus.Activities.Notications_Activity;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.Activities.RecentBloodRequest_Activity;
import mizpahsoft.apps.bplus.Activities.Settings_Activity;
import mizpahsoft.apps.bplus.Activities.SuggestSaviour;
import mizpahsoft.apps.bplus.Activities.Testimonials_Activity;
import mizpahsoft.apps.bplus.Activities.Tips_Activity;

/**
 * Created by Mizpah on 11/5/2016.
 */

public class GridviewAdapter extends BaseAdapter {

    String[] result;
    Context context;
    int[] imageId;
    private static LayoutInflater inflater = null;

    public GridviewAdapter(MainActivity mainActivity, String[] prgmNameList, int[] prgmImages) {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        context = mainActivity;
        imageId = prgmImages;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.grid_row, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textView1);
        holder.tv.setTypeface(Global.setFont(context, Global.LIGHTFONT));
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);


        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            /*typeOfLogin   =1  For normal user
            typeOfLogin   =2  For blood bank*/
                if (position == 0) {
                    if(Global.getSP(context,Global.USER_TYPE).equals("1")){
                        Intent i = new Intent(context, SearchDonorActivity.class);
                        context.startActivity(i);
                    }
                    else{
                        Intent i = new Intent(context, AddDonor.class);
                        context.startActivity(i);
                    }

                } else if (position == 1) {
                    if(Global.getSP(context,Global.USER_TYPE).equals("1")){
                        Intent i = new Intent(context, RecentBloodRequest_Activity.class);
                        context.startActivity(i);
                    }
                    else{
                        Intent i = new Intent(context, SaveBloodUnitActivity.class);
                        context.startActivity(i);
                    }

                } else if (position == 2) {
                    if(Global.getSP(context,Global.USER_TYPE).equals("1")){
                        Intent i = new Intent(context, Notications_Activity.class);
                        context.startActivity(i);
                    }
                    else{
                        Intent i = new Intent(context, BloodUnitStatus.class);
                        context.startActivity(i);
                    }


                } else if (position == 3) {
                    Intent i = new Intent(context, EventRelatedClass.class);
                    context.startActivity(i);

                } else if (position == 4) {
                    Intent i = new Intent(context, BloodBanks_Activity.class);
                    context.startActivity(i);

                } else if (position == 5) {
                    Intent i = new Intent(context, Tips_Activity.class);
                    context.startActivity(i);

                } else if (position == 6) {
                    Intent i = new Intent(context, SuggestSaviour.class);
                    context.startActivity(i);


                } else if (position == 7) {
                    Intent i = new Intent(context, Testimonials_Activity.class);
                    context.startActivity(i);

                } else if (position == 8) {

                    Intent i = new Intent(context, Settings_Activity.class);
                    context.startActivity(i);
                }
            }
        });

        return rowView;
    }
}