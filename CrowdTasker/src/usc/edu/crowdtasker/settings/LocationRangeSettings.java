package usc.edu.crowdtasker.settings;

import usc.edu.crowdtasker.R;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class LocationRangeSettings extends PreferenceFragment {
	
	private SeekBarPreference loadRangeSeekbar;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.fragment_location_range_settings);
	    
	    
	    loadRangeSeekbar = (SeekBarPreference)findPreference("load_range_seekbar");
	    /*loadRangeSeekbar.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
			}
		});*/
	    
	}
}
