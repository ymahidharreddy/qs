package mizpahsoft.apps.bplus.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mizpah_DEV on 9/9/2017.
 */

public class NotificationsModel implements Parcelable {
    public String getB_req_id() {
        return b_req_id;
    }

    public void setB_req_id(String b_req_id) {
        this.b_req_id = b_req_id;
    }

    public String getNotiId() {
        return notiId;
    }

    public void setNotiId(String notiId) {
        this.notiId = notiId;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotiCreatedTime() {
        return notiCreatedTime;
    }

    public void setNotiCreatedTime(String notiCreatedTime) {
        this.notiCreatedTime = notiCreatedTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    private String b_req_id;
    private String notiId;
    private String notification;
    private String name;
    private String notiCreatedTime;
    private String address;
    private String age;
    private String bloodGroup;

    public String getReq_status_id() {
        return req_status_id;
    }

    public void setReq_status_id(String req_status_id) {
        this.req_status_id = req_status_id;
    }

    public String getDonor() {
        return donor;
    }

    public void setDonor(String donor) {
        this.donor = donor;
    }

    public String getAcceptedUsers() {
        return acceptedUsers;
    }

    public void setAcceptedUsers(String acceptedUsers) {
        this.acceptedUsers = acceptedUsers;
    }

    private String req_status_id;
    private String donor;
    private String acceptedUsers;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String phoneNumber;
    private String gender;
    private String message;

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    private String profilePicture;

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    private String createdTime;

    public NotificationsModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.b_req_id);
        dest.writeString(this.notiId);
        dest.writeString(this.notification);
        dest.writeString(this.name);
        dest.writeString(this.notiCreatedTime);
        dest.writeString(this.address);
        dest.writeString(this.age);
        dest.writeString(this.bloodGroup);
        dest.writeString(this.req_status_id);
        dest.writeString(this.donor);
        dest.writeString(this.acceptedUsers);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.gender);
        dest.writeString(this.message);
        dest.writeString(this.profilePicture);
        dest.writeString(this.createdTime);
    }

    protected NotificationsModel(Parcel in) {
        this.b_req_id = in.readString();
        this.notiId = in.readString();
        this.notification = in.readString();
        this.name = in.readString();
        this.notiCreatedTime = in.readString();
        this.address = in.readString();
        this.age = in.readString();
        this.bloodGroup = in.readString();
        this.req_status_id = in.readString();
        this.donor = in.readString();
        this.acceptedUsers = in.readString();
        this.phoneNumber = in.readString();
        this.gender = in.readString();
        this.message = in.readString();
        this.profilePicture = in.readString();
        this.createdTime = in.readString();
    }

    public static final Creator<NotificationsModel> CREATOR = new Creator<NotificationsModel>() {
        @Override
        public NotificationsModel createFromParcel(Parcel source) {
            return new NotificationsModel(source);
        }

        @Override
        public NotificationsModel[] newArray(int size) {
            return new NotificationsModel[size];
        }
    };
}
