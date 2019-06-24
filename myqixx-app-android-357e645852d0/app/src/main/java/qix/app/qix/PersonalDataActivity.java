package qix.app.qix;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import qix.app.qix.fragments.myqix.ContactDataFragment;
import qix.app.qix.fragments.myqix.PersonalDataFragment;
import qix.app.qix.helpers.Helpers;

public class PersonalDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //*  getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_personal_data);

        Intent i = getIntent();
        boolean personalDataMenuPressed = i.getBooleanExtra("personalData", true);

        if(personalDataMenuPressed){
           // setTitle("Personal Data");
            PersonalDataFragment pdf = new PersonalDataFragment();
            Helpers.switchFragment(this, pdf, R.id.personalDataLayout);
        }else{
            setTitle("Contact Data");
            ContactDataFragment cdf = new ContactDataFragment();
            Helpers.switchFragment(this, cdf, R.id.personalDataLayout);
        }
    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }*/
}
