package qix.app.qix.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfferResponse implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
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
    @SerializedName("days_after_birthday")
    @Expose
    private Integer daysAfterBirthday;
    @SerializedName("days_before_birthday")
    @Expose
    private Integer daysBeforeBirthday;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("enable_non_fixed_bonus")
    @Expose
    private Boolean enableNonFixedBonus;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("fixed_bonus")
    @Expose
    private Integer fixedBonus;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("max_age")
    @Expose
    private Integer maxAge;
    @SerializedName("partner_id")
    @Expose
    private String partnerId;
    @SerializedName("radius")
    @Expose
    private Integer radius;
    @SerializedName("show_only_on_user_birthday")
    @Expose
    private Boolean showOnlyOnUserBirthday;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("to")
    @Expose
    private String to;
    @SerializedName("weekdays")
    @Expose
    private List<String> weekdays = null;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("gift")
    @Expose
    private String gift;
    @SerializedName("promo_code")
    @Expose
    private String promoCode;
    @SerializedName("budget")
    @Expose
    private Integer budget;
    @SerializedName("qix_currency")
    @Expose
    private String qixCurrency;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("offer_image_url")
    @Expose
    private String offerImageUrl;
    @SerializedName("partner_image_url")
    @Expose
    private String partnerImageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getDaysAfterBirthday() {
        return daysAfterBirthday;
    }

    public void setDaysAfterBirthday(Integer daysAfterBirthday) {
        this.daysAfterBirthday = daysAfterBirthday;
    }

    public Integer getDaysBeforeBirthday() {
        return daysBeforeBirthday;
    }

    public void setDaysBeforeBirthday(Integer daysBeforeBirthday) {
        this.daysBeforeBirthday = daysBeforeBirthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Boolean getEnableNonFixedBonus() {
        return enableNonFixedBonus;
    }

    public void setEnableNonFixedBonus(Boolean enableNonFixedBonus) {
        this.enableNonFixedBonus = enableNonFixedBonus;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getFixedBonus() {
        return fixedBonus;
    }

    public void setFixedBonus(Integer fixedBonus) {
        this.fixedBonus = fixedBonus;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Boolean getShowOnlyOnUserBirthday() {
        return showOnlyOnUserBirthday;
    }

    public void setShowOnlyOnUserBirthday(Boolean showOnlyOnUserBirthday) {
        this.showOnlyOnUserBirthday = showOnlyOnUserBirthday;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<String> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(List<String> weekdays) {
        this.weekdays = weekdays;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
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

    public String getOfferImageUrl() {
        return offerImageUrl;
    }

    public void setOfferImageUrl(String offerImageUrl) {
        this.offerImageUrl = offerImageUrl;
    }

    public String getPartnerImageUrl() {
        return partnerImageUrl;
    }

    public void setPartnerImageUrl(String partnerImageUrl) {
        this.partnerImageUrl = partnerImageUrl;
    }

}