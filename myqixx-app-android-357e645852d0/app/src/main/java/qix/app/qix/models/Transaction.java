package qix.app.qix.models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;

public class Transaction implements Serializable {

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("qixUserReward")
    @Expose
    private Integer qixUserReward;
    @SerializedName("qixUserAmount")
    @Expose
    private Integer qixUserAmount;
    @SerializedName("fiatUserAmount")
    @Expose
    private Double fiatUserAmount;
    @SerializedName("fiatUserType")
    @Expose
    private String fiatUserType;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("stateTransaction")
    @Expose
    private String stateTransaction;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getQixUserReward() {
        return qixUserReward;
    }

    public void setQixUserReward(Integer qixUserReward) {
        this.qixUserReward = qixUserReward;
    }

    public Integer getQixUserAmount() {
        return qixUserAmount;
    }

    public void setQixUserAmount(Integer qixUserAmount) {
        this.qixUserAmount = qixUserAmount;
    }

    public Double getFiatUserAmount() {
        return fiatUserAmount;
    }

    public void setFiatUserAmount(Double fiatUserAmount) {
        this.fiatUserAmount = fiatUserAmount;
    }

    public String getFiatUserType() {
        return fiatUserType;
    }

    public void setFiatUserType(String fiatUserType) {
        this.fiatUserType = fiatUserType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStateTransaction() {
        return stateTransaction;
    }

    public void setStateTransaction(String stateTransaction) {
        this.stateTransaction = stateTransaction;
    }

    public Constants.TransactionType getType() {
        switch(type){
            case "pay":
                return Constants.TransactionType.PAY;
            case "buy":
                return Constants.TransactionType.BUY;
            case "travel":
                return Constants.TransactionType.TRAVEL;
            case "shake":
                return  Constants.TransactionType.SHAKE;
            case "pay_in_store":
                return Constants.TransactionType.PAY_IN_STORE;
            case "wait_for_approval":
                return Constants.TransactionType.WAIT_FOR_APPROVAL;
            case "marketplace":
                return Constants.TransactionType.MARKETPLACE;
        }
        return null;
    }

    public void setType(Constants.TransactionType type) {
        String temp = null;
        switch(type){
            case PAY:
                temp = "pay";
                break;
            case BUY:
                temp = "buy";
                break;
            case TRAVEL:
                temp = "travel";
                break;
            case SHAKE:
                temp = "shake";
                break;
        }
        this.type = temp;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public ArrayList<Constants.TransactionData> getData() throws ParseException {

        ArrayList<Constants.TransactionData> data = new ArrayList<>();

        String nameString;
        switch(getType()){

            case BUY:
                nameString = "QIXBUY";
                break;
            case TRAVEL:
                nameString = "QIXTRAVEL";
                break;
            case MARKETPLACE:
                nameString = "QIXMARKETPLACE";
                break;
             default:
                 nameString = name;
        }

        data.add(new Constants.TransactionData("Partner Name", nameString));
        data.add(new Constants.TransactionData("State", stateTransaction));
        data.add(new Constants.TransactionData("Type", type));
        data.add(new Constants.TransactionData("Identifier", transactionId));
        data.add(new Constants.TransactionData("Date", Helpers.getStringFrom(Helpers.getDateFrom(timestamp, "yyyy-MM-dd'T'HH:mm:ss.sss'Z'"), "dd/MM/yyyy HH:mm")));

        if(!getStateTransaction().equals("REJECTED")){
            if(getType() != Constants.TransactionType.SHAKE && getType() != Constants.TransactionType.BUY){
                data.add(new Constants.TransactionData("Amount", fiatUserAmount+""));
            }

            if(getType() == Constants.TransactionType.SHAKE){
                if(getQixUserReward() != null){
                    data.add(new Constants.TransactionData("QIX Amount", "+"+qixUserReward));
                }
            }else{
                if(getQixUserAmount() != null){
                    String value;
                    if(getType() == Constants.TransactionType.PAY_IN_STORE && getQixUserAmount() == 0){
                        value = "+" + qixUserReward;
                    }else{
                        String sign;
                        int total;
                        if(getQixUserAmount() > 0){
                            sign = getType() == Constants.TransactionType.BUY ? "+" : "-";
                            total = getQixUserAmount();
                        }else{
                            sign = "+";
                            total = getQixUserReward();
                        }

                        value = sign + total + " QIX";
                    }
                   data.add(new Constants.TransactionData("QIX Amount", value));
                }
            }
        }

        return data;
    }
}
