package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class ShakeRequest {

    @SerializedName("watermark")
    @Expose
    private Integer watermark;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public ShakeRequest(HashMap<String, String> data){
        this.watermark = Integer.valueOf(data.get("watermark"));
        this.latitude = Double.valueOf(data.get("latitude"));
        this.longitude = Double.valueOf(data.get("longitude"));
    }

    public Integer getWatermark() {
        return watermark;
    }

    public void setWatermark(Integer watermark) {
        this.watermark = watermark;
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

}