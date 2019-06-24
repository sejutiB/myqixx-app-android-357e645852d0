package qix.app.qix.helpers.adapters;


import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import qix.app.qix.fragments.sign_up.SignupFourthFragment;
import qix.app.qix.fragments.sign_up.SignupSecondFragment;
import qix.app.qix.fragments.sign_up.SignupThirdFragment;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.interfaces.SignupFlowInterface;

public class QixSignupFragmentPagerAdapter extends QixFragmentPagerAdapter implements SignupFlowInterface {

    public QixSignupFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragmentList = fragments;
    }

    @Override
    public void onFirstPageComplete(HashMap<String, String> data) {
        ((SignupSecondFragment)mFragmentList.get(Constants.QIXSIGNUP_SECOND_PAGE_INDEX)).onFirstPageComplete(data);
    }

    @Override
    public void onSecondPageComplete(HashMap<String, String> data) {
        ((SignupThirdFragment)mFragmentList.get(Constants.QIXSIGNUP_THIRD_PAGE_INDEX)).onSecondPageComplete(data);
    }

    @Override
    public void onSignupComplete(boolean success) {
        ((SignupFourthFragment)mFragmentList.get(Constants.QIXSIGNUP_FOURTH_PAGE_INDEX)).onSignupComplete(success);

    }
}
