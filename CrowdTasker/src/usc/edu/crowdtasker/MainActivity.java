package usc.edu.crowdtasker;

import java.util.Locale;

import usc.edu.crowdtasker.NavigationDrawerFragment;
import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.provider.UserProvider;
import usc.edu.crowdtasker.settings.SettingsActivity;
import usc.edu.crowdtasker.tasker.TaskerView;
import usc.edu.crowdtasker.worker.WorkerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements 
	ActionBar.TabListener,NavigationDrawerFragment.NavigationDrawerCallbacks {

	public static final String TAG = "MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private SharedPreferences prefs;
    private User currentUser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        /*mNavigationDrawerFragment.setUp(
        R.id.navigation_drawer,
        (DrawerLayout) findViewById(R.id.drawer_layout));*/
        
    }
    
    @Override
    protected void onResume() {  
    	super.onResume();

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentUser = UserProvider.getCurrentUser(getApplicationContext());
        
        if(currentUser == null){
    		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
    		startActivity(intent);
    		finish();
    	}	
    	
    	mSectionsPagerAdapter.updateCurrentFragment();
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	mSectionsPagerAdapter.stopUpdateFragments();
    }
    
    @Override
    public void onNavigationDrawerItemSelected(int position) {
    	
    	switch(position){
	    	case NavigationDrawerFragment.NAV_PROFILE:
	    		Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
	    		startActivity(profileIntent);
	    		break;
	    	case NavigationDrawerFragment.NAV_SETTINGS:
	    		Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
	    		startActivity(settingsIntent);
	    		break;
	    	case NavigationDrawerFragment.NAV_LOGOUT:
	    		UserProvider.logoutCurrentUser(getApplicationContext());
	    		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
	    		startActivity(loginIntent);
	    		finish();
	    		break;
	    	default:
	    		break;
    	}
        // update the main content by replacing fragments
        //FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction()
        //        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
        //        .commit();
    }

    /*
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }*/
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	onNavigationDrawerItemSelected(NavigationDrawerFragment.NAV_SETTINGS);
        } else if (id == R.id.action_logout){
        	onNavigationDrawerItemSelected(NavigationDrawerFragment.NAV_LOGOUT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
    	int position = tab.getPosition();
        mViewPager.setCurrentItem(tab.getPosition());
    	mSectionsPagerAdapter.updateFragment(position);

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

    	private Fragment[] fragments;
    	private int currentPosition = 0;
    	
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new Fragment[getCount()];
        }

        @Override
        public Fragment getItem(int position) {
        	Fragment fragment;
        	if(position == 0)
        		fragment = WorkerView.newInstance();
        	else fragment = TaskerView.newInstance();
        	fragments[position] = fragment;
        	return fragment;
        }
        
        
        
        public void updateFragment(int position){
    		currentPosition = position;
    		updateCurrentFragment();
        }
        
        public void stopUpdateFragments(){
        	if(fragments != null){
	        	for(int i = 0; i < fragments.length; i++){
	        		if(fragments[i] != null){
	        			((UpdatableFragment)fragments[i]).stopUpdate();
	        		}
	    		}
        	}
        }
        
        public void updateCurrentFragment(){
        	if(currentPosition >= 0 && currentPosition < fragments.length
        			&& fragments[currentPosition] != null){
        		for(int i = 0; i < fragments.length; i++){
        			if(i == currentPosition){
                		((UpdatableFragment)fragments[i]).update();
        			}else{
                		((UpdatableFragment)fragments[i]).stopUpdate();
        			}
        		}
        	}
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.worker).toUpperCase(l);
                case 1:
                    return getString(R.string.tasker).toUpperCase(l);
            }
            return null;
        }
    }

    

}
