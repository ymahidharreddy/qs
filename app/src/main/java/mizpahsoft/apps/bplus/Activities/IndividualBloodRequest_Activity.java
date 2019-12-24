package mizpahsoft.apps.bplus.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.Model.NotificationsModel;
import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.CircleTransform;
import mizpahsoft.apps.bplus.utils.Global;

public class IndividualBloodRequest_Activity extends AppCompatActivity {

    TextView Name, Location, Age, BloodGrp, Gender, Mobile, Discription, DonarCount, DonorsText;
    LinearLayout ButtonsLL;
    String  b_req_id;
    ProgressDialog progressDialog;
    SharedPreferences sp;
    String notiId;
    String profilePicture;
    ImageView UserPic;
    Button btn_accept,btn_decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_blood_request);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Blood Request");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sp = getSharedPreferences("loginprefs", 0);

        btn_accept= (Button) findViewById(R.id.btn_accept);
        btn_accept.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        btn_decline= (Button) findViewById(R.id.btn_decline);
        btn_decline.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Name = (TextView) findViewById(R.id.tv_name);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Location = (TextView) findViewById(R.id.tv_location);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Age = (TextView) findViewById(R.id.tv_age);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        BloodGrp = (TextView) findViewById(R.id.tv_bloodgroup);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Gender = (TextView) findViewById(R.id.tv_gender);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Mobile = (TextView) findViewById(R.id.tv_mobile);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        Discription = (TextView) findViewById(R.id.tv_discription);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        DonarCount = (TextView) findViewById(R.id.donar_count);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        DonorsText = (TextView) findViewById(R.id.textView15);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        ButtonsLL = (LinearLayout) findViewById(R.id.ll_buttons);
        Name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        UserPic = (ImageView) findViewById(R.id.image_userpic);

        try {
            NotificationsModel nm = getIntent().getParcelableExtra("DATA");
            Name.setText(nm.getName());
            Location.setText(nm.getAddress());
            BloodGrp.setText(nm.getBloodGroup());
            Age.setText(nm.getAge());
            Gender.setText(nm.getGender());
            Mobile.setText(nm.getPhoneNumber());
            Discription.setText(nm.getMessage());
            b_req_id = nm.getB_req_id();
            profilePicture = nm.getProfilePicture();
            if (!(profilePicture.equals("")) && !(profilePicture.equals(null))) {
                String url = Global.Images_Base_URL + "uploads/profilePics/" + profilePicture;
                Picasso.with(IndividualBloodRequest_Activity.this).load(url).transform(new CircleTransform()).error(R.mipmap.no_image).into(UserPic);
            }
            notiId = nm.getNotiId();
            switch (nm.getReq_status_id()) {
                case "2":
                    DonarCount.setText(nm.getAcceptedUsers());
                    break;
                case "1":
                    DonarCount.setText("No one Accepted");
                    break;
                default:
                    ButtonsLL.setVisibility(View.GONE);
                    DonorsText.setVisibility(View.GONE);
                    DonarCount.setText("Request Completed by : " + nm.getDonor());
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            MyApplication.getInstance().trackException(e);
            MyApplication.getInstance().trackEvent("try", "onCreate", "get into some exception");
        }
    }

    public void Accept_Click(View view) {
        ShowDialog();
    }


    public void Decline_Click(View view) {

        finish();
    }

    private void AcceptReq() {

        StringRequest stringrequest = new StringRequest(Request.Method.POST, Global.Accept_Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int status = jsonObject.getInt("status");
                    String message = jsonObject.getString("message");
                    if (status == 1) {
                        try {
                            Toast.makeText(IndividualBloodRequest_Activity.this, message, Toast.LENGTH_LONG).show();
                            setResult(Global.ACTIVITY_FOR_RESULT);
                            finish();
                            MyApplication.getInstance().trackEvent("AcceptReq", "status=1", "req success");
                        } catch (Exception e) {
                            Toast.makeText(IndividualBloodRequest_Activity.this, e.toString(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("AcceptReq", "status=1", "get into some exception");
                        }
                    } else {
                        Toast.makeText(IndividualBloodRequest_Activity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackEvent("AcceptReq", "status=0", jsonObject.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("AcceptReq", "mainjsonparsingexception", "get into some exception");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(IndividualBloodRequest_Activity.this, "" + error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                MyApplication.getInstance().trackException(error);
                MyApplication.getInstance().trackEvent("AcceptReq", "volley error listener", "get into some exception");
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", sp.getString("user_id", ""));
                params.put("requestId", b_req_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringrequest);
    }


    private void ShowDialog() {

        // Create custom dialog object
        final Dialog dialog = new Dialog(IndividualBloodRequest_Activity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.rooles_blood);
        // Set dialog title
        dialog.setTitle("");
        dialog.show();

        Button declineButton = (Button) dialog.findViewById(R.id.declineButton);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(IndividualBloodRequest_Activity.this, "", "Please wait..");
                AcceptReq();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("IndividualBloodRequestActivity");
    }
}
