package qix.app.qix.models;

import com.google.android.gms.vision.label.internal.client.INativeImageLabeler;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class TransactionRequest {


    @SerializedName("fiatUserAmount")
    @Expose
    private Double fiatUserAmount;
    @SerializedName("totFiatMerchantAmount")
    @Expose
    private Double totFiatMerchantAmount;
    @SerializedName("fiatMerchantAmount")
    @Expose
    private Double fiatMerchantAmount;
    @SerializedName("fiatMerchantType")
    @Expose
    private String fiatMerchantType;
    @SerializedName("partnerPosId")
    @Expose
    private String partnerPosId;
    @SerializedName("qixMerchantReward")
    @Expose
    private Integer qixMerchantReward;

    @SerializedName("transactionId")
    @Expose
    private String transactionId;

    /* QIX Valuta utente */
    @SerializedName("qixUserAmount")
    @Expose
    private Integer qixUserAmount;

    /* QIX Valuta commerciante */
    @SerializedName("qixMerchantAmount")
    @Expose
    private Integer qixMerchantAmount;

    public TransactionRequest(HashMap<String, Object> data){
        this.qixMerchantAmount = (Integer) data.get("qixMerchantAmount");
        this.qixMerchantReward = (Integer) data.get("qixMerchantReward");
        this.fiatUserAmount = (Double) data.get("fiatUserAmount"); // EURO
        this.fiatMerchantAmount = (Double) data.get("fiatMerchantAmount");
        this.fiatMerchantType = (String) data.get("fiatMerchantType");
        this.partnerPosId = (String) data.get("partnerPosId");
        this.totFiatMerchantAmount = (Double) data.get("totFiatMerchantAmount");
        this.qixUserAmount = (Integer)data.get("qixUserAmount");
        this.transactionId = (String) data.get("transactionId");
    }

    public Integer getQixUserAmount() {
        return qixUserAmount;
    }

    public void setQixUserAmount(Integer qixUserAmount) {
        this.qixUserAmount = qixUserAmount;
    }

    public Integer getQixMerchantAmount() {
        return qixMerchantAmount;
    }

    public void setQixMerchantAmount(Integer qixMerchantAmount) {
        this.qixMerchantAmount = qixMerchantAmount;
    }

    public Double getFiatUserAmount() {
        return fiatUserAmount;
    }

    public void setFiatUserAmount(Double fiatUserAmount) {
        this.fiatUserAmount = fiatUserAmount;
    }

    public Double getTotFiatMerchantAmount() {
        return totFiatMerchantAmount;
    }

    public void setTotFiatMerchantAmount(Double totFiatMerchantAmount) {
        this.totFiatMerchantAmount = totFiatMerchantAmount;
    }

    public Double getFiatMerchantAmount() {
        return fiatMerchantAmount;
    }

    public void setFiatMerchantAmount(Double fiatMerchantAmount) {
        this.fiatMerchantAmount = fiatMerchantAmount;
    }

    public String getFiatMerchantType() {
        return fiatMerchantType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setFiatMerchantType(String fiatMerchantType) {
        this.fiatMerchantType = fiatMerchantType;
    }

    public String getPartnerPosId() {
        return partnerPosId;
    }

    public void setPartnerPosId(String partnerPosId) {
        this.partnerPosId = partnerPosId;
    }

    public Integer getQixMerchantReward() {
        return qixMerchantReward;
    }

    public void setQixMerchantReward(Integer qixMerchantReward) {
        this.qixMerchantReward = qixMerchantReward;
    }

}