package qix.app.qix.fragments.shaketutorial;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import qix.app.qix.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class shaketutorial_fourth extends Fragment {

    public shaketutorial_fourth() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_shaketutorial_fourth, container, false);
        Button next = view.findViewById(R.id.nextshake3);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return view;
    }

}
