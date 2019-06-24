package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionStatusResponse {

    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean isSuccessful() {
        return success;
    }
}