package qix.app.qix.fragments.settings;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import qix.app.qix.R;

/**
 * A simple {@link PreferenceFragmentCompat} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_list, rootKey);
    }

}
