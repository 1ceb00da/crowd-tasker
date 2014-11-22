package usc.edu.crowdtasker.worker;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import usc.edu.crowdtasker.R;
import usc.edu.crowdtasker.UpdatableFragment;
import usc.edu.crowdtasker.data.model.Task;
import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.provider.TaskProvider;
import usc.edu.crowdtasker.data.provider.UserProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import usc.edu.crowdtasker.data.provider.RouteProvider;


public class WorkerView extends Fragment implements LocationListener, 
		OnMarkerClickListener, OnMapClickListener, UpdatableFragment, OnClickListener {

	public static final String TAG = "WorkerView";
	
    private GoogleMap mMap;
    private FrameLayout mapWrapper;
    private View rootView;
    private LocationManager locationManager;

    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private boolean locationInitialized = false;
    
    private HashMap<Marker, Task> taskMarkerMapping;
    private Marker openPickupMarker;
    private Marker openDropoffMarker;
    
    private Location currentLocation;
	private Polyline currentRoutePoly;
   
    private SharedPreferences prefs;
    private User currentUser;
 	private Task currentTask;
 	
 	
 	private Button acceptBtn;
 	private Button declineBtn;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WorkerView newInstance() {
    	WorkerView fragment = new WorkerView();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public WorkerView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.worker_view, container, false);
        this.rootView = rootView;
        mapWrapper = (FrameLayout)rootView.findViewById(R.id.map_wrapper);
        mapWrapper.setVisibility(View.INVISIBLE);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setUpMapIfNeeded();
        
        acceptBtn = (Button) rootView.findViewById(R.id.accpetTaskBtn);
        declineBtn = (Button) rootView.findViewById(R.id.declineTaskBtn);
        return rootView;
    }
    
    
    @Override
    public void onDestroyView() {
        locationManager.removeUpdates(this);
    	super.onDestroyView();
    }
    
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	// Try to obtain the map from the SupportMapFragment.
        	mMap = ((SupportMapFragment) 
        			getFragmentManager().findFragmentById(R.id.map)).getMap();
        	
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
    	mMap.setMyLocationEnabled(true);
    	mMap.setOnMapClickListener(this);
    	mMap.setOnMarkerClickListener(this);
    	mMap.getUiSettings().setZoomControlsEnabled(false);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
        		LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); 
    }
    
    private void showTasksOnMap(Location currentLocation){
    	if(currentLocation == null)
    		return;
    	final double rangeRadius = (double)prefs.getInt(getString(R.string.pref_range_radius), 10);
        final String rangeUnit = prefs.getString(getString(R.string.pref_range_unit), "mile");
        final int numNearestTasks = prefs.getInt(getString(R.string.pref_nearest_tasks), 10);
        
    	new AsyncTask<Location, Void, List<Task>>() {

			@Override
			protected List<Task> doInBackground(Location... params) {
				Location location = params[0];
				double[] latLng = new double[]{location.getLatitude(), 
											   location.getLongitude()};
				List<Task> tasks = TaskProvider.getTasksRange(latLng, rangeRadius, 
						rangeUnit, numNearestTasks);
				return tasks;
			}
			
			@Override
			protected void onPostExecute(List<Task> tasks) {
				taskMarkerMapping = new HashMap<Marker, Task>();
				mMap.clear();
				for(Task task : tasks){
					MarkerOptions opt = new MarkerOptions();
					LatLng pickupLoc = new LatLng(task.getPickupLocation()[0], task.getPickupLocation()[1]);
					opt.position(pickupLoc);
					opt.title(task.getName());
					String snippet = "";
					if(task.getDescription() != null)
						snippet += task.getDescription();
					if(task.getPayment() != null)
						snippet += ", "
								+ NumberFormat.getCurrencyInstance().format(task.getPayment());
					
					opt.snippet(snippet);
					if(currentUser != null && task.getOwnerId() == currentUser.getId())
						opt.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
					else opt.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					
					Marker marker = mMap.addMarker(opt);
					taskMarkerMapping.put(marker, task);
					
					//icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
					
					/*opt = new MarkerOptions();
					LatLng dropoffLoc = new LatLng(task.getDropoffLoc()[0], task.getDropoffLoc()[1]);
					opt.position(dropoffLoc);
					opt.title("Dropoff " + task.getName());
					mMap.addMarker(opt);*/
				}
			}
		}.execute(currentLocation);
    }

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;
		if(!locationInitialized){
			LatLng latLng = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());
		    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
		    mMap.moveCamera(cameraUpdate);//animateCamera(cameraUpdate);
		    mapWrapper.setVisibility(View.VISIBLE);
	        locationInitialized = true;
	        update();
		}
	}
	

	@Override
	public boolean onMarkerClick(Marker marker) {
		
		
		if(openDropoffMarker != null){
			if(openDropoffMarker.equals(marker)){
				if(openPickupMarker != null)
					openPickupMarker.showInfoWindow();
				return true;
			}
			openDropoffMarker.remove();
			openDropoffMarker = null;
		} 
		
		if(taskMarkerMapping != null && taskMarkerMapping.containsKey(marker)){
			Task task = taskMarkerMapping.get(marker);
			if(task.getDropoffLocation() != null){
				MarkerOptions opt = new MarkerOptions();
				LatLng dropoffLoc = new LatLng(task.getDropoffLocation()[0], task.getDropoffLocation()[1]);
				opt.position(dropoffLoc);
				opt.title(getString(R.string.dropoff) +  ": " +task.getName());
				opt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

				openDropoffMarker = mMap.addMarker(opt);
				openDropoffMarker.showInfoWindow();
				showTaskDetailsPanel(task);
			}
			openPickupMarker = marker;
			openPickupMarker.showInfoWindow();
			fitToOpenMarkers();
		}
		return true;
	}
	
	private void showTaskDetailsPanel(Task task) {
		this.currentTask = task;
		final LinearLayout taskPanel = (LinearLayout) rootView.findViewById(R.id.task_details_panel);
		String directions;
		taskPanel.setVisibility(View.VISIBLE);
		
		TextView taskheading = (TextView) mapWrapper.findViewById(R.id.taskHeading);
		TextView taskDetails = (TextView) mapWrapper.findViewById(R.id.taskDetails);
		
		taskheading.setText(task.getName());
		taskDetails.setText(task.getDescription());
				
		acceptBtn.setOnClickListener(this);
		declineBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				taskPanel.setVisibility(View.GONE);
				
			}
		});
		
	}
	

	private void showRoute( List<LatLng> routePoints) {
        if (currentRoutePoly != null)
        	currentRoutePoly.remove();
        
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < routePoints.size(); z++) {
            LatLng point = routePoints.get(z);
            options.add(point);
        }
        //POLYLINES = options;
        currentRoutePoly = mMap.addPolyline(options);
            			
	}

	

	private void fitToOpenMarkers(){
		if(openPickupMarker == null)
			return;
		CameraUpdate cameraUpdate;
		
		if(openDropoffMarker == null){
			cameraUpdate = CameraUpdateFactory.newLatLngZoom(openPickupMarker.getPosition(), 15);

		}else{
			LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
			boundsBuilder.include(openPickupMarker.getPosition());
			boundsBuilder.include(openDropoffMarker.getPosition());
			
			LatLngBounds bounds = boundsBuilder.build();
			cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 150);
		}
		
		mMap.animateCamera(cameraUpdate);
	}

	@Override
	public void onMapClick(LatLng latLng) {
		if(openDropoffMarker != null){
			openDropoffMarker.remove();
			openDropoffMarker = null;
		}
	}
 
	@Override
	public void onClick(View v) {
		if (v.getId() == acceptBtn.getId()) {
			currentTask.setStatus(Task.TaskStatus.ACCEPTED);
			currentTask.setWorkerId(currentUser.getId());
			
			Log.d(TAG, currentTask.toString());
			
			new AsyncTask<Task, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Task... params) {
					return TaskProvider.updateTask(params[0]);
				}
				
				@Override
				protected void onPostExecute(Boolean result) {
					if (result) {
						// Show task accept panel
						// panle has timer, 
						// get time to deadline form db
						
					}
				};
				
			}.execute(currentTask);
			
		}
		String pickUp = currentTask.getPickupAddress();
		String dropOff = currentTask.getDropoffAddress();

		
		new AsyncTask<String, Void, List<LatLng>>(){
			@Override
			protected  List<LatLng> doInBackground(String... params) {
				return RouteProvider.getRoute(params[0], params[1]); 
			}
			@Override
			protected void onPostExecute(List<LatLng> result) {
				showRoute(result);
			}
		}.execute(pickUp, dropOff);
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
		public void update() {
			currentUser = UserProvider.getCurrentUser(getActivity());
	        showTasksOnMap(currentLocation);
		}
}

