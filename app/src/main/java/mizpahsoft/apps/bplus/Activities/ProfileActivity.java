package mizpahsoft.apps.bplus.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.sdsmdg.tastytoast.TastyToast;
import com.shawnlin.numberpicker.NumberPicker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.utils.CircleTransform;
import mizpahsoft.apps.bplus.utils.Global;


/**
 * Created by Mizpah-DEV on 07-Nov-16.
 */

public class ProfileActivity extends Activity {
    private static final String TAG = "ProfileActivity";
    ImageView iv_propic;
    EditText et_profile_name, et_profile_mobile;
    Button bt_update, bt_ok;
    TextView ac_location;
    String userid = "", name = "", mobile = "", blood_type_id = "", image = "", location = "", gender = "";
    SharedPreferences sp;
    ArrayList<String> ar_bloodtype, ar_bloodtypeid;
    Bitmap bitmap;
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD7GFvtyyLyDAPdAMyc0lf7axug6QZt6OQ";
    ProgressDialog progressDialog;
    static String REQUEST_TYPE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE_CAM = 2;
    String ar_gender[] = {"MALE", "FEMALE", "OTHERS"};
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 101;
    int temp_mob_num;
    ProgressDialog progressDialog1;
    ArrayList<String> age_array = new ArrayList<>();
    String age = "";
    static final String SELECT_AGE = "Select Age";
    static final String SELECT_BLOOD_GROUP = "Select Blood Group";
    TextView tv_age, tv_blood_group, tv_gender;
    String[] ages, bloodGroups;
    boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText("My Profile");

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sp = getSharedPreferences("loginprefs", 0);
        userid = sp.getString("user_id", "N/A");
        iv_propic = (ImageView) findViewById(R.id.iv_propic);

        ac_location = (TextView) findViewById(R.id.ac_location);
        ac_location.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_age.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_blood_group = (TextView) findViewById(R.id.tv_blood_group);
        tv_blood_group.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_gender.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        age_array.clear();
        for (int i = 17; i < 100; i++) {
            age_array.add("" + i);
        }
        ages = new String[age_array.size()];
        ages = age_array.toArray(ages);

        tv_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(ages, tv_age);
            }
        });
        tv_blood_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(bloodGroups, tv_blood_group);
            }
        });
        tv_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(ar_gender, tv_gender);
            }
        });

        ac_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progressDialog1 = ProgressDialog.show(ProfileActivity.this, "", "Please wait..");
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(ProfileActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    MyApplication.getInstance().trackEvent("ac_address", "onClick", "places intent called");
                } catch (GooglePlayServicesRepairableException e) {
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("ac_address", "onClick", "places intent failed exception");
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("ac_address", "onClick", "places intent failed exception");
                    // TODO: Handle the error.
                }
            }
        });

        et_profile_name = (EditText) findViewById(R.id.et_profile_name);
        et_profile_name.setTypeface(Global.setFont(this, Global.LIGHTFONT));


        et_profile_mobile = (EditText) findViewById(R.id.et_profile_mobile);
        et_profile_mobile.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        et_profile_mobile.setEnabled(false);


        ar_bloodtype = new ArrayList<>();
        ar_bloodtypeid = new ArrayList<>();

        bt_update = (Button) findViewById(R.id.bt_update);
        bt_update.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_ok.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        getSpinnerData();

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_profile_name.getText().toString().trim();
                mobile = et_profile_mobile.getText().toString().trim();
                location = ac_location.getText().toString().trim();
                if (!et_profile_mobile.getText().toString().equals("")
                        && !et_profile_mobile.getText().toString().equals(null)) {
                    temp_mob_num = Integer
                            .parseInt(String.valueOf(et_profile_mobile.getText().toString().toCharArray()[0]));
                } else {
                    temp_mob_num = -1;

                }
                checkTolerance();
                MyApplication.getInstance().trackEvent("bt_update", "onClick", "checkTolerance");
            }
        });

        iv_propic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();
                MyApplication.getInstance().trackEvent("iv_tochange", "onClick", "showFileChooser");
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra("FROM_MAINACTIVITY")) {
            if (getIntent().getStringExtra("FROM_MAINACTIVITY").equals("MUST_UPDATE_PROFILE")) {
                TastyToast.makeText(getApplicationContext(), "Please update your profile.", TastyToast.LENGTH_LONG,
                        TastyToast.ERROR);
            }
        } else {
            finish();
        }
    }

    private void showFileChooser() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    REQUEST_TYPE = "CAMERA";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Do something for lollipop and above versions
                        if (checkPermission()) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, 1);
                            }
                        } else {
                            requestPermission();
                        }
                    } else {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, 1);
                        }
                    }


                } else if (items[item].equals("Choose from Gallery")) {

                    REQUEST_TYPE = "GALLERY";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Do something for lollipop and above versions
                        if (checkPermission()) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        } else {
                            requestPermission();
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }


                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_CAM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (REQUEST_TYPE.equals("CAMERA")) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, 1);
                            MyApplication.getInstance().trackEvent("onReqPermission", "camera permission", "accepted & called respective intent");
                        }
                    }
                } else {
                    TastyToast.makeText(getApplicationContext(), "you denied permission? you can't add your profile pic.", TastyToast.LENGTH_LONG,
                            TastyToast.ERROR);
                    MyApplication.getInstance().trackEvent("onReqPermission", "camera/gallery permission", "denied by user");
                }
                break;
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (REQUEST_TYPE.equals("GALLERY")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                        MyApplication.getInstance().trackEvent("onReqPermission", "gallery permission", "accepted & called respective intent");
                    }
                } else {
                    TastyToast.makeText(getApplicationContext(), "you denied permission? you can't add your profile pic.", TastyToast.LENGTH_LONG,
                            TastyToast.ERROR);
                    MyApplication.getInstance().trackEvent("onReqPermission", "camera/gallery permission", "denied by user");
                }
                break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void requestPermission() {
        if (REQUEST_TYPE.equals("GALLERY")) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Toast.makeText(this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ProfileActivity.this);
                // set title
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setCancelable(true);
                // set dialog message
                alertDialogBuilder
                        .setMessage("READ_EXTERNAL_STORAGE permission allows us to access location data. Please allow in App Settings for additional functionality.")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    dialog.cancel();
                                    MyApplication.getInstance().trackEvent("requestPermission()", "accepted by user", "gallery permission given");
                                    //so some work
                                } catch (Exception e) {
                                    //Exception
                                }
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            } else {
                MyApplication.getInstance().trackEvent("requestPermission()", "denied by user(gallery)", "requesting again(We can't assure that this action works)");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                // Toast.makeText(this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ProfileActivity.this);
                // set title
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setCancelable(true);
                // set dialog message
                alertDialogBuilder
                        .setMessage("CAMERA permission allows us to take picture. Please allow in App Settings for additional functionality.")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    dialog.cancel();
                                    MyApplication.getInstance().trackEvent("requestPermission()", "accepted by user", "camera permission given");
                                    //so some work
                                } catch (Exception e) {
                                    //Exception
                                }
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            } else {
                MyApplication.getInstance().trackEvent("requestPermission()", "denied by user(camera)", "requesting again(We can't assure that this action works)");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAM);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int THUMBNAIL_HEIGHT = 400;
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                image = encodeTobase64(bitmap);
                iv_propic.setImageBitmap(getRoundedShape(bitmap));
                MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=1", "got bitmap");
            } else if (requestCode == 2) {

                Bitmap bitmap2 = null;
                try {

                    bitmap2 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    Matrix matrix = new Matrix();
                    String manufacturer = Build.MANUFACTURER;
                    String model = Build.MODEL;
                    if (manufacturer.equalsIgnoreCase("Samsung") && model.equalsIgnoreCase("SM-J200G")) {
                        matrix.postRotate(90);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap2, 220, 220, true);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                        iv_propic.setImageBitmap(getRoundedShape(rotatedBitmap));
                        image = encodeTobase64(rotatedBitmap);
                        MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "got resized bitmap for samsung");
                    } else {
                        iv_propic.setImageBitmap(getRoundedShape(bitmap2));
                        image = encodeTobase64(bitmap2);
                        MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "got bitmap");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("onActivityResult", "requestcode=2", "get into some exception");
                }
            }
        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                location = "" + place.getAddress();
                ac_location.setText(location);
                ac_location.setTextColor(Color.BLACK);
                progressDialog1.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "got address");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());
                location = "";
                progressDialog1.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "error in getting address");

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                location = "";
                progressDialog1.dismiss();
                MyApplication.getInstance().trackEvent("onActivityResult", "places auto complete", "cancelled(getting address)");
            }
        }
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] tempbyte = getBytesFromBitmap(resize(image, 500, 500));
        String imageEncoded = Base64.encodeToString(tempbyte, Base64.DEFAULT);


        return imageEncoded;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public boolean checkPermission() {
        if (REQUEST_TYPE.equals("GALLERY")) {
            int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED) {
                MyApplication.getInstance().trackEvent("checkPermission", "for gallery", "yes");
                return true;
            } else {
                MyApplication.getInstance().trackEvent("checkPermission", "for gallery", "no");
                return false;
            }
        } else {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (result == PackageManager.PERMISSION_GRANTED) {
                MyApplication.getInstance().trackEvent("checkPermission", "for camera", "yes");
                return true;
            } else {
                MyApplication.getInstance().trackEvent("checkPermission", "for camera", "no");
                return false;
            }
        }
    }

    public void checkTolerance() {

        int position = (int) tv_gender.getTag();
        if (position == 0) {
            gender = "1";
        } else if (position == 1) {
            gender = "2";
        } else if (position == 2) {
            gender = "0";
        }
        int pos = (int) tv_blood_group.getTag();
        blood_type_id = ar_bloodtypeid.get(pos);

        age = tv_age.getText().toString().trim();

        if (name.equals("")) {
            TastyToast.makeText(getApplicationContext(), "Please enter your name!", TastyToast.LENGTH_LONG,
                    TastyToast.ERROR);
        } else if (name.length() < 3) {
            TastyToast.makeText(getApplicationContext(), "Name should be minimum 3 characters in length", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        } else if (mobile.equals("")) {
            TastyToast.makeText(getApplicationContext(), "Please enter your mobile number", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        } else if (temp_mob_num < 7) {
            TastyToast.makeText(getApplicationContext(), "Please enter valid mobile number!", TastyToast.LENGTH_LONG,
                    TastyToast.ERROR);
        } else if (mobile.length() < 10) {
            TastyToast.makeText(getApplicationContext(), "Please enter valid mobile number", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        } else if (age.equals("")) {
            TastyToast.makeText(getApplicationContext(), "Please select Age", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        } else if (blood_type_id.equals("")) {
            TastyToast.makeText(getApplicationContext(), "Please select Blood Group", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        } else if (location.equals("")) {
            TastyToast.makeText(getApplicationContext(), "Please enter your location", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        } else {
            bt_update.setEnabled(false);
            goForUpdate();
            MyApplication.getInstance().trackEvent("checkTolerance", "method", "goForUpdate");
        }
    }

    public void getSpinnerData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.profile_spinnerdata,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                JSONArray ar_results = object.getJSONArray("result");
                                for (int i = 0; i < ar_results.length(); i++) {
                                    JSONObject ob_results = ar_results.getJSONObject(i);
                                    ar_bloodtype.add(ob_results.getString("bloodgroup"));
                                    ar_bloodtypeid.add(ob_results.getString("id"));
                                }
                                bloodGroups = new String[ar_bloodtype.size()];
                                bloodGroups = ar_bloodtype.toArray(bloodGroups);
                                getProfileData();
                                MyApplication.getInstance().trackEvent("getSpinnerData", "status=1", "spinner populated");
                            } else {
                                String message = object.getString("message");
                                MyApplication.getInstance().trackEvent("getSpinnerData", "status=0", object.getString("message"));
                                //Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("getSpinnerData", "mainjsonparsingexception", "get into some exception");
                            //Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("getSpinnerData", "volley error listener", "get into some exception");
                        // Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getProfileData() {
        progressDialog = ProgressDialog.show(ProfileActivity.this, "", "Please wait..");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.profile_getdata + userid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                JSONObject ob_res = object.getJSONObject("result");
                                if (!ob_res.getString("name").equals(null) && !ob_res.getString("name").equals("null")) {
                                    et_profile_name.setText(ob_res.getString("name"));
                                }
                                if (!ob_res.getString("phoneNumber").equals(null) && !ob_res.getString("phoneNumber").equals("null")) {
                                    et_profile_mobile.setText(ob_res.getString("phoneNumber"));
                                }
                                if (!ob_res.getString("age").equals(null) && !ob_res.getString("age").equals("null")) {
                                    age = ob_res.getString("age");
                                    tv_age.setText(age);
                                    tv_age.setTag(ageStringToPosition(age));
                                } else {
                                    tv_age.setTag(0);
                                }
                                if (!ob_res.getString("bloodGroup").equals(null) && !ob_res.getString("bloodGroup").equals("null")) {
                                    String bg = ob_res.getString("bloodGroup");
                                    for (int j = 0; j < ar_bloodtype.size(); j++) {
                                        if (bg.equals(ar_bloodtype.get(j))) {
                                            tv_blood_group.setText((ar_bloodtype.get(j)));
                                            tv_blood_group.setTag(j);
                                        }
                                    }
                                } else {
                                    tv_blood_group.setTag(0);
                                }
                                if (!ob_res.getString("address").equals(null) && !ob_res.getString("address").equals("null")) {
                                    ac_location.setText(ob_res.getString("address"));
                                }
                                if (!ob_res.getString("gender").equals(null) && !ob_res.getString("gender").equals("null") && !ob_res.getString("gender").equals("")) {
                                    String gender_own = ob_res.getString("gender");
                                    for (int m = 0; m < ar_gender.length; m++) {
                                        if (ar_gender[m].equalsIgnoreCase(gender_own)) {
                                            tv_gender.setText(ar_gender[m]);
                                            tv_gender.setTag(m);
                                        }
                                    }
                                } else {
                                    tv_gender.setTag(0);
                                }
                                String profilepic = ob_res.getString("profilePicture");

                                if (!(profilepic.equals("")) && !(profilepic.equals(null))) {
                                    String url = Global.profileimageurl + profilepic;
                                    Picasso.with(getApplicationContext()).load(url).transform(new CircleTransform()).placeholder(R.drawable.profile_noimage).resize(208, 208).into(iv_propic);
                                    progressDialog.dismiss();
                                } else {
                                    bitmap = getRoundedShape(BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.profile_noimage));
                                    iv_propic.setImageBitmap(bitmap);
                                    progressDialog.dismiss();
                                }
                                progressDialog.dismiss();
                                MyApplication.getInstance().trackEvent("getProfileData", "status=1", "sucessfully loaded profile data");
                            } else {
                                String message = object.getString("message");
                                //Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                MyApplication.getInstance().trackEvent("getProfileData", "status=0", object.getString("message"));
                            }
                        } catch (Exception e) {
                            //Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("getProfileData", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("getProfileData", "volley error listener", "get into some exception");
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

    public void goForUpdate() {
        progressDialog = ProgressDialog.show(ProfileActivity.this, "", "Please wait..");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.Update_Profile_URl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject object = arr.getJSONObject(0);
                            int success = object.getInt("status");
                            if (success == 1) {
                                TastyToast.makeText(getApplicationContext(), "Updated Successfully.", TastyToast.LENGTH_LONG,
                                        TastyToast.SUCCESS);
                                bt_update.setEnabled(true);
                                progressDialog.dismiss();
                                if (getIntent().hasExtra("FROM_MAINACTIVITY")) {
                                    if (getIntent().getStringExtra("FROM_MAINACTIVITY").equals("MUST_UPDATE_PROFILE")) {
                                        //for new user who needs to update the profile madatory
                                        //after successful update,navigating to home screen
                                        Global.saveSP(getApplicationContext(), Global.LOGIN_CHECK, "1");
                                        finish();

                                    }
                                } else {
                                    //for user who has profile data
                                    //after successfull update showing the updated data
                                    getProfileData();
                                }

                                MyApplication.getInstance().trackEvent("goForUpdate", "status=1", "profile update success");

                            } else {
                                bt_update.setEnabled(true);
                                String message = object.getString("message");
                                //Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                MyApplication.getInstance().trackEvent("goForUpdate", "status=0", object.getString("message"));
                            }
                        } catch (Exception e) {
                            bt_update.setEnabled(true);
                            //Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("goForUpdate", "mainjsonparsingexception", "get into some exception");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        bt_update.setEnabled(true);
                        //Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("goForUpdate", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", userid);
                params.put("name", name);
                params.put("phoneNumber", mobile);
                params.put("age", age);
                params.put("address", location);
                params.put("bloodGroup", blood_type_id);
                params.put("proPic", image);
                params.put("gender", gender);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            progressDialog1.dismiss();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("ProfileActivity");
    }

    public void showDialog(final String[] values, final TextView textView) {

        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.layout_number_picker);
        dialog.setCancelable(false);
        dialog.show();
        try {
            //Grab the window of the dialog, and change the width
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            //This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int position = 0;
        try {
            position = (int) textView.getTag();
        } catch (Exception e) {
            e.printStackTrace();
        }

        NumberPicker number_picker = (NumberPicker) dialog.findViewById(R.id.number_picker);
        // set divider color
        number_picker.setDividerColor(getResources().getColor(R.color.dividerColor));
        number_picker.setDividerColorResource(R.color.dividerColor);

        // set formatter
        number_picker.setFormatter(getString(R.string.number_picker_formatter));
        number_picker.setFormatter(R.string.number_picker_formatter);

        // set text color
        number_picker.setTextColor(getResources().getColor(R.color.textColorBloodGroup));
        number_picker.setTextColorResource(R.color.textColorBloodGroup);

        // set text size
        number_picker.setTextSize(getResources().getDimension(R.dimen.text_size));
        number_picker.setTextSize(R.dimen.text_size);

        // set typeface
        number_picker.setTypeface(Global.setFont(this, Global.LIGHTFONT));


        //Populate NumberPicker values from String array values
        //Set the minimum value of NumberPicker
        number_picker.setMinValue(0); //from array first value
        //Specify the maximum value/number of NumberPicker
        number_picker.setMaxValue(values.length - 1); //to array last value

        //Specify the NumberPicker data source as array elements
        number_picker.setDisplayedValues(values);
        number_picker.setValue(position);

        number_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected value from picker
                isSelected = true;
                textView.setText(values[newVal]);
                textView.setTag(newVal);
            }
        });

        Button button = (Button) dialog.findViewById(R.id.bt_ok);
        button.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!isSelected) {
                    if (values.length == 1) {
                        textView.setText(values[0]);
                        textView.setTag(0);
                    } else {
                        textView.setText(values[1]);
                        textView.setTag(1);
                    }
                }
                isSelected = false;
            }
        });
    }

    public int ageStringToPosition(String age) {
        for (int i = 0; i < ages.length; i++) {
            if (age.equalsIgnoreCase(ages[i])) {
                return i;
            }
        }
        return 0;
    }
}
