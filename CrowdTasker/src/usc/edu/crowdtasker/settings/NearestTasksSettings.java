package usc.edu.crowdtasker.settings;

import usc.edu.crowdtasker.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class NearestTasksSettings extends PreferenceFragment {
	private SeekBarPreference nearestTasksSeekbar;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.fragment_nearest_tasks_settings);
	    nearestTasksSeekbar = (SeekBarPreference)findPreference("pref_nearest_tasks");

	}
}
