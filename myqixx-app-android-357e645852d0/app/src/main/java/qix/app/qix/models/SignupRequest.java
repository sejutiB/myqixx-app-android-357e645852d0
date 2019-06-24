package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class SignupRequest {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("address_street")
    @Expose
    private String addressStreet;
    @SerializedName("address_city")
    @Expose
    private String addressCity;
    @SerializedName("address_country")
    @Expose
    private String addressCountry;
    @SerializedName("address_postcode")
    @Expose
    private String addressPostcode;
    @SerializedName("birthdate")
    @Expose
    private String birthdate;
    @SerializedName("family_name")
    @Expose
    private String familyName;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("qixCurrency")
    @Expose
    private String qixCurrency;

    public SignupRequest(HashMap<String, String> data){
        email = data.get("email");
        addressStreet = data.get("address_street");
        addressCity = data.get("address_city");
        addressCountry = data.get("address_country");
        addressPostcode = data.get("address_postcode");
        birthdate = data.get("birthdate");
        familyName = data.get("family_name");
        gender = data.get("gender");
        locale = data.get("locale");
        middleName = data.get("middle_name");
        name = data.get("name");
        phoneNumber = data.get("phone_number");
        password = data.get("password");
        qixCurrency = data.get("qixCurrency");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getAddressPostcode() {
        return addressPostcode;
    }

    public void setAddressPostcode(String addressPostcode) {
        this.addressPostcode = addressPostcode;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQixCurrency() {
        return qixCurrency;
    }

    public void setQixCurrency(String qixCurrency) {
        this.qixCurrency = qixCurrency;
    }

}