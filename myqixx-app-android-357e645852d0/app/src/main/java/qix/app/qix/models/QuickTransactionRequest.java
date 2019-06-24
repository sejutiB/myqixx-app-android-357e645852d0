package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class QuickTransactionRequest {

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("partnerPosId")
    @Expose
    private String partnerPosId;

    @SerializedName("transactionId")
    @Expose
    private String transactionId;

    @SerializedName("qixMerchantAmount")
    @Expose
    private String qixMerchantAmount;



    public QuickTransactionRequest(HashMap<String, String> data){
        this.timestamp = (String) data.get("timestamp");
        this.partnerPosId = (String) data.get("partnerPosId");
        this.transactionId = (String) data.get("transactionId");
        this.qixMerchantAmount = (String) data.get("qixMerchantAmount");
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPartnerPosId() {
        return partnerPosId;
    }

    public void setPartnerPosId(String partnerPosId) {
        this.partnerPosId = partnerPosId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getQixMerchantAmount() {
        return qixMerchantAmount;
    }

    public void setQixMerchantAmount(String qixMerchantAmount) {
        this.qixMerchantAmount = qixMerchantAmount;
    }
}
