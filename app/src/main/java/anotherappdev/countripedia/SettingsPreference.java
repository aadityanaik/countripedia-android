package anotherappdev.countripedia;

import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsPreference extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        SwitchPreference switchPreference = (SwitchPreference) findPreference("prefTheme");
        switchPreference.setOnPreferenceChangeListener(new android.support.v7.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object newValue) {
                CountrySearchActivity.startInSettings = true;
                getActivity().recreate();
                return true;
            }
        });
    }
}
