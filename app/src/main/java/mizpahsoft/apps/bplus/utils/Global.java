package mizpahsoft.apps.bplus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import org.json.JSONArray;

/***Created by Mizpah on 11/5/2016*/
public class Global {

    //For All api calls
    public static final String ProdUrl = "http://bloodsources.com/v1/Webservices/";
    public static final String StageURL = "http://bloodsources.com/test/v1/Webservices/";
    public static final String Base_URL = ProdUrl;

    // For Loding All images
    public static final String Images_URL_Stage = "http://bloodsources.com/test/v1/";
    public static final String Images_ProdUrl = "http://bloodsources.com/v1/";
    public static final String Images_Base_URL = Images_ProdUrl;

    public static final String Banner_Url = Base_URL + "Banners/banners";
    public static final String Lgoin_Url = Base_URL + "users/signin";
    public static final String Registraion_URL = Base_URL + "users/register";
    public static final String AddDonor = Base_URL + "BloodBanks/addDonar";
    public static final String ADD_DONOR_RESEND_OTP_URL = Base_URL + "BloodBanks/reSendOtp";
    public static final String CHECK_PASSWORD_EXISTS_API = Base_URL + "users/checkPwdExistsInlogin";
    public static final String ADD_BLOOD_UNIT = Base_URL + "BloodUnits/addBloodUnit";
    public static final String MOBILE_NUMBER_BY_RFID = Base_URL + "BloodUnits/mobileNumberByRFID";

    public static final String UPDATE_BLOOD_UNIT_STATUS = Base_URL + "BloodUnits/updateBloodUnitStatus";
    public static final String CREATE_PASSWORD = Base_URL + "users/createPassword";

    public static final String CHangepwd_URL = Base_URL + "Users/changePassword";
    public static final String RequestBlood_URL = Base_URL + "bloodRequests/addBloodRequest";
    public static final String profile_spinnerdata = Base_URL + "BloodRequests/bloodgroups";
    public static final String Events_URl = Base_URL + "Events/events?start=";
    public static final String Tips_Url = Base_URL + "Tips/tips?start=";
    public static final String RecentRequests_Url = Base_URL + "BloodRequests/bloodRequests?userId=";
    public static final String BloodBanks_Url = Base_URL + "BloodBanks/bloodBanks?start=";
    public static final String Testimonials_Url = Base_URL + "BloodRequests/testmonials?start=";

    public static final String suggestdonor = Base_URL + "bloodRequests/suggestDonar";
    public static final String Update_Profile_URl = Base_URL + "Users/UpdateProfile";

    public static final String profileimageurl = Images_Base_URL + "uploads/profilePics/";
    public static final String profile_getdata = Base_URL + "Users/profile?userId=";
    public static final String VerifiOTP = Base_URL + "Users/authenticationByOtp";
    public static final String VerifiAdddonorOTP = Base_URL + "BloodBanks/authenticationOfDonar";

    public static final String sendno_forgotpass = Base_URL + "Users/forgotPassword";
    public static final String sendpass_forgotpass = Base_URL + "Users/changePasswordByOTP";
    public static final String Notifications_URL = Base_URL + "BloodRequests/notifications?userId=";
    public static final String Logout_Url = Base_URL + "Users/logout";
    public static final String Accept_Url = Base_URL + "BloodRequests/acceptRequest";
    public static final String UpdateDonar_UrL = Base_URL + "BloodRequests/updateRequestDonor";
    public static final String UserLocationandBloodGrup = Base_URL + "Users/addUserDetails";
    public static final String Events1_URl = Base_URL + "Events/events?userId=";
    public static final String Events2_URl = Base_URL + "Events/events?eventId=";
    public static final String EventsUpdate_URl = Base_URL + "Events/UpdateEvent";
    public static final String Events_image = Images_Base_URL + "uploads/eventimages/";
    public static final String addevent_url = Base_URL + "Events/addEvents";
    public static final String NotificationsCount_URL = Base_URL + "BloodRequests/notificationsCount";
    public static final String NotificationsCount_Read_URL = Base_URL + "BloodRequests/updateNotifications";
    public static final String DONORS_WITH_BOOLD_GROUP = Base_URL + "BloodRequests/donars";
    public static final String API_SEARCH_DONORS_URL = Base_URL + "BloodRequests/searchDonars?bloodGroup=";

    public static final String SingleReq = Base_URL + "bloodRequests/singleRequest";
    public static final String HideNumber = Base_URL + "Users/hideMobileNumber";

    public static final String Admin_events_to_aproove = Base_URL + "Events/inactiveEvents";
    public static final String Admin_events_aprooved = Base_URL + "Events/approveEvent";
    public static final String Notification_Enable = Base_URL + "Users/notiEnable";
    public static final String Delete_Event = Base_URL + "Events/delete_event";
    public static final String UPDATE_FCM_TOKEN = Base_URL + "users/UpdateToken";
    public static final String APP_SETTINGS = Base_URL + "Settings/appSettings";

    public static final String API_VIDEO_ADS = Base_URL + "Banners/videoAds";
    public static final String VIDEO_ADS_PATH = Images_Base_URL + "uploads/videoAds/";


    public static String bllod_group_reqblood = "";

    public static boolean ReSend = true;

    public static JSONArray RequestusersArray;

    public static final int ACTIVITY_FOR_RESULT = 1;

    public static final String REGULARFONT = "fonts/Lato-Regular.ttf";
    public static final String LIGHTFONT = "fonts/Lato-Light.ttf";
    public static final String BLACKFONT = "fonts/Lato-Black.ttf";
    public static final String BOLDFONT = "fonts/Lato-Bold.ttf";
    public static final String ITALIC = "fonts/Lato-Italic.ttf";

    //keys for sharedprefs
            /*typeOfLogin   =1  For normal user
            typeOfLogin   =2  For blood bank*/
    public static final String USER_TYPE = "USER_TYPE";
    public static final String USER_ID = "user_id";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String LOGIN_CHECK = "logincheck";
    public static final String IS_ADS_SHOW = "IS_ADS_SHOW";
    public static final String BLOOD_BANK_NAME = "BLOOD_BANK_NAME";
    public static final String EST_YEAR = "EST_YEAR";
    public static final String ADDRESS = "ADDRESS";
    public static final String EMAIL_ID = "EMAIL_ID";
    public static final String WEBSITE_URL = "WEBSITE_URL";
    public static final String[] countriesCodes = {"+91"};


    public static Typeface setFont(Context context, String TYPE) {
        switch (TYPE) {
            case REGULARFONT:
                return Typeface.createFromAsset(context.getAssets(), REGULARFONT);
            case LIGHTFONT:
                return Typeface.createFromAsset(context.getAssets(), LIGHTFONT);
            case BLACKFONT:
                return Typeface.createFromAsset(context.getAssets(), BLACKFONT);
            case BOLDFONT:
                return Typeface.createFromAsset(context.getAssets(), BOLDFONT);
            case ITALIC:
                return Typeface.createFromAsset(context.getAssets(), ITALIC);
        }

        //default
        return Typeface.createFromAsset(context.getAssets(), LIGHTFONT);
    }

    public static Boolean isInternetPresent(Context context) {
        ConnectionDetector cd = new ConnectionDetector(context);
        return cd.isConnectingToInternet();
    }


    public static void saveSP(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("loginprefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putString(key, value);
        et.apply();

    }


    public static String getSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("loginprefs", Context.MODE_PRIVATE);
        return sp.getString(key, "NA");

    }

    public static void clearSP(Context context) {
        SharedPreferences sp = context.getSharedPreferences("loginprefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.clear().apply();

    }
}
