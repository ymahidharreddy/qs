package mizpahsoft.apps.bplus.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;

public class BeforeLoginActivity extends AppCompatActivity {
    TabLayout tab_layout;
    ViewPager view_pager;
    Button bt_log_in;
    Button bt_register;
    ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_login);

        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        tab_layout.setupWithViewPager(view_pager, true);
        bt_log_in = (Button) findViewById(R.id.bt_log_in);
        bt_log_in.setTypeface(Global.setFont(this, Global.REGULARFONT));

        bt_register = (Button) findViewById(R.id.bt_register);
        bt_register.setTypeface(Global.setFont(this, Global.REGULARFONT));

        populateData();

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(arrayList);
        view_pager.setAdapter(adapter);
        view_pager.setOnPageChangeListener(createOnPageChangeListener());

        bt_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BeforeLoginActivity.this, SignInActivity.class);
                startActivity(i);
                finish();
            }
        });
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BeforeLoginActivity.this, LoginAndRegistrationActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private ViewPager.OnPageChangeListener createOnPageChangeListener() {

        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        };
    }

    public void populateData() {
        for (int i = 0; i < 3; i++) {
            arrayList.add("text" + i);
        }
    }

    // Adapter for multi images
    public class MyViewPagerAdapter extends PagerAdapter {
        ArrayList<String> arrayList = new ArrayList<>();
        //ArrayList<Integer> arrayList_int = new ArrayList<>();
        // int screen_width;

        public MyViewPagerAdapter(ArrayList<String> arrayList) {
            this.arrayList = arrayList;
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            if (view == null) {
                try {
                    LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = layoutInflater.inflate(R.layout.view_pager_landing_page, container, false);
                    ImageView imageView = (ImageView) view.findViewById(R.id.image);
                    TextView tv_text = (TextView) view.findViewById(R.id.tv_text);
                    tv_text.setTypeface(Global.setFont(getApplicationContext(), Global.LIGHTFONT));
                    switch (position) {
                        case 0:
                            tv_text.setText(getString(R.string.before_log_in_text1));
                            imageView.setImageResource(R.drawable.registration_slide);
                            break;
                        case 1:
                            tv_text.setText(getString(R.string.before_log_in_text2));
                            imageView.setImageResource(R.drawable.registration_image_2);
                            break;
                        case 2:
                            tv_text.setText(getString(R.string.before_log_in_text3));
                            imageView.setImageResource(R.drawable.restration_image_3);
                            break;
                    }
                    ((ViewPager) container).addView(view);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                }
            }
            return view;

        }

        // Banner size
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            ((ViewPager) container).removeView(view);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("Before Login screen");
    }
}