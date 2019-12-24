package mizpahsoft.apps.bplus.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.MobileAds;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import mizpahsoft.apps.bplus.Adapters.GridviewAdapter;
import mizpahsoft.apps.bplus.BuildConfig;
import mizpahsoft.apps.bplus.Model.NotificationsModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;

public class MainActivity extends AppCompatActivity {
    GridView gridview;
    ImageView notifications;
    ImageView Search;
    ArrayList<String> videoAdsArrayList = new ArrayList<>();
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    TextView Notificationcount;
    VideoView vv_for_video;
    ProgressBar progressBar;
    int count = 0;

    public static String[] prgmNameList = {"Search Donor", "My Requests", "Notifications", "Events", "Blood Banks",
            "Tips", "Spread News", "Testimonials", "Settings"};
    public static String[] prgmNameList_blood_bank = {"Add Donor", "Save BloodUnit", "BloodUnit Status", "Events", "Blood Banks",
            "Tips", "Spread News", "Testimonials", "Settings"};

    public static int[] prgmImages = {R.mipmap.search_donor, R.mipmap.myrequest, R.mipmap.notifications,
            R.mipmap.events, R.mipmap.bloodbanks, R.mipmap.tips, R.mipmap.spreadnews, R.mipmap.testimonials,
            R.mipmap.settings};
    String Ncount;
    RelativeLayout videoads, viewFlipLayout;
    ViewPager viewPager;
    MyViewPagerAdapter myViewPagerAdapter;
    LinearLayout dotsLayout;
    int currentPage = 0, dotsCount;
    private TextView[] dots;
    ArrayList<String> BannersList = new ArrayList<>();
    ArrayList<NotificationsModel> arrayList = new ArrayList<>();
    int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().hasExtra("isUserProfileUpdated")) {
            if (getIntent().getStringExtra("isUserProfileUpdated").equals("0")) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.putExtra("FROM_MAINACTIVITY", "MUST_UPDATE_PROFILE");
                startActivity(i);
            }
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customView = getLayoutInflater().inflate(R.layout.actionbar_layout_profile, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);

        MobileAds.initialize(getApplicationContext(),"ca-app-pub-7092499695797748~6475222368");

        //getSupportActionBar().setCustomView(R.layout.actionbar_layout_profile);
        notifications = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.btn_profile_save);
        Search = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.btn_search);
        Notificationcount = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.tv_count);

        TextView title = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.textView62);
        title.setTypeface(Global.setFont(this, Global.REGULARFONT));


        sp = getSharedPreferences("loginprefs", 0);
        edt = sp.edit();


        videoads = (RelativeLayout) findViewById(R.id.videoads);
        viewFlipLayout = (RelativeLayout) findViewById(R.id.viewFlipLayout);

        vv_for_video = (VideoView) findViewById(R.id.vv_for_video);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        vv_for_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                vv_for_video.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                vv_for_video.start();
            }
        });
        vv_for_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (count < (videoAdsArrayList.size() - 1)) {
                    count = count + 1;
                } else if (count >= (videoAdsArrayList.size() - 1)) {
                    count = 0;
                }
                Uri video = Uri.parse(Global.VIDEO_ADS_PATH + videoAdsArrayList.get(count));
                vv_for_video.setVideoURI(video);
            }
        });

        gridview = (GridView) findViewById(R.id.gridItems);

        if (Global.getSP(this, Global.USER_TYPE).equals("1")) {
            gridview.setAdapter(new GridviewAdapter(this, prgmNameList, prgmImages));
        } else {
            gridview.setAdapter(new GridviewAdapter(this, prgmNameList_blood_bank, prgmImages));
        }

        if (Global.isInternetPresent(this)) {
            GetBanners();
        } else {
            noInternetDialog();
        }

        if (Global.getSP(this, Global.USER_TYPE).equals("2")) {
            //hide notification icon for blood bank login
            notifications.setVisibility(View.GONE);
            Notificationcount.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Search.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            // params.addRule(RelativeLayout.LEFT_OF, R.id.id_to_be_left_of);
            Search.setLayoutParams(params);
            // Search.setLayoutParams(lp);

        }
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, UnReadNotications_Activity.class);
                i.putParcelableArrayListExtra("DATA", arrayList);
                i.putExtra("start", start);
                startActivity(i);
                MyApplication.getInstance().trackEvent("notifications", "onClick", "navigation to unread notifications activity");
            }
        });
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchDonaorByBloodGrp_Activity.class);
                startActivity(i);
                MyApplication.getInstance().trackEvent("Search", "onClick", "navigation to SearchDonaorByBloodGrp_Activity activity");
            }
        });

        getAppSettings();

        if (getIntent().hasExtra("NOTIFICATION")) {
            Intent intent = new Intent(getApplicationContext(), Notications_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    // to get notifications count
    private void GetNotificationsCount() {
        //edt.putString("UnreadNotifications", "NoData").commit();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.NotificationsCount_URL + "?userId=" + sp.getString("user_id", "") + "&start=0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        arrayList.clear();
                        try {
                            JSONArray BannerOJ = new JSONArray(response);
                            JSONObject jb = BannerOJ.getJSONObject(0);
                            JSONObject jb12 = jb.getJSONObject("userDetails");
                            String roleid = jb12.getString("roleId");
                            edt.putString("roleid", roleid).commit();
                            JSONObject jb1 = jb.getJSONObject("userDetails");
                            edt.putString("is_phoneNumber_visible", jb1.getString("is_phoneNumber_visible")).commit();
                            edt.putString("noti_enable", jb1.getString("noti_enable")).commit();
                            if (jb.getInt("status") == 1) {
                                Ncount = jb.getString("count");
                                JSONArray UnreadNotifications = jb.getJSONArray("result");
                                //edt.putString("UnreadNotifications", UnreadNotifications.toString()).commit();
                                Notificationcount.setText(Ncount);
                                Notificationcount.setVisibility(View.VISIBLE);
                                if (Ncount.equals("")) {
                                    Notificationcount.setVisibility(View.INVISIBLE);
                                } else if (Ncount.equals("0")) {
                                    Notificationcount.setVisibility(View.INVISIBLE);
                                }
                                JSONArray array = jb.getJSONArray("result");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    NotificationsModel nm = new NotificationsModel();
                                    nm.setB_req_id(object.getString("b_req_id"));
                                    nm.setNotiId(object.getString("notiId"));
                                    nm.setNotification(object.getString("notification"));
                                    nm.setName(object.getString("name"));
                                    nm.setAddress(object.getString("address"));
                                    nm.setAge(object.getString("age"));
                                    nm.setProfilePicture(object.getString("profilePicture"));
                                    nm.setNotiCreatedTime(object.getString("notiCreatedTime"));
                                    nm.setBloodGroup(object.getString("bloodGroup"));
                                    nm.setReq_status_id(object.getString("req_status_id"));
                                    nm.setDonor(object.getString("donor"));
                                    nm.setMessage(object.getString("message"));
                                    nm.setGender(object.getString("gender"));
                                    nm.setPhoneNumber(object.getString("phoneNumber"));
                                    nm.setAcceptedUsers("" + object.getJSONArray("acceptedUsers").length());
                                    arrayList.add(nm);
                                }
                                start = Integer.parseInt(jb.getString("offset"));
                            } else {
                                Notificationcount.setText("");
                                Notificationcount.setVisibility(View.INVISIBLE);
                            }
                            MyApplication.getInstance().trackEvent("GetNotificationsCount", "status=1", "got all new notifications");
                        } catch (Exception e) {
                            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            Notificationcount.setVisibility(View.INVISIBLE);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("GetNotificationsCount", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("GetNotificationsCount", "volley error listener", "get into some exception");
                        //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void GetBanners() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.Banner_Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray BannerOJ = new JSONArray(response);
                            JSONObject jb = BannerOJ.getJSONObject(0);
                            if (jb.getInt("status") == 1) {
                                JSONArray banners = jb.getJSONArray("banners");
                                for (int i = 0; i < banners.length(); i++) {
                                    JSONObject BOJ = banners.getJSONObject(i);
                                    String banner_name = BOJ.getString("image");
                                    BannersList.add(banner_name);
                                }

                                JSONArray videos = jb.getJSONArray("videos");
                                for (int i = 0; i < videos.length(); i++) {
                                    JSONObject BOJ = videos.getJSONObject(i);
                                    String banner_name = BOJ.getString("video").replace(" ", "%20");
                                    videoAdsArrayList.add(banner_name);
                                }

                                if (jb.getInt("isVedio") == 1) {
                                    videoads.setVisibility(View.VISIBLE);
                                    if (videoAdsArrayList.size() > 0) {
                                        Uri video = Uri.parse(Global.VIDEO_ADS_PATH + videoAdsArrayList.get(count));
                                        vv_for_video.setVideoURI(video);
                                    }
                                } else if (jb.getInt("isVedio") == 0) {
                                    viewFlipLayout.setVisibility(View.VISIBLE);
                                    setViewPagerItemsWithAdapter(BannersList.size());
                                    setUiPageViewController(BannersList.size());
                                }
                                MyApplication.getInstance().trackEvent("GetBanners", "status=1", "got all banners");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("GetBanners", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("GetBanners", "volley error listener", "get into some exception");
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void setViewPagerItemsWithAdapter(int a) {
        final int count1 = a;
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(viewPagerPageChangeListener);
        final Handler viewPagerhandler = new Handler();

        final Runnable Update = new Runnable() {
            public void run() {

                if (currentPage == count1) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                viewPagerhandler.post(Update);
            }
        }, 2500, 4000);

    }

    private void setUiPageViewController(int b) {

        dotsCount = b;
        dots = new TextView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new TextView(MainActivity.this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(30);
            dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
            dotsLayout.addView(dots[i]);
        }

        dots[0].setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    // Adapter for Banners
    public class MyViewPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = null;
            if (view == null) {
                try {
                    LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = layoutInflater.inflate(R.layout.view_pager_item, container, false);
                    ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);


                    String pic_Append_url = "/uploads/banners/" + BannersList.get(position);

                    Picasso.with(getApplicationContext()).load(Global.Images_Base_URL + "/" + pic_Append_url)
                            .into(imageView);

                    ((ViewPager) container).addView(view);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            return view;

        }

        // Banner size
        @Override
        public int getCount() {
            return BannersList.size();
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

    // page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            try {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));

                }
                dots[position].setTextColor(getResources().getColor(R.color.colorPrimary));
                //dots[position].setTextSize(50);
            } catch (Exception e) {
                // TODO: handle exception
                String elog = e.toString();
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            //dots[arg0].setTextSize(30);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            //dots[arg0].setTextSize(30);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Ncount = "0";
        if (Global.getSP(this, Global.USER_TYPE).equals("1")) {
            //api call for only donor login
            //not needed for blood bank login
            GetNotificationsCount();
        }
        MyApplication.getInstance().trackScreenView("MainActivity");
    }

    public void alertDialog(String isServerInMaintenance, String reason) {
        final Dialog alertDialog = new Dialog(this, android.R.style.Theme_Light);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.alert_layout);
        alertDialog.setCancelable(false);

        ImageView iv_image = (ImageView) alertDialog.findViewById(R.id.iv_image);
        TextView tv_reason = (TextView) alertDialog.findViewById(R.id.tv_reason);
        tv_reason.setTypeface(Global.setFont(this, Global.REGULARFONT));
        Button btn_click = (Button) alertDialog.findViewById(R.id.btn_click);
        btn_click.setTypeface(Global.setFont(this, Global.REGULARFONT));
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        tv_reason.setText(reason);
        if (isServerInMaintenance.equals("1")) {
            btn_click.setVisibility(View.GONE);
            iv_image.setBackgroundResource(R.drawable.maintanence);
        } else {
            iv_image.setBackgroundResource(R.drawable.update_alert);
        }
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        alertDialog.show();
    }

    private void getAppSettings() {
        /*
        * this api is used ot get info about server maintanance
        * and to show update for the users
        * */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.APP_SETTINGS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    JSONObject object = array.getJSONObject(0);

                    if (object.getInt("status") == 1) {
                        JSONObject result = object.getJSONObject("result");
                        //this is key to show ads or not
                        // if isAdsShow=0 dont show ads
                        //if isAdsShow=1 show ads
                        Global.saveSP(getApplicationContext(), Global.IS_ADS_SHOW, result.getString("isAdsShow"));
                        if (result.getString("isServerInMaintenance").equals("1")) {
                            alertDialog(result.getString("isServerInMaintenance"), result.getString("reason"));
                        } else if (BuildConfig.VERSION_CODE < Integer.parseInt(result.getString("andriodAppVersion"))) {
                            //andriodAppVersion is the minimum android version of app
                            //that is allowed to run
                            alertDialog(result.getString("isServerInMaintenance"), getString(R.string.update_version));

                        }
                    } else {
                        TastyToast.makeText(MainActivity.this, "" + object.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    TastyToast.makeText(MainActivity.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("SaveBloodUnit mobile_by_rfid", "mainjsonparsingexception" + e, "get into some exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TastyToast.makeText(LoginAndRegistrationActivity.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("SaveBloodUnit", "volley error listener", "get into some exception");
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void noInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.no_internet))
                .setCancelable(false)
                .setPositiveButton("Reload", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert");
        alert.show();
    }
}
