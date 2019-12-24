package mizpahsoft.apps.bplus.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.CircleTransform;
import mizpahsoft.apps.bplus.utils.Global;

/***
 * Created by Mizpah-DEV on 11-Nov-16.
 */

public class IndividualDetails extends AppCompatActivity {
    ImageView iv_propic;
    TextView tv_name, tv_bgroup, tv_mobile, tv_gender, tv_address;
    //String userId = "";
    Bitmap bitmap;
    ProgressDialog progressDialog;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    Button button3,button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_donor);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("Donor Details");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sp = getSharedPreferences("loginprefs", 0);
        edt = sp.edit();

        button3= (Button) findViewById(R.id.button3);
        button3.setTypeface(Global.setFont(this,Global.REGULARFONT));

        button4= (Button) findViewById(R.id.button4);
        button4.setTypeface(Global.setFont(this,Global.REGULARFONT));

        iv_propic = (ImageView) findViewById(R.id.iv_propic);


        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_bgroup = (TextView) findViewById(R.id.tv_bloodgroup);
        tv_bgroup.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_mobile.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_gender.setTypeface(Global.setFont(this,Global.LIGHTFONT));

        tv_address = (TextView) findViewById(R.id.tv_location);
        tv_address.setTypeface(Global.setFont(this,Global.LIGHTFONT));


        getDonorDetails();
    }

    public void getDonorDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.profile_getdata + getIntent().getStringExtra("USER_ID"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                JSONObject ob_res = object.getJSONObject("result");
                                if (!ob_res.getString("name").equals(null) && !ob_res.getString("name").equals("null") && !ob_res.getString("name").equals("")) {
                                    tv_name.setText(ob_res.getString("name"));
                                }
                                if (!ob_res.getString("phoneNumber").equals(null) && !ob_res.getString("phoneNumber").equals("null") && !ob_res.getString("phoneNumber").equals("")) {
                                    tv_mobile.setText(ob_res.getString("phoneNumber"));
                                }
                                if (!ob_res.getString("bloodGroup").equals(null) && !ob_res.getString("bloodGroup").equals("null") && !ob_res.getString("bloodGroup").equals("")) {
                                    tv_bgroup.setText(ob_res.getString("bloodGroup"));
                                }
                                if (!ob_res.getString("address").equals(null) && !ob_res.getString("address").equals("null") && !ob_res.getString("address").equals("")) {
                                    tv_address.setText(ob_res.getString("address"));
                                }
                                if (!ob_res.getString("gender").equals("null") && !ob_res.getString("gender").equals("")) {
                                    tv_gender.setText(ob_res.getString("gender"));
                                }
                                String profilepic = ob_res.getString("profilePicture");

                                if (!(profilepic.equals("")) && !(profilepic.equals(null))) {
                                    String url = Global.profileimageurl + profilepic;
                                    Picasso.with(getApplicationContext()).load(url).transform(new CircleTransform()).placeholder(R.mipmap.no_image).into(iv_propic);
                                } else {
                                    bitmap = getRoundedShape(BitmapFactory.decodeResource(getApplication().getResources(), R.mipmap.no_image));
                                    iv_propic.setImageBitmap(bitmap);
                                }
                                MyApplication.getInstance().trackEvent("getDonorDetails","status=1","got all donors list");
                            } else {
                                String message = object.getString("message");
                                Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                                MyApplication.getInstance().trackEvent("getDonorDetails","status=0",message);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("getDonorDetails","mainjsonparsingexception","get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("getDonorDetails","volley error listener","get into some exception");
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 300;
        int targetHeight = 300;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2, ((float) targetHeight - 1) / 2, (Math.min(((float) targetWidth), ((float) targetHeight)) / 2), Path.Direction.CCW);
        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public void CallDonor_btnClick(View v) {

        if (!tv_mobile.getText().toString().trim().equals("")) {

            Uri callUri = Uri.parse("tel:" + tv_mobile.getText().toString().trim());
            Intent callIntent = new Intent(Intent.ACTION_DIAL, callUri);
            startActivity(callIntent);
            //finish();
            MyApplication.getInstance().trackEvent("CallDonor_btnClick","onClick","calling to donor will happen");
        }

    }

    public void SendRequest_btnClick(View v) {

        SendRequest();
        MyApplication.getInstance().trackEvent("SendRequest_btnClick","onClick","specified action happens");

    }

    private void SendRequest() {
        progressDialog = ProgressDialog.show(IndividualDetails.this,
                "Sending Request", "Please wait!");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.SingleReq,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            String message = object.getString("message");
                            if (success == 1) {
                                TastyToast.makeText(IndividualDetails.this, "" + message, TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                progressDialog.dismiss();
                                MyApplication.getInstance().trackEvent("IndividualDetails SendRequest","status=1","success");
                            } else {
                                TastyToast.makeText(IndividualDetails.this, "" + message, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                progressDialog.dismiss();
                                MyApplication.getInstance().trackEvent("IndividualDetails SendRequest","status=0",message);
                            }
                        } catch (Exception e) {
                            Toast.makeText(IndividualDetails.this, "" + e, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("IndividualDetails SendRequest","mainjsonparsingexception","get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TastyToast.makeText(IndividualDetails.this, "" + error, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("IndividualDetails SendRequest","volley error listener","get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", sp.getString("user_id", ""));
                params.put("donarId", getIntent().getStringExtra("USER_ID"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(IndividualDetails.this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getDonorDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("IndividualDetails");
    }
}
