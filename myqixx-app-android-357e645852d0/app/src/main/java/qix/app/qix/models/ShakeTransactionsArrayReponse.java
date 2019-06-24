package qix.app.qix.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShakeTransactionsArrayReponse {

    @SerializedName("wins")
    @Expose
    private List<ShakeTransaction> transactionList = null;
    @SerializedName("lastEvaluatedKey")
    @Expose
    private String lastEvaluatedKey;

    public List<ShakeTransaction> getTransactions() {
        return transactionList;
    }

    public void setTransactions(List<ShakeTransaction> transactionList) {
        this.transactionList = transactionList;
    }

    public String getLastEvaluatedKey() {
        return lastEvaluatedKey;
    }

    public void setLastEvaluatedKey(String lastEvaluatedKey) {
        this.lastEvaluatedKey = lastEvaluatedKey;
    }
}
