package qix.app.qix.helpers.adapters;


import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import qix.app.qix.fragments.qixbuy.QixBuyResultFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayChartFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayPaymentResultFragment;
import qix.app.qix.fragments.qixpay.payment_flow.QixpayReviewAmountFragment;
import qix.app.qix.helpers.interfaces.PaymentFlowInterface;
import qix.app.qix.helpers.interfaces.QixBuyFlowInterface;

public class QixbuyFragmentPagerAdapter extends QixFragmentPagerAdapter implements QixBuyFlowInterface {

    public QixbuyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragmentList = fragments;
    }

    @Override
    public void onDataReceived(Map<String, Object> data) {
        Integer fragmentIndex = (Integer)data.get("fragmentIndex");
        ((QixBuyResultFragment)mFragmentList.get(fragmentIndex)).onDataReceived(data);
    }
}
