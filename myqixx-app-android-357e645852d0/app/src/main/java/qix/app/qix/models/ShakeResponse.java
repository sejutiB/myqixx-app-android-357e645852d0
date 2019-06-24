package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShakeResponse {

    @SerializedName("win")
    @Expose
    private boolean win;

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("winConsolation")
    @Expose
    private boolean winConsolation;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("qix")
    @Expose
    private Integer qix;
    @SerializedName("qixCurrency")
    @Expose
    private String qixCurrency;
    @SerializedName("prizeCount")
    @Expose
    private Integer prizeCount;
    @SerializedName("consolation")
    @Expose
    private boolean consolation;

    @SerializedName("image")
    @Expose
    private String imageLink;
    @SerializedName("userMessage")
    @Expose
    private String userMessage;

    public boolean isSuccessfull() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public boolean hasWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean getWinConsolation() {
        return winConsolation;
    }

    public void setWinConsolation(boolean winConsolation) {
        this.winConsolation = winConsolation;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getQix() {
        return qix;
    }

    public void setQix(Integer qix) {
        this.qix = qix;
    }

    public String getQixCurrency() {
        return qixCurrency;
    }

    public void setQixCurrency(String qixCurrency) {
        this.qixCurrency = qixCurrency;
    }

    public Integer getPrizeCount() {
        return prizeCount;
    }

    public void setPrizeCount(Integer prizeCount) {
        this.prizeCount = prizeCount;
    }

    public boolean getConsolation() {
        return consolation;
    }

    public void setConsolation(boolean consolation) {
        this.consolation = consolation;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

}

