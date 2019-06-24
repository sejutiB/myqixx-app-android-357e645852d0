package qix.app.qix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Configuration;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.ProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivityList extends AppCompatActivity {

    private RoundedImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_settings_list);
        setTitle("Settings");
        profileImage= findViewById(R.id.profileIImageView2);
        profileImage.setImageResource(R.drawable.blankprofile);
        this.setProfileImage();
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void goToAboutActivity(View view) {
        Helpers.startNewActivity(AboutActivity.class);
    }

    public void goToShakeTutorial(View view) {
        Helpers.startNewActivity(ShakeTutorial.class);
    }

    public void signout(View view) {
        Helpers.presentSimpleDialog(this, "", "Are you sure you want to logout?", "Yes", "No", true, (dialogInterface, i) -> {
            dialogInterface.cancel();

            Helpers.logout();
            Intent intent = new Intent(SettingsActivityList.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
           // setResult(5);
        }, (dialogInterface, i) -> dialogInterface.dismiss());
    }

    private void setProfileImage() {
        AsyncRequest.getProfileData(this, new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse result = response.body();
                    assert result != null;
                    Configuration.setUserId(result.getSub());
                    Picasso.get().load(result.getImageUrl()).error(R.drawable.blankprofile).into(profileImage);
                } else {
                    Helpers.presentToast(" ", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Helpers.presentToast("Failure: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT);
            }
        });
    }
}
