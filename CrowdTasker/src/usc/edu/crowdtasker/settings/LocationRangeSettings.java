package usc.edu.crowdtasker.settings;

import usc.edu.crowdtasker.R;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class LocationRangeSettings extends PreferenceFragment {
	
	private SeekBarPreference rangeRadiusSeekbar;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.fragment_location_range_settings);
	    rangeRadiusSeekbar = (SeekBarPreference)findPreference("pref_range_radius");
	}
}
