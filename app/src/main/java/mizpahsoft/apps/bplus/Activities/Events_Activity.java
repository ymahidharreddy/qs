package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mizpahsoft.apps.bplus.Adapters.EventsAdapter;
import mizpahsoft.apps.bplus.Model.EventsModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.EndlessScrollListener;
import mizpahsoft.apps.bplus.utils.Global;

public class Events_Activity extends AppCompatActivity {

    ListView events_list;
    ProgressDialog progressDialog;
    JSONArray eventslist;
    EventsAdapter eventAdapter;
    ArrayList<EventsModel> arrayList = new ArrayList<>();
    int start = 0;
    boolean NO_POST_DATA = false;
    boolean IS_API_CALLED = false;
    View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        events_list = (ListView) findViewById(R.id.events_list);
        eventAdapter = new EventsAdapter(Events_Activity.this, arrayList);//jArray is your json array

        //Set the above adapter as the adapter of choice for our list
        events_list.setAdapter(eventAdapter);

        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);

        events_list.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (!NO_POST_DATA && !IS_API_CALLED) {
                    IS_API_CALLED = true;
                    Log.e("FOOTER", "ADDING FOOTER " + arrayList.size());
                    events_list.addFooterView(footerView);
                    GetEvents();
                }
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        progressDialog = ProgressDialog.show(Events_Activity.this, "", "please wait..");
        GetEvents();

    }

    private void GetEvents() {

        StringRequest stringrequest = new StringRequest(Request.Method.GET, Global.Events_URl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (footerView != null)
                    events_list.removeFooterView(footerView);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONArray array = jsonObject.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            EventsModel em = new EventsModel();
                            em.setEventId(object.getString("eventId"));
                            em.setEvent(object.getString("event"));
                            em.setEventDate(object.getString("eventDate"));
                            em.setEventAddress(object.getString("eventAddress"));
                            em.setEventImage(object.getString("eventImage"));
                            arrayList.add(em);
                        }
                        start = start + Integer.parseInt(jsonObject.getString("offset"));
                        eventAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("getEvents", "status=1", "got all events");
                    } else {
                        NO_POST_DATA = true;
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("getEvents", "status=0", "get into some exception");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("getEvents", "mainjsonparsingexception", "get into some exception");
                }
                IS_API_CALLED = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IS_API_CALLED = false;
                progressDialog.dismiss();
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("getEvents", "volley error listener", "get into some exception");
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("EventsActivity");
    }
}
