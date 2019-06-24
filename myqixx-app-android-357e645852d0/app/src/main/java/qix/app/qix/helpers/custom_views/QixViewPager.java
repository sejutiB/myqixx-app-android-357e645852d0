package qix.app.qix.helpers.custom_views;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import qix.app.qix.helpers.interfaces.QixLocationInterface;

public class QixViewPager extends ViewPager {

    private Boolean disable = false;

    public QixViewPager(@NonNull Context context) {
        super(context);
    }

    public QixViewPager(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !disable && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !disable && super.onTouchEvent(event);
    }

    public void disableSwipe(Boolean disable){
        this.disable = disable;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }
}

