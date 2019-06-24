package qix.app.qix.helpers.adapters;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import qix.app.qix.fragments.recovery_password.RecoverySecondFragment;
import qix.app.qix.fragments.recovery_password.RecoveryThirdFragment;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.interfaces.RecoveryFlowInterface;

public class QixRecoveryPasswordFragmentPagerAdapter extends QixFragmentPagerAdapter implements RecoveryFlowInterface {

    public QixRecoveryPasswordFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragmentList = fragments;
    }

    @Override
    public void onEmailReceived(String email) {
        ((RecoverySecondFragment)mFragmentList.get(Constants.QIXRECOVERY_SECOND_PAGE_INDEX)).onEmailReceived(email);
    }

    @Override
    public void onRecoveryCompleted(boolean success) {
        ((RecoveryThirdFragment)mFragmentList.get(Constants.QIXRECOVERY_THIRD_PAGE_INDEX)).onRecoveryCompleted(success);
    }
}
