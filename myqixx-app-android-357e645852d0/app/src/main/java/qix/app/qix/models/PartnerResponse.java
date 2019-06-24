package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PartnerResponse implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("rate_accrual")
    @Expose
    private Integer rateAccrual;
    @SerializedName("rate_redemption")
    @Expose
    private Integer rateRedemption;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("qix_currency")
    @Expose
    private String qixCurrency;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    private String category;

    private Double amount;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getRateAccrual() {
        return rateAccrual;
    }

    public void setRateAccrual(Integer rateAccrual) {
        this.rateAccrual = rateAccrual;
    }

    public Integer getRateRedemption() {
        return rateRedemption;
    }

    public void setRateRedemption(Integer rateRedemption) {
        this.rateRedemption = rateRedemption;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getQixCurrency() {
        return qixCurrency;
    }

    public void setQixCurrency(String qixCurrency) {
        this.qixCurrency = qixCurrency;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String toString(){
        return this.name + " " + this.id;
    }
}