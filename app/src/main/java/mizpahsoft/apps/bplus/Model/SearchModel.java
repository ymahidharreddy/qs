package mizpahsoft.apps.bplus.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mizpah_DEV on 9/9/2017.
 */

public class SearchModel implements Parcelable {
    private String name;
    private String address;
    private String bloodgroup;
    private String profilePicture;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    private String distance;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.bloodgroup);
        dest.writeString(this.profilePicture);
        dest.writeString(this.userId);
        dest.writeString(this.distance);
    }

    public SearchModel() {
    }

    protected SearchModel(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.bloodgroup = in.readString();
        this.profilePicture = in.readString();
        this.userId = in.readString();
        this.distance = in.readString();
    }

    public static final Parcelable.Creator<SearchModel> CREATOR = new Parcelable.Creator<SearchModel>() {
        @Override
        public SearchModel createFromParcel(Parcel source) {
            return new SearchModel(source);
        }

        @Override
        public SearchModel[] newArray(int size) {
            return new SearchModel[size];
        }
    };
}
