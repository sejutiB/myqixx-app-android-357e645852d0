package qix.app.qix.fragments.myqix;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import qix.app.qix.AboutActivity;
import qix.app.qix.FAQActivity;
import qix.app.qix.PersonalDataActivity;
import qix.app.qix.R;
import qix.app.qix.SettingsActivityList;
import qix.app.qix.TermActivity;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Configuration;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.ProfileResponse;
import qix.app.qix.models.SuccessResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link MyQixListFragment} subclass.
 */
public class MyQixListFragment extends Fragment {

    private static Integer[] LIST_ITEMS = {
            R.string.myqix_item_profile,
            R.string.myqix_item_term,
            R.string.myqix_item_privacy,
            R.string.myqix_item_faq,
           //* R.string.myqix_item_about,
           //* R.string.myqix_item_signout,
            R.string.settings
    };

    private static Integer[] LIST_ICONS = {
            R.drawable.arrow_gray,
            R.drawable.arrow_gray,
            R.drawable.arrow_gray,
            R.drawable.arrow_gray,
            R.drawable.arrow_gray,
          //*  R.drawable.arrow_gray,
          //*  R.drawable.arrow_gray
    };

    private static Integer[] LIST_CHILD_ICONS = {
            R.drawable.user_data_list_icon,
            R.drawable.contact_list_icon
    };

    private LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<>();
    private LinkedHashMap<String, List<Integer>> expandableListChildsImage = new LinkedHashMap<>();

    @BindView(R.id.profileIImageView)
    ImageView profileImage;

    @BindView(R.id.client_name)
    TextView profileName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myqix, container, false);
        getUserData();
        ButterKnife.bind(this, view);

        setHasOptionsMenu(false);

        this.setProfileImage();

        profileImage.setOnClickListener(view1 -> PickImageDialog.build(new PickSetup()).setOnPickResult(pickResult -> {
            if (pickResult.getError() == null) {

                //Setting the real returned image.
                File file = new File(pickResult.getPath());

                String ext = MimeTypeMap.getFileExtensionFromUrl(pickResult.getPath());

                // create RequestBody instance from file
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/" + ext), file);


                AsyncRequest.uploadUserImage(getActivity(), ext, requestFile, new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SuccessResponse> call, @NonNull Response<SuccessResponse> response) {
                        if (response.isSuccessful()) {

                            //SuccessResponse result = response.body();
                            profileImage.setImageURI(pickResult.getUri());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SuccessResponse> call, @NonNull Throwable t) {
                        Helpers.presentToast("Error uploading the image", Toast.LENGTH_SHORT);
                    }
                });

            } else {
                //Handle possible errors
                //TODO: do what you have to do with r.getError();
                Helpers.presentToast(pickResult.getError().getMessage(), Toast.LENGTH_LONG);
            }
        }).setOnPickCancel(() -> {
            //TODO: do what you have to if user clicked cancel
        }).show(Objects.requireNonNull(getActivity()).getSupportFragmentManager()));

        generateList();

        ExpandableListView expandableListView = view.findViewById(R.id.expandableListView);


        final List<Integer> expandableListTitle = Arrays.asList(LIST_ITEMS);
        final List<Integer> expandableListIcon = Arrays.asList(LIST_ICONS);

        MyQixExpandableListAdapter expandableListAdapter = new MyQixExpandableListAdapter(getContext(), expandableListTitle, expandableListIcon, expandableListChildsImage, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupExpandListener(groupPosition -> {
            HashMap<String, Boolean> h = new HashMap<>();
            // TODO: adding elements activities
            switch (groupPosition) {
                case 0:
                    Helpers.startNewActivity(PersonalDataActivity.class, false, true, h);
                    break;
                case 1:
                    h.put("isTerms", true);
                    Helpers.startNewActivity(TermActivity.class, false, true, h);
                    break;
                case 2:
                    h.put("isTerms", false);
                    Helpers.startNewActivity(TermActivity.class, false, true, h);
                    break;
                case 3:
                    Helpers.startNewActivity(FAQActivity.class, false, true, h);
                    break;
                case 4:
                    Helpers.startNewActivity(SettingsActivityList.class, false, true, h);
                    break;
                /*case 5:
                    Helpers.presentSimpleDialog(getActivity(), "", "Are you sure you want to logout?", "Yes", "No", true, (dialogInterface, i) -> {
                        dialogInterface.cancel();

                        Helpers.logout();
                        Objects.requireNonNull(getActivity()).finish();
                    }, (dialogInterface, i) -> dialogInterface.dismiss());
                    break;*/
            }
        });

       /* expandableListView.setOnChildClickListener((parent, view12, groupPosition, childPosition, id) -> {
            // TODO: adding all buttons functions
            if (groupPosition == 0) {
                HashMap<String, Boolean> data = new HashMap<>();
                boolean isPersonal = false;
                if (childPosition == 0) {
                    isPersonal = true;
                }
                data.put("personalData", isPersonal);
                Helpers.startNewActivity(PersonalDataActivity.class, false, true, data);
            }

            return false;
        });*/

        return view;
    }

    private void setProfileImage() {
        AsyncRequest.getProfileData(getActivity(), new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse result = response.body();
                    assert result != null;
                    Configuration.setUserId(result.getSub());
                    Picasso.get().load(result.getImageUrl()).error(R.drawable.blankprofile).into(profileImage);
                } else {
                    Helpers.presentToast("Error in uploading picture", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Helpers.presentToast("Failure: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    private void generateList() {
        List<String> profile = new ArrayList<>();
        profile.add(getResources().getString(R.string.myqix_child_personal));
        profile.add(getResources().getString(R.string.myqix_child_contact));

        List<Integer> icons = Arrays.asList(LIST_CHILD_ICONS);
        expandableListChildsImage.put(getResources().getString(LIST_ITEMS[0]), icons);

        expandableListDetail.put(getResources().getString(LIST_ITEMS[0]), profile);
        expandableListDetail.put(getResources().getString(LIST_ITEMS[1]), new ArrayList<>());
        expandableListDetail.put(getResources().getString(LIST_ITEMS[2]), new ArrayList<>());
        expandableListDetail.put(getResources().getString(LIST_ITEMS[3]), new ArrayList<>());
        expandableListDetail.put(getResources().getString(LIST_ITEMS[4]), new ArrayList<>());
       //* expandableListDetail.put(getResources().getString(LIST_ITEMS[5]), new ArrayList<>());

    }

    private void getUserData() {
        AsyncRequest.getProfileData(getActivity(), new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse p = response.body();

                    assert p != null;
                    // nameEditText.setText(p.getName());
                    // surnameEditText.setText(p.getFamilyName());
                    profileName.setText(String.format("%s %s", p.getName(), p.getFamilyName()));

                } else {
                    Helpers.presentToast("Check your connection", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Helpers.presentToast("Check your connection", Toast.LENGTH_SHORT);
            }
        });
    }

}
