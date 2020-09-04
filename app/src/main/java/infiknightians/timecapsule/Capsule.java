package infiknightians.timecapsule;

import java.util.Date;

public class Capsule {

    private String userId, name;
    private String capsule;
    private String date;

    public Capsule() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapsule() {
        return capsule;
    }

    public void setCapsule(String capsule) {
        this.capsule = capsule;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void sendMail(int Key)
    {
        String mail = this.userId;
        String subject = "NITC Guesthoust Booking";
        String message = "";
        message = "Your secret message has been successfully saved in database.\nPlease save the private key to Decrypt it at correct time.\nYour private key is : " + Key + "\nPlease don't share it with anyone.\n\n Time Capsule Admin\nINFIKNIGHTIANS";
        JavaMailAPI javaMailAPI = new JavaMailAPI(mail, subject, message);
        javaMailAPI.execute();

    }


    public String toString()
    {
        return String.format("\n" + name + " :\nWrote On:\n" + date+ "\n");
    }
}
