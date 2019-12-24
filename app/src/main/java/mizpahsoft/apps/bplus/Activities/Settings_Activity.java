package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.Global;

public class Settings_Activity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor edt;
    ProgressDialog progressDialog;
    CheckBox cb_mobilenumber, cd_pushnotifications;
    String hidmobile, notification;
    Button admin, btn_log_out, button5;
    TextView tv_blood_bank_details;
    //String AdminNum[] = {"8886502502", "8341440044"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Settings");

        tv_blood_bank_details = (TextView) findViewById(R.id.tv_blood_bank_details);
        tv_blood_bank_details.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        sp = getSharedPreferences("loginprefs", 0);
        edt = sp.edit();

        admin = (Button) findViewById(R.id.button6);
        admin.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        button5 = (Button) findViewById(R.id.button5);
        button5.setTypeface(Global.setFont(this, Global.REGULARFONT));

        cb_mobilenumber = (CheckBox) findViewById(R.id.cb_mobilenumber);
        cb_mobilenumber.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        cd_pushnotifications = (CheckBox) findViewById(R.id.cd_pushnotifications);
        cd_pushnotifications.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        if (Global.getSP(this, Global.USER_TYPE).equals("2")) {
            //hide profile for blood bank login
            button5.setVisibility(View.GONE);
            cb_mobilenumber.setVisibility(View.GONE);
            cd_pushnotifications.setVisibility(View.GONE);
            String text = "Blood Bank Name : " + Global.getSP(this, Global.BLOOD_BANK_NAME) +
                    "\n\nEstablished Year : " + Global.getSP(this, Global.EST_YEAR) + "\n\n Ph.No : " +
                    Global.getSP(this, Global.PHONE_NUMBER) + "\n\nEmail ID : " + Global.getSP(this, Global.EMAIL_ID) +
                    "\n\nWebsite : " + Global.getSP(this, Global.WEBSITE_URL) + "\n\nAddress : " + Global.getSP(this, Global.ADDRESS);
            tv_blood_bank_details.setText(text);
            tv_blood_bank_details.setVisibility(View.VISIBLE);
        }

        btn_log_out = (Button) findViewById(R.id.btn_log_out);
        btn_log_out.setTypeface(Global.setFont(this, Global.REGULARFONT));
        btn_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        String tep = sp.getString("is_phoneNumber_visible", "12");
        String not = sp.getString("noti_enable", "12");

        if (tep.equalsIgnoreCase("0")) {
            cb_mobilenumber.setChecked(false);
        } else {
            cb_mobilenumber.setChecked(true);
        }

        if (not.equalsIgnoreCase("0")) {
            cd_pushnotifications.setChecked(false);
        } else {
            cd_pushnotifications.setChecked(true);
        }


        cd_pushnotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (cd_pushnotifications.isChecked()) {
                    notification = "1";
                    updateNotificationvalue();
                    MyApplication.getInstance().trackEvent("cd_pushnotifications", "checkable", "checked");
                } else {
                    notification = "0";
                    updateNotificationvalue();
                    MyApplication.getInstance().trackEvent("cd_pushnotifications", "checkable", "unchecked");
                }
            }
        });

        cb_mobilenumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (cb_mobilenumber.isChecked()) {
                    hidmobile = "1";
                    updateMobilevalue();
                    MyApplication.getInstance().trackEvent("cb_mobilenumber", "checkable", "checked");
                } else {
                    hidmobile = "0";
                    updateMobilevalue();
                    MyApplication.getInstance().trackEvent("cb_mobilenumber", "checkable", "unchecked");
                }
            }


        });
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure, you really want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Global.clearSP(getApplicationContext());
                        Intent i = new Intent(getApplicationContext(), BeforeLoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert");
        alert.show();
    }

    // For Notification Enable or Disable
    private void updateNotificationvalue() {

        progressDialog = ProgressDialog.show(Settings_Activity.this,
                "", "Please wait!");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.Notification_Enable,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            String message = object.getString("message");
                            if (success == 1) {
                                //edt.putString("EnableNot", notification).commit();
                                TastyToast.makeText(Settings_Activity.this, "" + message, TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                progressDialog.dismiss();
                                MyApplication.getInstance().trackEvent("updateNotificationvalue", "status=1", "updation done");
                            } else {
                                TastyToast.makeText(Settings_Activity.this, "" + message, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                progressDialog.dismiss();
                                MyApplication.getInstance().trackEvent("updateNotificationvalue", "status=0", message);
                            }
                        } catch (Exception e) {
                            Toast.makeText(Settings_Activity.this, "" + e, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("updateNotificationvalue", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TastyToast.makeText(Settings_Activity.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("updateNotificationvalue", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", sp.getString("user_id", ""));
                params.put("is_enable", notification);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Settings_Activity.this);
        requestQueue.add(stringRequest);
    }


    // To Hide or Show Mobile Number
    private void updateMobilevalue() {

        progressDialog = ProgressDialog.show(Settings_Activity.this,
                "", "Please wait!");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.HideNumber,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            String message = object.getString("message");
                            if (success == 1) {
                                //edt.putString("numberhide", hidmobile).commit();
                                TastyToast.makeText(Settings_Activity.this, "" + message, TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                progressDialog.dismiss();
                                MyApplication.getInstance().trackEvent("updateMobilevalue", "status=1", "updation successful");
                            } else {
                                TastyToast.makeText(Settings_Activity.this, "" + message, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                progressDialog.dismiss();
                                MyApplication.getInstance().trackEvent("updateMobilevalue", "status=0", message);
                            }
                        } catch (Exception e) {
                            Toast.makeText(Settings_Activity.this, "" + e, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("updateMobilevalue", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TastyToast.makeText(Settings_Activity.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("updateMobilevalue", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", sp.getString("user_id", ""));
                params.put("is_phoneNumber_visible", hidmobile);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Settings_Activity.this);
        requestQueue.add(stringRequest);
    }

    public void Profile_btnClick(View view) {

        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        MyApplication.getInstance().trackEvent("Profile_btnClick", "onClick", "navigation to profile");
    }

    public void adminAccess_Click(View view) {

        startActivity(new Intent(getApplicationContext(), AdminPage.class));
        MyApplication.getInstance().trackEvent("adminAccess_Click", "onClick", "navigation to admin");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("SettingsActivity");
    }
}
