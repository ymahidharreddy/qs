package mizpahsoft.apps.bplus.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mizpahsoft.apps.bplus.R;
import mizpahsoft.apps.bplus.app.MyApplication;
import mizpahsoft.apps.bplus.barcode.BarcodeTracker;
import mizpahsoft.apps.bplus.barcode.BarcodeTrackerFactory;
import mizpahsoft.apps.bplus.camera.CameraSource;
import mizpahsoft.apps.bplus.camera.CameraSourcePreview;
import mizpahsoft.apps.bplus.utils.Global;

public class BloodUnitStatus extends AppCompatActivity
        implements BarcodeTracker.BarcodeGraphicTrackerCallback {

    private static final String TAG = "Barcode-reader";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    String barcode_data = "", blood_unit_status_code = "", mobile_number = "";
    ImageView iv_back;
    //barcode
    TextView tv_title_mobile, tv_donor_mobile, tv_scan;
    ProgressBar progressBarItem;
    ImageView iv_reload;
    Button btn_next;
    LinearLayout ll_donor_info;

    //status
    TextView td8, tv_stock, tv_rejected, tv_patient;
    EditText et_patient_name;
    Button btn_submit;
    ViewFlipper viewFlipLayout;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_blood_unit_status);

        viewFlipLayout = (ViewFlipper) findViewById(R.id.viewFlipLayout);

        TextView tv_title_main = (TextView) findViewById(R.id.tv_title_main);
        tv_title_main.setTypeface(Global.setFont(this, Global.REGULARFONT));
        tv_title_main.setText(getString(R.string.bs_title));

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //barcode intialisation
        tv_title_mobile = (TextView) findViewById(R.id.tv_title_mobile);
        tv_title_mobile.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_scan = (TextView) findViewById(R.id.tv_scan);
        tv_scan.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        progressBarItem = (ProgressBar) findViewById(R.id.progressBarItem);

        ll_donor_info = (LinearLayout) findViewById(R.id.ll_donor_info);

        tv_donor_mobile = (TextView) findViewById(R.id.tv_donor_mobile);
        tv_donor_mobile.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        iv_reload = (ImageView) findViewById(R.id.iv_reload);
        iv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcode_data = "";
                ll_donor_info.setVisibility(View.GONE);
                mPreview.setVisibility(View.VISIBLE);
            }
        });
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipLayout.showNext();
            }
        });


        //status intialisation
        td8 = (TextView) findViewById(R.id.td8);
        td8.setTypeface(Global.setFont(this, Global.LIGHTFONT));

        tv_stock = (TextView) findViewById(R.id.tv_stock);
        tv_stock.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blood_unit_status_code = "1";
                tv_stock.setBackgroundResource(R.drawable.edit_text_green_bg);
                tv_stock.setTextColor(getResources().getColor(R.color.textColorGreen));

                tv_rejected.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_rejected.setTextColor(getResources().getColor(R.color.textColor777));
                tv_patient.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_patient.setTextColor(getResources().getColor(R.color.textColor777));

                btn_submit.setBackgroundResource(R.drawable.bt_yellow_bg);
                btn_submit.setClickable(true);

                et_patient_name.setVisibility(View.GONE);
            }
        });

        tv_rejected = (TextView) findViewById(R.id.tv_rejected);
        tv_rejected.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blood_unit_status_code = "2";
                tv_rejected.setBackgroundResource(R.drawable.edit_text_green_bg);
                tv_rejected.setTextColor(getResources().getColor(R.color.textColorGreen));

                tv_stock.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_stock.setTextColor(getResources().getColor(R.color.textColor777));
                tv_patient.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_patient.setTextColor(getResources().getColor(R.color.textColor777));

                btn_submit.setBackgroundResource(R.drawable.bt_yellow_bg);
                btn_submit.setClickable(true);

                et_patient_name.setVisibility(View.GONE);
            }
        });

        tv_patient = (TextView) findViewById(R.id.tv_patient);
        tv_patient.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        tv_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blood_unit_status_code = "3";
                tv_patient.setBackgroundResource(R.drawable.edit_text_green_bg);
                tv_patient.setTextColor(getResources().getColor(R.color.textColorGreen));

                tv_stock.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_stock.setTextColor(getResources().getColor(R.color.textColor777));
                tv_rejected.setBackgroundResource(R.drawable.edit_text_gray_bg);
                tv_rejected.setTextColor(getResources().getColor(R.color.textColor777));

                btn_submit.setBackgroundResource(R.drawable.bt_yellow_bg);
                btn_submit.setClickable(true);

                et_patient_name.setVisibility(View.VISIBLE);
            }
        });


        et_patient_name = (EditText) findViewById(R.id.et_patient_name);
        et_patient_name.setVisibility(View.GONE);
        et_patient_name.setTypeface(Global.setFont(this, Global.LIGHTFONT));
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setClickable(false);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blood_unit_status_code.equals("")) {
                    TastyToast.makeText(BloodUnitStatus.this, getString(R.string.bu_status), TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                } else if (blood_unit_status_code.equals("3") && et_patient_name.getText().toString().equals("")) {
                    TastyToast.makeText(BloodUnitStatus.this, getString(R.string.bu_patient), TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                } else if (!Global.isInternetPresent(getApplicationContext())) {
                    TastyToast.makeText(BloodUnitStatus.this, getString(R.string.no_internet), TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                } else {
                    updateBloodUnitStatus();
                }

            }
        });
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);

        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }
    }


    @Override
    public void onDetectedQrCode(final Barcode barcode) {
        if (barcode != null) {

            try {
                this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (barcode_data.equals("")) {
                            barcode_data = barcode.displayValue;
                            getMobileNumberByRFID();
                            //tv_scan.setText(barcode.displayValue);
                        }

                        //Toast.makeText(getApplicationContext(), "ON UI Thread", Toast.LENGTH_LONG).show();
                    }
                });

                //Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                String ss = e.toString();
            }

        }
    }

    // Handles the requesting of the camera permission.
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
        }
    }

    /**
     * Creates and starts the camera.
     * <p>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(this);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error,
                        Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(metrics.widthPixels, metrics.heightPixels)
                .setRequestedFps(24.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    // Restarts the camera
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
        MyApplication.getInstance().trackScreenView("Blood unit status screen");
    }

    // Stops the camera
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            boolean autoFocus = true;
            boolean useFlash = false;
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void updateBloodUnitStatus() {
        btn_submit.setBackgroundResource(R.drawable.bt_ash_bg);
        btn_submit.setClickable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.UPDATE_BLOOD_UNIT_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSONArray arr = new JSONArray(response);
                            JSONObject object = new JSONObject(response);
                            int success = object.getInt("status");
                            if (success == 1) {
                                TastyToast.makeText(BloodUnitStatus.this, "" + object.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                finish();
                            } else {
                                TastyToast.makeText(BloodUnitStatus.this, "" + object.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                MyApplication.getInstance().trackEvent("SaveBloodUnit", "status=0", object.getString("message"));
                            }
                        } catch (Exception e) {
                            TastyToast.makeText(BloodUnitStatus.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            MyApplication.getInstance().trackException(e);
                            MyApplication.getInstance().trackEvent("SaveBloodUnit", "mainjsonparsingexception" + e, "get into some exception");
                        }
                        btn_submit.setBackgroundResource(R.drawable.bt_yellow_bg);
                        btn_submit.setClickable(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btn_submit.setBackgroundResource(R.drawable.bt_yellow_bg);
                        btn_submit.setClickable(true);
                        TastyToast.makeText(BloodUnitStatus.this, getString(R.string.server_error), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        MyApplication.getInstance().trackException(error);
                        MyApplication.getInstance().trackEvent("SaveBloodUnit", "volley error listener", "get into some exception");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phoneNumber", mobile_number);
                params.put("RFID", barcode_data.trim());
                params.put("status",blood_unit_status_code);
                params.put("patientName", et_patient_name.getText().toString().trim());
                //  params.put("phoneNumber", et_mobile.getText().toString().trim());
                params.put("bloodBankId", Global.getSP(getApplicationContext(), Global.USER_ID));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void getMobileNumberByRFID() {
        progressBarItem.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Global.MOBILE_NUMBER_BY_RFID + "?rfIdcode=" + barcode_data.trim(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getInt("status") == 1) {
                        tv_donor_mobile.setText(object.getString("phoneNumber"));
                        mobile_number = object.getString("phoneNumber");
                        ll_donor_info.setVisibility(View.VISIBLE);
                        mPreview.setVisibility(View.GONE);
                    } else {
                        barcode_data = "";
                        TastyToast.makeText(BloodUnitStatus.this, "" + object.getString("message"), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    TastyToast.makeText(BloodUnitStatus.this, "" + e, TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    MyApplication.getInstance().trackException(e);
                    MyApplication.getInstance().trackEvent("SaveBloodUnit mobile_by_rfid", "mainjsonparsingexception" + e, "get into some exception");

                }

                progressBarItem.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                barcode_data = "";
                progressBarItem.setVisibility(View.GONE);
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
}

