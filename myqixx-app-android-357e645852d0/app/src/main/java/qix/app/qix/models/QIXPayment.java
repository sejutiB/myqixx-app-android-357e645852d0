package qix.app.qix.models;

import com.google.android.gms.common.util.ArrayUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class QIXPayment implements Serializable {

    public enum PaymentType{
        QIXNOW,
        QIXPAY
    }

    private String qixMerchantAmount;
    private String partnerPosId;
    private String timestamp;
    private String transactionId;
    private PaymentType paymentType;


    public QIXPayment(String data){
        ArrayList<String> d = ArrayUtils.toArrayList(data.split("\\^"));
        this.qixMerchantAmount = d.get(0);
        this.partnerPosId = d.get(1);
        this.timestamp = d.get(2);
        this.transactionId = d.get(3);
        this.paymentType = d.get(4).equals("0") ? PaymentType.QIXNOW : PaymentType.QIXPAY;
    }

    public String getQixMerchantAmount() {
        return qixMerchantAmount;
    }

    public void setQixMerchantAmount(String qixMerchantAmount) {
        this.qixMerchantAmount = qixMerchantAmount;
    }

    public String getPartnerPosId() {
        return partnerPosId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public void setPartnerPosId(String partnerPosId) {
        this.partnerPosId = partnerPosId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public HashMap<String, String> toMap(){
        HashMap<String, String> requestData = new HashMap<>();
        requestData.put("qixMerchantAmount", getQixMerchantAmount());
        requestData.put("partnerPosId", getPartnerPosId());
        requestData.put("timestamp", getTimestamp());
        requestData.put("transactionId", getTransactionId());

        return requestData;
    }
}
