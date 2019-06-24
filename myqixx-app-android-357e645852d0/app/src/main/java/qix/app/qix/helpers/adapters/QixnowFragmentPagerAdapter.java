package qix.app.qix.helpers.adapters;


import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayChartFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayPaymentResultFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayReviewAmountFragment;
import qix.app.qix.helpers.interfaces.PaymentFlowInterface;
import qix.app.qix.helpers.interfaces.QixnowFlowInterface;
import qix.app.qix.models.PartnerResponse;

public class QixnowFragmentPagerAdapter extends QixFragmentPagerAdapter implements QixnowFlowInterface {

    public QixnowFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragmentList = fragments;
    }

    @Override
    public void onDataReceived(Map<String, Object> data, PartnerResponse partner) {
        Integer fragmentIndex = (Integer)data.get("fragmentIndex");
        ((QixpayChartFragment)mFragmentList.get(fragmentIndex)).onAmountReceived(data, partner);
    }

}
