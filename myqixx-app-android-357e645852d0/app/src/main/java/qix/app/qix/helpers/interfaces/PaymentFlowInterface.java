package qix.app.qix.helpers.interfaces;

import java.util.Map;

import qix.app.qix.models.PartnerResponse;

public interface PaymentFlowInterface {
    void onAmountReceived(Map<String, Object> data, PartnerResponse partner);
    void onSelectQixNumber(Map<String, Object> data, PartnerResponse partner);
    void onPaymentResult(Map<String, Object> data, PartnerResponse partner);
    void onFinalAmountSelected(Map<String, Object> data, PartnerResponse partner);
    void onTransactionStarted(Map<String, Object> data, PartnerResponse partnerResponse);
}
