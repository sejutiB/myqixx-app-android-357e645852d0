package qix.app.qix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import qix.app.qix.R;

public class ShakeResultFragment extends Fragment {

    private static final String TAG = "ShakeFragment";

    public ShakeResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shake_result, container, false);

        assert getArguments() != null;
        Boolean success = getArguments().getBoolean("success");
        String imageUrl = getArguments().getString("image");
        Integer code = getArguments().getInt("code");
        getActivity().setTitle(success ? "Congratulations!" : " ");

        ImageView shakeImage =  view.findViewById(R.id.shakeResultImage);
        TextView shakeTextMessage = view.findViewById(R.id.shakeMessageText);

        if(imageUrl != null)
            Picasso.get().load(imageUrl).error(success ? R.drawable.trophy : R.drawable.sorry_shake).into(shakeImage);

        switch (code) {
            case 200:
                String desc = getArguments().getString("description");
                shakeTextMessage.setText(desc);
                break;
            case 201:
                shakeImage.setImageResource(R.drawable.sorry_shake);
                break;
            case 404:
                shakeImage.setImageResource(R.drawable.not_found_shake);
                break;
            case 406:
                shakeImage.setImageResource(R.drawable.already_won);
                break;
            case 410:
                shakeImage.setImageResource(R.drawable.sorry_shake);
                break;
            case 420:
                shakeImage.setImageResource(R.drawable.sorry_no_more_tries_left);
                break;
            case 500:
                shakeImage.setImageResource(R.drawable.sorry_shake);
                break;
            default:
                shakeImage.setImageResource(R.drawable.sorry_shake);
                break;
        }

        return view;
    }

}
