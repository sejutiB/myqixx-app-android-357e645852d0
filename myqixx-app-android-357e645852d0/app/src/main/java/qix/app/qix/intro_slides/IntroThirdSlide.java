package qix.app.qix.intro_slides;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.R;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.custom_views.QixViewPager;

public class IntroThirdSlide extends Fragment {

    @BindView(R.id.introJoinButton)
    Button joinButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third_slide, container, false);
        ButterKnife.bind(this, view);

        joinButton.setOnClickListener(view1 -> Objects.requireNonNull(getActivity()).finish());

        return view;
    }
}
