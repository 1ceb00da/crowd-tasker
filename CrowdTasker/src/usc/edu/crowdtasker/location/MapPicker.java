package usc.edu.crowdtasker.location;

import java.io.IOException;
import java.util.List;

import usc.edu.crowdtasker.R;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapPicker extends FragmentActivity 
		implements LocationListener, OnMapClickListener {
	
	public static final String TAG = "MapPicker";
	
	public static final String LOCATION  = "LOC";
	public static final String LOCATION_ADDRESS  = "LOC_ADDRESS";
	public static final String TITLE = "TITLE";

	private static final long MIN_TIME = 400;
	private static final float MIN_DISTANCE = 1000;
	
	private GoogleMap mMap;
    private FrameLayout mapWrapper;
    
    private Button okBtn;
    private Button cancelBtn;
    private TextView titleView;

	private LocationManager locationManager;
	private LatLng chosenLocation;
	private Address chosenAddress;
	private Marker chosenLocationMarker;

	private boolean locationInitialized = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_picker_view);
		mapWrapper = (FrameLayout)findViewById(R.id.map_wrapper);
	    mapWrapper.setVisibility(View.INVISIBLE);
	    okBtn = (Button) findViewById(R.id.ok_btn);
	    cancelBtn = (Button) findViewById(R.id.cancel_btn);
	    titleView = (TextView) findViewById(R.id.title);
	    
	    okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pickLocation();
			}
		});
	    
	    cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	    
	    Bundle extras = getIntent().getExtras();
	    if(extras != null){
		    if(extras.containsKey(LOCATION)){
		    	double[] loc = extras.getDoubleArray(LOCATION);
		    	chosenLocation = new LatLng(loc[0], loc[1]);
		    }
		    
		    if(extras.containsKey(TITLE))
		    	titleView.setText(extras.getString(TITLE));
		    else titleView.setText(R.string.pick_location);
	    }
		setUpMapIfNeeded();
	}

	@Override
	public void onStop() {
		if(locationManager != null)
			locationManager.removeUpdates(this);
		super.onStop();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play
	 * services APK is correctly installed) and the map has not already been
	 * instantiated.. This will ensure that we only ever call
	 * {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
	 * for the user to install/update the Google Play services APK on their
	 * device.
	 * <p>
	 * A user can return to this FragmentActivity after following the prompt and
	 * correctly installing/updating/enabling the Google Play services. Since
	 * the FragmentActivity may not have been completely destroyed during this
	 * process (it is likely that it would only be stopped or paused),
	 * {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			MapFragment mMapFragment = (MapFragment) getFragmentManager()
					.findFragmentById(R.id.map);

			mMap = mMapFragment.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera. In this case, we just add a marker near Africa. This should only
	 * be called once and when we are sure that {@link #mMap} is not null.
	 */
	private void setUpMap() {
		mMap.setMyLocationEnabled(true);
		mMap.setOnMapClickListener(this);
		locationManager = 
				(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (!locationInitialized) {
			
			if(chosenLocation == null)
				chosenLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
			
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					chosenLocation, 15);
			mMap.moveCamera(cameraUpdate);//animateCamera(cameraUpdate, 1000, null);
			
			showChosenLocation();
		    mapWrapper.setVisibility(View.VISIBLE);

			locationInitialized = true;
		}
	}

	public void showChosenLocation(){
		new AsyncTask<Void, Void, Address>(){

			@Override
			protected Address doInBackground(Void... params) {
				try {
					Geocoder geoCoder = new Geocoder(getApplicationContext());
					List<Address> matches;
					matches = geoCoder.getFromLocation(
							chosenLocation.latitude, chosenLocation.longitude, 1);
					
					Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
					return bestMatch;

				} catch (IOException e) {
					Log.e(TAG, "Show chosen location :" + e.getMessage());
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(Address address) {
				
				chosenAddress = address;
				String addressStr = "Chosen location";
				
				if(chosenAddress != null){
					addressStr = getReadableAddress(address);
				}
				
				if(chosenLocationMarker == null){
					chosenLocationMarker = mMap.addMarker(
						new MarkerOptions().position(chosenLocation)
						.title(addressStr));
				}else{
					chosenLocationMarker.setPosition(chosenLocation);
					chosenLocationMarker.setTitle(addressStr);
				}
				chosenLocationMarker.showInfoWindow();
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
						chosenLocation, 15);
				mMap.animateCamera(cameraUpdate);

			}
			
		}.execute();
	}
	
	public String getReadableAddress(Address address){
		String result = "";
		int numLines = address.getMaxAddressLineIndex();
		for(int i = 0; i < numLines; i++){
			result += address.getAddressLine(i);
			if(i < numLines - 1)
				result += ", ";
		}
		return result;
	}
	public void pickLocation(){
		Intent returnIntent = new Intent();
		if(chosenLocation != null)
			returnIntent.putExtra(LOCATION, 
				new double[]{chosenLocation.latitude, chosenLocation.longitude});
		
		if(chosenAddress != null)
			returnIntent.putExtra(LOCATION_ADDRESS, getReadableAddress(chosenAddress));
		
		setResult(RESULT_OK,returnIntent);
		finish();
	}
	
	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onMapClick(LatLng location) {
		chosenLocation = location;
		showChosenLocation();
	}

}
