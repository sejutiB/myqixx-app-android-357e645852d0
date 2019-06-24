package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShakeTransaction {
    @SerializedName("qixCurrency")
    @Expose
    private String qixCurrency;
    @SerializedName("prizeTitle")
    @Expose
    private String prizeTitle;
    @SerializedName("timeShakeWin")
    @Expose
    private String timeShakeWin;
    @SerializedName("prizeDescription")
    @Expose
    private String prizeDescription;
    @SerializedName("qix")
    @Expose
    private Integer qix;
    @SerializedName("idCampaign")
    @Expose
    private String idCampaign;

    public String getQixCurrency() {
        return qixCurrency;
    }

    public void setQixCurrency(String qixCurrency) {
        this.qixCurrency = qixCurrency;
    }

    public String getPrizeTitle() {
        return prizeTitle;
    }

    public void setPrizeTitle(String prizeTitle) {
        this.prizeTitle = prizeTitle;
    }

    public String getTimeShakeWin() {
        return timeShakeWin;
    }

    public void setTimeShakeWin(String timeShakeWin) {
        this.timeShakeWin = timeShakeWin;
    }

    public String getPrizeDescription() {
        return prizeDescription;
    }

    public void setPrizeDescription(String prizeDescription) {
        this.prizeDescription = prizeDescription;
    }

    public Integer getQix() {
        return qix;
    }

    public void setQix(Integer qix) {
        this.qix = qix;
    }

    public String getIdCampaign() {
        return idCampaign;
    }

    public void setIdCampaign(String idCampaign) {
        this.idCampaign = idCampaign;
    }
}
