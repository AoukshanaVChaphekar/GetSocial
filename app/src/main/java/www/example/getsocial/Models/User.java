package www.example.getsocial.Models;

public class User {
    String userName,mail,password,ProfilePicUrl,UserId;

    public User(String ProfilePic,String userName, String mail, String password,String UserId) {
        this.ProfilePicUrl=ProfilePic;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.UserId=UserId;
    }
    public User(){}

    //sign up constructor
    public User(String userName, String mail, String password,String profilePicUrl) {
            this.userName = userName;
            this.mail = mail;
            this.password = password;
            this.ProfilePicUrl=profilePicUrl;
        }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicUrl() {
        return ProfilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        ProfilePicUrl = profilePicUrl;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
