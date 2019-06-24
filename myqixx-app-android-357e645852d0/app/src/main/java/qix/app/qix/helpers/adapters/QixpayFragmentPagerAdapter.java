package qix.app.qix.helpers.adapters;


import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayChartFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayPaymentResultFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayReviewAmountFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpaySelectFragment;
import qix.app.qix.helpers.interfaces.PaymentFlowInterface;
import qix.app.qix.models.PartnerResponse;

public class QixpayFragmentPagerAdapter extends QixFragmentPagerAdapter implements PaymentFlowInterface {

    public QixpayFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragmentList = fragments;
    }

    @Override
    public void onAmountReceived(Map<String, Object> data, PartnerResponse partner) {
        Integer fragmentIndex = (Integer)data.get("fragmentIndex");
        ((QixpaySelectFragment)mFragmentList.get(fragmentIndex)).onAmountReceived(data, partner);
    }

    @Override
    public void onSelectQixNumber(Map<String, Object> data, PartnerResponse partner) {
        Integer fragmentIndex = (Integer)data.get("fragmentIndex");
        ((QixpayChartFragment)mFragmentList.get(fragmentIndex)).onSelectQixNumber(data, partner);
    }

    @Override
    public void onPaymentResult(Map<String, Object> data, PartnerResponse partner) {
        Integer fragmentIndex = (Integer)data.get("fragmentIndex");
        ((QixpayPaymentResultFragment)mFragmentList.get(fragmentIndex)).onPaymentResult(data, partner);
    }

    @Override
    public void onFinalAmountSelected(Map<String, Object> data, PartnerResponse partner) {
        Integer fragmentIndex = (Integer)data.get("fragmentIndex");
        ((QixpayReviewAmountFragment)mFragmentList.get(fragmentIndex)).onFinalAmountSelected(data, partner);
    }

    @Override
    public void onTransactionStarted(Map<String, Object> data, PartnerResponse partner) {
        Integer fragmentIndex = (Integer)data.get("fragmentIndex");
        ((QixpayPaymentResultFragment)mFragmentList.get(fragmentIndex)).onTransactionStarted(data, partner);
    }
}
