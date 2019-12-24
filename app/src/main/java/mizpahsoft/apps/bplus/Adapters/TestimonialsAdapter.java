package mizpahsoft.apps.bplus.Adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.utils.CircleTransform;
import mizpahsoft.apps.bplus.utils.Global;
import mizpahsoft.apps.bplus.Model.TestimonialsModel;
import mizpahsoft.apps.bplus.R;

/**
 * Created by Mizpah on 11/10/2016.
 */

public class TestimonialsAdapter extends BaseAdapter implements ListAdapter {

    public final Activity activity;
    String profilePicture;
    ImageView UserPic;
    ArrayList<TestimonialsModel> arrayList = new ArrayList<>();

    public TestimonialsAdapter(Activity activity, ArrayList<TestimonialsModel> arrayList) {
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
            convertView = activity.getLayoutInflater().inflate(R.layout.testimonials_row, null);


        TextView Name = (TextView) convertView.findViewById(R.id.tv_name);
        Name.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        TextView Messages = (TextView) convertView.findViewById(R.id.tv_message);
        Messages.setTypeface(Global.setFont(activity,Global.LIGHTFONT));
        UserPic = (ImageView) convertView.findViewById(R.id.image_userpic);

        TestimonialsModel tt = arrayList.get(position);

        Name.setText(tt.getName());
        Messages.setText(StringEscapeUtils.unescapeJava(tt.getMessage()));
        profilePicture = tt.getProfilePicture();
        if (!(profilePicture.equals("")) && !(profilePicture.equals(null))) {
            String url = Global.Images_Base_URL + "uploads/profilePics/" + profilePicture;
            Picasso.with(activity).load(url).transform(new CircleTransform()).error(R.mipmap.no_image).resize(150,150).into(UserPic);
        }
        return convertView;
    }
}


