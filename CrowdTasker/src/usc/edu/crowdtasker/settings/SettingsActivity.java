package usc.edu.crowdtasker.settings;

import java.util.List;

import usc.edu.crowdtasker.R;

import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return LocationRangeSettings.class.getName().equals(fragmentName);
    }
}
