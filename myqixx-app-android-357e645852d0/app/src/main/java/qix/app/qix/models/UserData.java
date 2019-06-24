package qix.app.qix.models;

import org.json.JSONException;
import org.json.JSONObject;

import qix.app.qix.R;

public class UserData {

    private String sub;
    private String address;
    private String birthday;
    private Boolean isEmailVerified;
    private String gender;
    private Boolean isPhoneNumberVerified;
    private String locale;
    private String phoneNumber;
    private String name;
    private String familyName;
    private String email;

    public UserData(String sub, String address, String birthday, boolean email_verified, String gender, boolean phone_number_verified, String locale, String phone_number, String family_name, String name, String email) {
        this.sub = sub;
        this.address = address;
        this.birthday = birthday;
        this.isEmailVerified = email_verified;
        this.gender = gender;
        this.isPhoneNumberVerified = phone_number_verified;
        this.locale = locale;
        this.phoneNumber = phone_number;
        this.familyName = family_name;
        this.name = name;
        this.email = email;
    }

    public UserData(JSONObject data){
        try{
            this.sub = data.getString("sub");
            this.address = data.getString("address");
            this.birthday = data.getString("birthdate");
            this.isEmailVerified = data.getBoolean("email_verified");
            this.gender = data.getString("gender");
            this.isPhoneNumberVerified = data.getBoolean("phone_number_verified");
            this.locale = data.getString("locale");
            this.phoneNumber = data.getString("phone_number");
            this.familyName = data.getString("family_name");
            this.name = data.getString("name");
            this.email = data.getString("email");
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public UserData(ProfileResponse data){
        this.sub = data.getSub();
        this.address = data.getAddress();
        this.birthday = data.getBirthdate();
        this.isEmailVerified = data.getEmailVerified();
        this.gender = data.getGender();
        this.isPhoneNumberVerified = data.getPhoneNumberVerified();
        this.locale = data.getLocale();
        this.phoneNumber = data.getPhoneNumber();
        this.familyName = data.getFamilyName();
        this.name = data.getName();
        this.email = data.getEmail();

    }

    public Integer getEmailVerified() {
        return isEmailVerified ? R.string.verified : R.string.unverified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public Integer getPhoneNumberVerified() {
        return isPhoneNumberVerified ? R.string.verified : R.string.unverified;
    }

    public void setPhoneNumberVerified(Boolean phoneNumberVerified) {
        isPhoneNumberVerified = phoneNumberVerified;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public static int numberOfVisibleFields(){
        return 9;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
