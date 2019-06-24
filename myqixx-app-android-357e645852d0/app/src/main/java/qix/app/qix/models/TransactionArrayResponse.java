package qix.app.qix.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionArrayResponse {

    @SerializedName("transactions")
    @Expose
    private List<Transaction> transactions = null;
    @SerializedName("lastEvaluatedKey")
    @Expose
    private String lastEvaluatedKey;

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getLastEvaluatedKey() {
        return lastEvaluatedKey;
    }

    public void setLastEvaluatedKey(String lastEvaluatedKey) {
        this.lastEvaluatedKey = lastEvaluatedKey;
    }

}