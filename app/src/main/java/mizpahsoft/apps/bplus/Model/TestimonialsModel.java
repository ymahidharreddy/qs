package mizpahsoft.apps.bplus.Model;

/**
 * Created by Mizpah_DEV on 9/9/2017.
 */

public class TestimonialsModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    private String name, message, profilePicture;
}
