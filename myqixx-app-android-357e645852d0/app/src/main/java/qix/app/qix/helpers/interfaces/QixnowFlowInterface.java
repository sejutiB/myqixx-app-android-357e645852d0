package qix.app.qix.helpers.interfaces;

import java.util.Map;

import qix.app.qix.models.PartnerResponse;

public interface QixnowFlowInterface {
    void onDataReceived(Map<String, Object> data, PartnerResponse partner);
}
