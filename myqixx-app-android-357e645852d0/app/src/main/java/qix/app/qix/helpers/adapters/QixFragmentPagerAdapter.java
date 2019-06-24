package qix.app.qix.helpers.adapters;

import android.location.Location;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import qix.app.qix.helpers.interfaces.QixLocationInterface;

public class QixFragmentPagerAdapter extends FragmentPagerAdapter implements QixLocationInterface {

    protected List<Fragment> mFragmentList;

    protected String data;

    public QixFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public QixFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragmentList = fragments;
    }

    @Override
    public Fragment getItem(int pos) {
        if (mFragmentList == null || mFragmentList.isEmpty()) {
            return null;
        }
        return mFragmentList.get(pos);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        if(mFragmentList == null)
            mFragmentList = new ArrayList<>();

        mFragmentList.add(fragment);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void onLocationUpdate(Location userLocation, boolean isFirstTime) {
        //Log.d("SEND", "Contacting the fragments");
        for(Fragment f : mFragmentList){
            if(f instanceof QixLocationInterface){
                ((QixLocationInterface) f).onLocationUpdate(userLocation, isFirstTime);
            }
        }
    }
}
