package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.Model.NotificationsModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;

public class Individual_Myrequest_Activity extends AppCompatActivity {

    TextView Name, Location, Age, BloodGrp, Gender, Mobile, Discription, DonarCount, DonorsText;
    Button ButtonsLL;
    String b_req_id, testimonial;
    ProgressDialog progressDialog;
    SharedPreferences sp;
    NumberPicker np;
    ArrayList<String> acceptedUsersArrayList;
    ArrayList<String> acceptedUserIdArrayList;
    String[] values;
    EditText Testimonial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual__myrequest);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Request Details");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sp = getSharedPreferences("loginprefs", 0);
        acceptedUsersArrayList = new ArrayList<>();
        acceptedUserIdArrayList = new ArrayList<>();

        Name = (TextView) findViewById(R.id.tv_name);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Location = (TextView) findViewById(R.id.tv_location);
        Location.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Age = (TextView) findViewById(R.id.tv_age);
        Age.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        BloodGrp = (TextView) findViewById(R.id.tv_bloodgroup);
        BloodGrp.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Gender = (TextView) findViewById(R.id.tv_gender);
        Gender.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Testimonial = (EditText) findViewById(R.id.et_optinion);
        Testimonial.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Discription = (TextView) findViewById(R.id.tv_discription);
        Discription.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        DonarCount = (TextView) findViewById(R.id.donar_count);
        DonarCount.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        DonorsText = (TextView) findViewById(R.id.textView15);
        DonorsText.setTypeface(Global.setFont(this,Global.REGULARFONT));

        ButtonsLL = (Button) findViewById(R.id.ButtonsLL);
        ButtonsLL.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        np = (NumberPicker) findViewById(R.id.numberPicker1);

        try {
            NotificationsModel nm = getIntent().getParcelableExtra("DATA");

            Name.setText(nm.getName());
            Location.setText(nm.getAddress());
            BloodGrp.setText(nm.getBloodGroup());
            Age.setText(nm.getAge());
            Gender.setText(nm.getGender());
            Discription.setText(nm.getMessage());
            b_req_id = nm.getB_req_id();

            switch (nm.getReq_status_id()) {
                case "2":
                    JSONArray acceptedUserArray = new JSONArray(nm.getAcceptedUsers());
                    DonarCount.setVisibility(View.GONE);
                    if (acceptedUserArray.length() != 0) {
                        for (int i = 0; i < acceptedUserArray.length(); i++) {
                            JSONObject au = acceptedUserArray.getJSONObject(i);
                            acceptedUsersArrayList.add(au.getString("name"));
                            acceptedUserIdArrayList.add(au.getString("userId"));
                        }
                        values = new String[acceptedUsersArrayList.size()];
                        values = acceptedUsersArrayList.toArray(values);

                        np.setMinValue(0);
                        np.setMaxValue(values.length - 1);
                        np.setDisplayedValues(values);
                        np.setWrapSelectorWheel(true);

                    }
                    break;
                case "1":
                    DonarCount.setText("No one accepted your request");
                    np.setVisibility(View.GONE);
                    ButtonsLL.setVisibility(View.GONE);
                    DonorsText.setVisibility(View.GONE);
                    Testimonial.setVisibility(View.GONE);
                    break;
                default:
                    ButtonsLL.setVisibility(View.GONE);
                    DonorsText.setVisibility(View.GONE);
                    DonarCount.setText("Request Completed by : " + nm.getDonor());
                    np.setVisibility(View.GONE);
                    Testimonial.setVisibility(View.GONE);
                    break;
            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            MyApplication.getInstance().trackException(e);
            MyApplication.getInstance().trackEvent("try", "onCreate()", "get into some exception");
        }
    }

    public void GotBlood_Click(View view) {
        String ReqUId = acceptedUserIdArrayList.get(np.getValue());
        testimonial = StringEscapeUtils.escapeJava(Testimonial.getText().toString().trim());
        SendRequest(b_req_id, ReqUId);
    }

    private void SendRequest(final String Req_id, final String Req_UserId) {
        progressDialog = ProgressDialog.show(Individual_Myrequest_Activity.this, "", "Please wait..");
        StringRequest stringrequest = new StringRequest(Request.Method.POST, Global.UpdateDonar_UrL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");

                    if (status == 1) {
                        try {

                            progressDialog.dismiss();
                            startActivity(new Intent(Individual_Myrequest_Activity.this, RecentBloodRequest_Activity.class));
                            finish();
                            Toast.makeText(Individual_Myrequest_Activity.this, "Request Accepted", Toast.LENGTH_LONG).show();
                            MyApplication.getInstance().trackEvent("SendRequest", "status=1", "Request Accepted");
                        } catch (Exception e) {
                            Toast.makeText(Individual_Myrequest_Activity.this, e.toString(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("SendRequest", "status=1", "get into some exception");
                        }
                    } else {
                        Toast.makeText(Individual_Myrequest_Activity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("SendRequest", "status=0", jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("SendRequest", "mainjsonparsingexception", "get into some exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Individual_Myrequest_Activity.this, "" + error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("SendRequest", "volley error listener", "get into some exception");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", sp.getString("user_id", ""));
                params.put("requestId", Req_id);
                params.put("donorId", String.valueOf(Req_UserId));
                params.put("messege", testimonial);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Individual_Myrequest_Activity.this);
        requestQueue.add(stringrequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("IndividualMyrequestActivity");
    }
}
