package usc.edu.crowdtasker.tasker;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;

import usc.edu.crowdtasker.R;
import usc.edu.crowdtasker.data.model.Task;
import usc.edu.crowdtasker.data.model.Task.TaskStatus;
import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.provider.RouteProvider;
import usc.edu.crowdtasker.data.provider.TaskProvider;
import usc.edu.crowdtasker.data.provider.UserProvider;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class TaskStatusActivity extends FragmentActivity{
	
	private Task currentTask;
	private TextView taskName;
	private TextView description;
	private TextView payment;
	private TextView deadline;
	private DateFormat dateFormat;
	private NumberFormat moneyFormat;
    private GoogleMap mMap;
    private Polyline currentRoutePoly;
    private TextView workerView;
    private RatingBar workerRatingBar;
    
    private TextView createdView;
    private TextView acceptedView;
    private TextView completedView;
    private TextView canceledView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_status_activity);
	    dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);
		moneyFormat = NumberFormat.getCurrencyInstance();
		
		taskName = (TextView)findViewById(R.id.task_name);
		description = (TextView)findViewById(R.id.description);
		payment = (TextView)findViewById(R.id.payment);
		deadline = (TextView)findViewById(R.id.deadline);
		workerView = (TextView)findViewById(R.id.worker);
		workerRatingBar = (RatingBar)findViewById(R.id.worker_rating_bar);
		
		createdView = (TextView)findViewById(R.id.status_created);
		acceptedView = (TextView)findViewById(R.id.status_accepted);
		completedView = (TextView)findViewById(R.id.status_completed);
		canceledView = (TextView)findViewById(R.id.status_canceled);
		
		setUpMapIfNeeded();
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Bundle extras = getIntent().getExtras();
	    currentTask = new Task();
	    if(extras != null && extras.containsKey(Task.ID_COL)){
	    	
	    	   new AsyncTask<Long, Void, Task>(){
	    		   
				@Override
				protected Task doInBackground(Long... params) {
			    	  return TaskProvider.getTaskById(params[0]);
				}
				
				@Override
				protected void onPostExecute(Task task) {
					if(task != null){
						currentTask = task;
						setFieldsFromTask();
					}
				}  
	    	   }.execute(extras.getLong(Task.ID_COL));
	     }
	}
	
	 private void setFieldsFromTask(){
	    	if(currentTask.getName() != null)
	    		taskName.setText(currentTask.getName());
	    	
	    	if(currentTask.getDescription() != null)
	    		description.setText(currentTask.getDescription());
	    	
	    	if(currentTask.getDeadline() != null)
	    		deadline.setText(dateFormat.format(currentTask.getDeadline()));
	    	
	    	if(currentTask.getPayment() != null)
	    		payment.setText(moneyFormat.format(currentTask.getPayment()));
	    	
	    	if(currentTask.getStatus() != null){
		    	createdView.setVisibility(View.GONE);
				acceptedView.setVisibility(View.GONE);
				completedView.setVisibility(View.GONE);
				canceledView.setVisibility(View.GONE);
				
				switch(currentTask.getStatus()){
					case ACCEPTED:
						acceptedView.setVisibility(View.VISIBLE);
						break;
					case COMPLETED:
						completedView.setVisibility(View.VISIBLE);
						break;
					case CANCELED:
						canceledView.setVisibility(View.VISIBLE);
						break;
					case CREATED:
					default:
						createdView.setVisibility(View.VISIBLE);
				}
	    	}
	    	
	    	if(currentTask.getWorkerId() != null){
		    	new AsyncTask<Long, Void, User>(){

					@Override
					protected User doInBackground(Long... params) {
						return UserProvider.getUserById(params[0]);
					}
					
					@Override
					protected void onPostExecute(User result) {
						if(result != null){
							String name = 
									result.getFirstName() == null ? "":result.getFirstName()
								  + result.getLastName() == null ? "": " " + result.getLastName();
							name = name.trim();
							if(name.isEmpty())
								name = result.getLogin();
							else name += " (" + result.getLogin() + ")";
							workerView.setText(result.getLogin());
							if(result.getRating() != null)
								workerRatingBar.setRating(result.getRating());
						}
						else workerView.setText(R.string.not_assigned);
					}
		    		
		    	}.execute(currentTask.getWorkerId());
	    	}else workerView.setText(R.string.not_assigned);
	    	
			mMap.clear();
			
			LatLng pickupLoc = null;
			LatLng dropoffLoc = null;
			
			if(currentTask.getPickupLocation() != null){
				MarkerOptions opt = new MarkerOptions();
				pickupLoc = new LatLng(currentTask.getPickupLocation()[0], 
						currentTask.getPickupLocation()[1]);
				opt.position(pickupLoc);
				opt.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				mMap.addMarker(opt);
			}
			
			if(currentTask.getDropoffLocation() != null){
				MarkerOptions opt = new MarkerOptions();
				dropoffLoc = new LatLng(currentTask.getDropoffLocation()[0], 
						currentTask.getDropoffLocation()[1]);
				opt.position(dropoffLoc);
				opt.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
				mMap.addMarker(opt);	
			}
			
			if(pickupLoc != null && dropoffLoc != null){
				LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
				boundsBuilder.include(pickupLoc);
				boundsBuilder.include(dropoffLoc);
				LatLngBounds bounds = boundsBuilder.build();
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 150);
				mMap.animateCamera(cameraUpdate);
			}
			
			if(currentTask.getPickupAddress() != null && currentTask.getDropoffAddress() != null){
							new AsyncTask<String, Void, List<LatLng>>(){
					@Override
					protected  List<LatLng> doInBackground(String... params) {
						return RouteProvider.getRoute(params[0], params[1]); 
					}
					@Override
					protected void onPostExecute(List<LatLng> result) {
						showRoute(result);
					}
				}.execute(currentTask.getPickupAddress(), currentTask.getDropoffAddress());
			}
			
			

	 }
	 
	 private void showRoute( List<LatLng> routePoints) {
	        if (currentRoutePoly != null)
	        	currentRoutePoly.remove();
	        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
	        for (int z = 0; z < routePoints.size(); z++) {
	            LatLng point = routePoints.get(z);
	            options.add(point);
	        }
	        currentRoutePoly = mMap.addPolyline(options);
		}
	 
	 private void setUpMapIfNeeded() {
			// Do a null check to confirm that we have not already instantiated the
			// map.
			if (mMap == null) {
				// Try to obtain the map from the SupportMapFragment.
				MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

				mMap = mMapFragment.getMap();
				// Check if we were successful in obtaining the map.
				if (mMap != null) {
					mMap.setMyLocationEnabled(true);
			    	mMap.getUiSettings().setZoomControlsEnabled(false);
				}
			}
		}
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     // Inflate the menu items for use in the action bar
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.task_actions, menu);
	     return super.onCreateOptionsMenu(menu);
	 }
	 
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 if(currentTask == null)
             return super.onOptionsItemSelected(item);

	     // Handle presses on the action bar items
	     switch (item.getItemId()) {
	         case R.id.action_edit:
	        	 Intent editIntent = new Intent(getApplicationContext(), NewTaskActivity.class);
	        	 editIntent.putExtra(Task.ID_COL, currentTask.getId());
	        	 startActivity(editIntent);
	             return true;
	             
	         case R.id.action_delete:
	        	 deleteTask();
	             return true;
	         default:
	             return super.onOptionsItemSelected(item);
	     }
	 }
	 
	 private void deleteTask(){
		 if(currentTask == null)
			 return;
		 if(currentTask.getStatus() != null 
		    && (currentTask.getStatus().equals(TaskStatus.CREATED)
			|| currentTask.getStatus().equals(TaskStatus.CANCELED)
			|| currentTask.getStatus().equals(TaskStatus.COMPLETED))){
			 new AlertDialog.Builder(this)
		    	.setTitle(R.string.delete_task_dialog_title)
		    	.setMessage(R.string.delete_task_dialog_msg)
		    	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		    		public void onClick(final DialogInterface dialog, int which) { 
		    			new AsyncTask<Long, Void, Boolean>(){

							@Override
							protected Boolean doInBackground(Long... params) {
								return TaskProvider.deleteTask(params[0]);
							}
							
							@Override
							protected void onPostExecute(Boolean result) {
								if(result){
									Toast.makeText(getApplicationContext(),
											R.string.task_delete_success, Toast.LENGTH_LONG).show();
									dialog.dismiss();
									finish();
								}else{
									Toast.makeText(getApplicationContext(),
											R.string.task_delete_error, Toast.LENGTH_LONG).show();
									dialog.dismiss();
								}
							}
		    				
		    			}.execute(currentTask.getId());
		    		}
		    	})
		    	.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		    		public void onClick(DialogInterface dialog, int which) { 
		    			dialog.dismiss();
		    		}
		    	})
		    	.setIcon(android.R.drawable.ic_dialog_alert)
		    	.show();
		 }else{
			 new AlertDialog.Builder(this)
		    	.setTitle(R.string.delete_task_dialog_title)
		    	.setMessage(R.string.delete_task_dialog_cancel)
		    	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		    		public void onClick(final DialogInterface dialog, int which) { 
		    			new AsyncTask<Task, Void, Boolean>(){

							@Override
							protected Boolean doInBackground(Task... params) {
								currentTask.setStatus(TaskStatus.CANCELED);
								return TaskProvider.updateTask(params[0]);
							}
							
							@Override
							protected void onPostExecute(Boolean result) {
								if(result){
									Toast.makeText(getApplicationContext(),
											R.string.task_cancel_success, Toast.LENGTH_LONG).show();
									dialog.dismiss();
									setFieldsFromTask();
								}else{
									Toast.makeText(getApplicationContext(),
											R.string.task_cancel_error, Toast.LENGTH_LONG).show();
									dialog.dismiss();
								}
							}
		    				
		    			}.execute(currentTask);
		    		}
		    	})
		    	.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		    		public void onClick(DialogInterface dialog, int which) { 
		    			dialog.dismiss();
		    		}
		    	})
		    	.setIcon(android.R.drawable.ic_dialog_alert)
		    	.show();
		 }
	 }
	    
}
