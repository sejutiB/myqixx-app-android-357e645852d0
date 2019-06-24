package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecoveryPasswordRequest {

    @SerializedName("email")
    @Expose
    private String email;

    public RecoveryPasswordRequest(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
