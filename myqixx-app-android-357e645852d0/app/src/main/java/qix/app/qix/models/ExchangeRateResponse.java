package qix.app.qix.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExchangeRateResponse {

    @SerializedName("rate")
    @Expose
    private Double rate;

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

}