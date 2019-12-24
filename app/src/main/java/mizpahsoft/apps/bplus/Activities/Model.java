package mizpahsoft.apps.bplus.Activities;

import java.util.ArrayList;

/**
 * Created by Mizpah on 11/8/2016.
 */

public class Model {

    private String DonorName = "";
    private String DomnorImage = "";
    public  String userId="";
    public static String useridtopass;

    public static ArrayList<AccepteUsersModel> AcceptedUsers;


    public String getRecentReq_Name() {
        return RecentReq_Name;
    }

    public void setRecentReq_Name(String recentReq_Name) {
        RecentReq_Name = recentReq_Name;
    }

    public String getRecentReq_Time() {
        return RecentReq_Time;
    }

    public void setRecentReq_Time(String recentReq_Time) {
        RecentReq_Time = recentReq_Time;
    }

    public String getRecentReq_Location() {
        return RecentReq_Location;
    }

    public void setRecentReq_Location(String recentReq_Location) {
        RecentReq_Location = recentReq_Location;
    }

    public String getRecentReq_Req_Id() {
        return RecentReq_Req_Id;
    }

    public void setRecentReq_Req_Id(String recentReq_Req_Id) {
        RecentReq_Req_Id = recentReq_Req_Id;
    }

    public String RecentReq_Name;
    public String RecentReq_Time;
    public String RecentReq_Location;
    public String RecentReq_Req_Id;



    /***********
     * Set Methods
     ******************/

    public void setAcceptedUsers(ArrayList AcceptedUsers) {
        this.AcceptedUsers = AcceptedUsers;
    }

    public void setDomnorImage(String DomnorImage) {
        this.DomnorImage = DomnorImage;
    }
    public void setDonorUserid(String userid) {
        this.userId=userid;
    }



    /***********
     * Get Methods
     ****************/

    public ArrayList<AccepteUsersModel> getAcceptedUsers() {
        return this.AcceptedUsers;
    }

    public String getDomnorImage() {
        return this.DomnorImage;
    }
    public String getDonorUserid() {
        return userId;
    }

}
