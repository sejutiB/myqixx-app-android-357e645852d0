package qix.app.qix.models;

import java.io.Serializable;

public class QixShop implements Serializable{

    private String id;
    private String name;
    private String street;
    private String category;
    private Integer iconId;
    private Integer distance;
    private Double amount;
    private String partner;
    private Double latitude;
    private Double longitude;
    private Integer rateAccrual;
    private Integer rateRedemption;
    private String currency;
    private String qixCurrency;

    public QixShop(String id, String name, String street, String category, Integer iconId, Integer distance, Double amount, String partner, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.category = category;
        this.iconId = iconId;
        this.distance = distance;
        this.amount = amount;
        this.partner = partner;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public QixShop(String id, String name, String street, String category, Integer resourceId, Integer distance,  Double amount, String partner) {
        this(id, name, street, category, resourceId, distance, amount, partner, null, null);
    }

    public QixShop(String id, String name, String street, String category, Integer resourceId, Integer distance) {
        this(id, name, street, category, resourceId, distance, null, null);
    }

    public QixShop(String id, String name, String street, String category, Integer resourceId) {
        this(id, name, street, category, resourceId,null, null, null);
    }

    public QixShop(String id, String name, String street, String category) {
        this(id, name, street, category, null, null, null, null);
    }

    public QixShop(){
        this(null,null, null, null);
    }


    public QixShop(Double amount, String partner) {
        this();
        this.amount = amount;
        this.partner = partner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getIconId() {
        return iconId;
    }

    public void setIconId(Integer iconId) {
        this.iconId = iconId;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
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

    @Override
    public String toString() {
        return this.name+" "+this.category;
    }


}
