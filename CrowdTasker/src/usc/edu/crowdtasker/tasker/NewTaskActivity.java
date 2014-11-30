package usc.edu.crowdtasker.tasker;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import usc.edu.crowdtasker.R;
import usc.edu.crowdtasker.data.model.Task;
import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.model.Task.TaskStatus;
import usc.edu.crowdtasker.data.provider.TaskProvider;
import usc.edu.crowdtasker.data.provider.UserProvider;
import usc.edu.crowdtasker.location.MapPicker;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewTaskActivity extends Activity {

	public static final int PICKUP_LOC_CODE = 1;
	public static final int DROPOFF_LOC_CODE = 2;
	public static final int DATEPICKER_DIALOG = 1;
	
	private TextView headerTitleTv;
	
	private RelativeLayout fieldsWrapper;
	private EditText nameEt;
	private EditText descriptionEt;

	private Button deadlineEt;
	private EditText paymentEt;
	private Button pickupLocationBt;
	private Button dropoffLocationBt;
	
	private Button createBtn;
	
	private Task currentTask;
	
	private DateFormat dateFormat;
	private NumberFormat moneyFormat;
	
	private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {  
       super.onCreate(savedInstanceState);    
       setContentView(R.layout.new_task_view);
       
       dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);
	   moneyFormat = NumberFormat.getInstance();
       
	   headerTitleTv = (TextView) findViewById(R.id.new_task_header_title);
       nameEt = (EditText) findViewById(R.id.name);
       descriptionEt = (EditText) findViewById(R.id.description);
       deadlineEt = (Button) findViewById(R.id.deadline);
       deadlineEt.setKeyListener(null);
       paymentEt = (EditText) findViewById(R.id.payment);
       pickupLocationBt = (Button) findViewById(R.id.pickup_loc);
       pickupLocationBt.setKeyListener(null);
       dropoffLocationBt = (Button) findViewById(R.id.dropoff_loc);
       dropoffLocationBt.setKeyListener(null);
       fieldsWrapper = (RelativeLayout) findViewById(R.id.new_task_field_wrapper);
       createBtn = (Button) findViewById(R.id.create_btn);
       
   	   progressDialog = new ProgressDialog(this);
   	   progressDialog.setCanceledOnTouchOutside(false);
   	   progressDialog.setCancelable(false);
       
       Bundle extras = getIntent().getExtras();
       currentTask = new Task();
       if(extras != null && extras.containsKey(Task.ID_COL)){

    	   headerTitleTv.setText(R.string.edit_task);
		   createBtn.setText(R.string.save);
		   fieldsWrapper.setVisibility(View.INVISIBLE);
		   
    	   progressDialog.setMessage(getString(R.string.loading_task));
    	   progressDialog.show();
    	   
    	   new AsyncTask<Long, Void, Task>(){
    		   
				@Override
				protected Task doInBackground(Long... params) {
			    	  return TaskProvider.getTaskById(params[0]);
				}
				
				@Override
				protected void onPostExecute(Task task) {
					progressDialog.dismiss();
					if(task != null){
						currentTask = task;
						setFieldsFromTask();
	
					}
					fieldsWrapper.setVisibility(View.VISIBLE);
				}  
			
    	   }.execute(extras.getLong(Task.ID_COL));
       }
       
       deadlineEt.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				showDeadlineDatePicker();				
			}
       });
	   
       pickupLocationBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onPickupLocation();
	    	}
       });
       
       dropoffLocationBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onDropoffLocation();
	    	}
      });
       
      createBtn.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				createOrEditTask();
			}
       });
      setFieldsFromTask();

    }
   
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == PICKUP_LOC_CODE){
    		if(resultCode == RESULT_OK){
    			double[] location = data.getDoubleArrayExtra(MapPicker.LOCATION);
    			String locationAddr = data.getStringExtra(MapPicker.LOCATION_ADDRESS);
    			currentTask.setPickupLocation(location);
    			currentTask.setPickupAddress(locationAddr);
    			setFieldsFromTask();
    		}
    	}else if(requestCode == DROPOFF_LOC_CODE){
    		if(resultCode == RESULT_OK){
    			double[] location = data.getDoubleArrayExtra(MapPicker.LOCATION);
    			String locationAddr = data.getStringExtra(MapPicker.LOCATION_ADDRESS);
    			currentTask.setDropoffLocation(location);
    			currentTask.setDropoffAddress(locationAddr);

    			setFieldsFromTask();
    		}
    	}
    }
    
    
    private void onPickupLocation(){
    	Intent intent = new  Intent(getApplicationContext(),MapPicker.class);
		if(currentTask.getPickupLocation() != null)
			intent.putExtra(MapPicker.LOCATION, currentTask.getPickupLocation());

		intent.putExtra(MapPicker.TITLE, getString(R.string.pickup_loc_header));
		startActivityForResult(intent, PICKUP_LOC_CODE);		
    }
    
    private void onDropoffLocation(){
    	Intent intent = new  Intent(getApplicationContext(),MapPicker.class);
		if(currentTask.getDropoffLocation() != null)
			intent.putExtra(MapPicker.LOCATION, currentTask.getDropoffLocation());
		
		intent.putExtra(MapPicker.TITLE, getString(R.string.dropoff_loc_header));
		startActivityForResult(intent, DROPOFF_LOC_CODE);	
    }
    
    private void showDeadlineDatePicker(){
    	final Calendar cal = Calendar.getInstance(TimeZone.getDefault());

    	final Calendar result = Calendar.getInstance(TimeZone.getDefault());
    	
    	final TimePickerDialog.OnTimeSetListener timePickerListener 
			= new TimePickerDialog.OnTimeSetListener(){

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				if(!view.isShown())
					return;
				result.set(Calendar.HOUR_OF_DAY, hourOfDay);
				result.set(Calendar.MINUTE, minute);
				currentTask.setDeadline(result.getTime());
				setFieldsFromTask();
			}
		
    	};
    	
    	DatePickerDialog.OnDateSetListener datePickerListener 
			= new DatePickerDialog.OnDateSetListener() {
		
			public void onDateSet(DatePicker view, int year,int month, int day) {
				if(!view.isShown())
					return;
				result.set(Calendar.YEAR, year);
				result.set(Calendar.MONTH, month);
				result.set(Calendar.DAY_OF_MONTH, day);

				TimePickerDialog timePicker = new TimePickerDialog(
						NewTaskActivity.this,
						timePickerListener,cal.get(Calendar.HOUR_OF_DAY),
						cal.get(Calendar.MINUTE),false);
				timePicker.setCancelable(true);
				timePicker.setTitle(getString(R.string.select_deadline_time));
				timePicker.show();
			}
    	};
		
	   DatePickerDialog datePicker = new DatePickerDialog(this, datePickerListener,
	            cal.get(Calendar.YEAR), 
	            cal.get(Calendar.MONTH),
	            cal.get(Calendar.DAY_OF_MONTH));
	    datePicker.setCancelable(true);
	    datePicker.setTitle(getString(R.string.select_deadline_date));
	    datePicker.show();
	        
    }
 
    
    
    private void createOrEditTask(){
    	
	   
    	User owner = UserProvider.getCurrentUser(getApplicationContext());
    	
    	currentTask.setName(nameEt.getText().toString());
    	currentTask.setDescription(descriptionEt.getText().toString());
    	currentTask.setOwnerId(owner.getId());
    	if(!TextUtils.isEmpty(deadlineEt.getText().toString())){
	    	try {
				Date deadline = dateFormat.parse(deadlineEt.getText().toString());
				currentTask.setDeadline(deadline);
			} catch (ParseException e) {
				Toast.makeText(getApplicationContext(), 
						getString(R.string.enter_correct_deadline_error), 
						Toast.LENGTH_SHORT).show();
				return;
			}
    	}else currentTask.setDeadline(null);
    	
    	
    	if(!TextUtils.isEmpty(paymentEt.getText().toString())){
	    	try {
				double payment = moneyFormat.parse(
						paymentEt.getText().toString()).doubleValue();
				currentTask.setPayment(payment);
			} catch (ParseException e) {
				Toast.makeText(getApplicationContext(), 
						getString(R.string.enter_correct_payment_error), 
						Toast.LENGTH_SHORT).show();
				return;
			}
    	}else currentTask.setPayment(null);
    	
    	if(currentTask.getStatus() == null)
    		currentTask.setStatus(TaskStatus.CREATED);
    	
    	progressDialog.setMessage(getString(R.string.saving_task));
	   	progressDialog.show();
	   	
    	new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				if(currentTask.getId() == null)
		    		return TaskProvider.createTask(currentTask);
		    	else return TaskProvider.updateTask(currentTask);
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				progressDialog.dismiss();
				if(currentTask.getId() == null){
		    		if(result){
		        		Toast.makeText(getApplicationContext(), 
		        				getString(R.string.task_create_success), Toast.LENGTH_SHORT).show();
		        		finish();
		    		}else{
		    			Toast.makeText(getApplicationContext(), 
		        				getString(R.string.task_create_error), Toast.LENGTH_SHORT).show();
		    		}
		    	}else{
		    		if(result){
		    			Toast.makeText(getApplicationContext(), 
		        				getString(R.string.task_update_success), Toast.LENGTH_SHORT).show();
		        		finish();
		    		}else{
		    			Toast.makeText(getApplicationContext(), 
		        				getString(R.string.task_update_error), Toast.LENGTH_SHORT).show();
		    		}	
		    	}
			}
    	}.execute();
    }
    
    private void setFieldsFromTask(){

    	if(currentTask.getName() != null)
    		nameEt.setText(currentTask.getName());
    	
    	if(currentTask.getDescription() != null)
    		descriptionEt.setText(currentTask.getDescription());
    	
    	if(currentTask.getDeadline() != null)
    		deadlineEt.setText(dateFormat.format(currentTask.getDeadline()));
    	
    	if(currentTask.getPayment() != null)
    		paymentEt.setText(moneyFormat.format(currentTask.getPayment()));
    	
    	if(currentTask.getPickupAddress() != null)
    		pickupLocationBt.setText(currentTask.getPickupAddress());
    	else if(currentTask.getPickupLocation() != null)
    		pickupLocationBt.setText(currentTask.getPickupLocation()[0]
    				+ ", " + currentTask.getPickupLocation()[1]);
    	
    	if(currentTask.getDropoffAddress() != null)
    		dropoffLocationBt.setText(currentTask.getDropoffAddress());
    	else if(currentTask.getDropoffLocation() != null)
    		dropoffLocationBt.setText(currentTask.getDropoffLocation()[0]
    				+ ", " + currentTask.getDropoffLocation()[1]);
    }
    
	
	
}
