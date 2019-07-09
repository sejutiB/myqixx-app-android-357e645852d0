package qix.app.qix;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cifrasoft.services.SoundCode;


import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.helpers.Configuration;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Helpers;
import zendesk.core.Identity;
import zendesk.core.JwtIdentity;
import zendesk.core.Zendesk;
import zendesk.support.Support;
import zendesk.support.request.RequestActivity;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.callButton)
    Button callButton;

   //* @BindView(R.id.emailButton)
    //*Button emailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getResources().getString(R.string.activity_info_title));
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        Zendesk.INSTANCE.init(this,
                Constants.ZENDESK_ENDPOINT,
                Constants.ZENDESK_APP_ID,
                Constants.ZENDESK_CLIENT_ID);

        Support.INSTANCE.init(Zendesk.INSTANCE);

        Identity identity = new JwtIdentity(Configuration.getIdToken());
        Zendesk.INSTANCE.setIdentity(identity);

        Support.INSTANCE.init(Zendesk.INSTANCE);


        callButton.setOnClickListener(this);
       //* emailButton.setOnClickListener(this);
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            SoundCode.release();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.qixchangeButton:
                break;
            case R.id.callButton:
                RequestActivity.builder()
                        .withRequestSubject("QIX Bug Report-User: "+ Helpers.getPreference(R.string.preference_user_id))
                        .withTags("Android", "testing")
                        .show(this);
                break;
           //* case R.id.emailButton:
              //*  break;
        }
    }
}
