package qix.app.qix.helpers.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import qix.app.qix.BuildConfig;
import qix.app.qix.helpers.Helpers;

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*int versionCode = BuildConfig.VERSION_CODE;

        if(versionCode < 20){
            Helpers.destroyAllPreferences();
        }*/
    }

}