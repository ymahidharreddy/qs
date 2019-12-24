package mizpahsoft.apps.bplus.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.Adapters.Donors_Maps_Adapter;
import mizpahsoft.apps.bplus.Model.NotificationsModel;
import mizpahsoft.apps.bplus.Model.SearchModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.EndlessScrollListener;
import mizpahsoft.apps.bplus.utils.Global;

public class MapsActivity extends AppCompatActivity {

    ListView Donors_list;
    Donors_Maps_Adapter jSONAdapter;
    public static ArrayList<String> ar_md;
    ArrayList<SearchModel> arrayList = new ArrayList<>();
    int start = 0;

    boolean NO_POST_DATA = false;
    boolean IS_API_CALLED = false;
    View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Donor List");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Donors_list = (ListView) findViewById(R.id.lv_donos);
        Donors_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userid = ar_md.get(position);
                Model.useridtopass = userid;
                Intent i = new Intent(MapsActivity.this, IndividualDetails.class);
                startActivity(i);
            }
        });
        arrayList = getIntent().getParcelableArrayListExtra("DATA");
        start = Integer.parseInt(getIntent().getStringExtra("start"));
        jSONAdapter = new Donors_Maps_Adapter(MapsActivity.this, arrayList);//jArray is your json array
        //Set the above adapter as the adapter of choice for our list
        Donors_list.setAdapter(jSONAdapter);


        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);

        Donors_list.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!NO_POST_DATA && !IS_API_CALLED) {
                    IS_API_CALLED = true;
                    Log.e("FOOTER", "ADDING FOOTER " + arrayList.size());
                    Donors_list.addFooterView(footerView);
                    getSearchDonors();
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        //getSearchDonors();
    }


    private void getSearchDonors() {
        String URL = Global.API_SEARCH_DONORS_URL + getIntent().getStringExtra("BLOOD_GROUP") + "&address="
                + getIntent().getStringExtra("ADDRESS") + "&userId=" + getSharedPreferences("loginprefs", 0).getString("user_id", "") +
                "&start=" + start;
        URL = URL.replace(" ", "%20");
        StringRequest stringrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("res", response);
                if (footerView != null) {
                    Donors_list.removeFooterView(footerView);
                }
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object1 = array.getJSONObject(i);
                            SearchModel sm = new SearchModel();
                            sm.setUserId(object1.getString("userId"));
                            sm.setName(object1.getString("name"));
                            sm.setAddress(object1.getString("address"));
                            sm.setProfilePicture(object1.getString("profilePicture"));
                            sm.setBloodgroup(object1.getString("bloodGroupName"));
                            sm.setDistance(object1.getString("distance"));
                            arrayList.add(sm);
                        }
                        start = start + Integer.parseInt(jsonObject.getString("offset"));
                        jSONAdapter.notifyDataSetChanged();
                        MyApplication.getInstance().trackEvent("getSearchDonors", "status=1", "got all donor");
                    } else {
                        NO_POST_DATA = true;
                        MyApplication.getInstance().trackEvent("getSearchDonors", "status=0", "no records");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("getSearchDonors", "mainjsonparsingexception", "get into some exception");
                }
                IS_API_CALLED = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IS_API_CALLED = false;
                if (footerView != null) {
                    Donors_list.removeFooterView(footerView);
                }
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("GetNotifications", "volley error listener", "get into some exception");
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("NotificationsActivity");
    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {

            Bundle b = getIntent().getExtras();
            donors = new JSONArray(b.getString("donorsArray"));
            Lat = b.getString("Lat");
            Lon= b.getString("Lon");
            ar_md = new ArrayList<String>();
            for (int i = 0; i < donors.length(); i++) {

                JSONObject mn = donors.getJSONObject(i);

                String Name = mn.getString("name");
                String userid=mn.getString("userId");

                ar_md.add(userid);
                double lat = Double.parseDouble(mn.getString("lat"));
                double lon = Double.parseDouble(mn.getString("lon"));
                LatLng TutorialsPoint = new LatLng(lat, lon);

                googleMap.addMarker(new
                        MarkerOptions().position(TutorialsPoint).title("Donor :" + Name));
            }
            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(Float.parseFloat(Lat),Float.parseFloat(Lon)));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);

            jSONAdapter = new Donors_Maps_Adapter(MapsActivity.this, donors);//jArray is your json array
            //Set the above adapter as the adapter of choice for our list
            Donors_list.setAdapter(jSONAdapter);


        } catch (Exception e) {
           // Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
        }
    }*/
}
