package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GatewayCheckRequest {

    @SerializedName("gatewayCheck")
    @Expose
    private String gatewayCheck;

    public String getGatewayCheck() {
        return gatewayCheck;
    }

    public void setGatewayCheck(String adyenCheck) {
        this.gatewayCheck = adyenCheck;
    }

}