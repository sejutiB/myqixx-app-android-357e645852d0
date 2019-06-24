package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class PaypalTransactionRequest {

    @SerializedName("orderId")
    @Expose
    private String orderId;


    public PaypalTransactionRequest(String orderId){
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}