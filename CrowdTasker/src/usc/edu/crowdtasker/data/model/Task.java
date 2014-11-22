package usc.edu.crowdtasker.data.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import usc.edu.crowdtasker.data.provider.DataProvider;

import android.text.TextUtils;
import android.util.Log;

public class Task extends JSONBase{

	public static final String TAG = "Task";
	
	public enum TaskStatus{
		CREATED, 
		ACCEPTED, 
		COMPLETED, 
		CANCELLED
	}
	
	public static final String ENTITY_NAME = "task";
	
	public static final String ID_COL = "ID";
	private Long id;
	
	public static final String OWNER_ID_COL = "OWNER_ID";
	private Long ownerId;
	
	public static final String WORKER_ID_COL = "WORKER_ID";
	private Long workerId;
	
	public static final String NAME_COL = "NAME";
	private String name;
	
	public static final String DESCRIPTION_COL = "DESCRIPTION";
	private String description;
	
	public static final String DEADLINE_COL = "DEADLINE";
	private Date deadline;
	
	public static final String PAYMENT_COL = "PAYMENT";
	private Double payment;
	
	public static final String PICKUP_LAT_COL = "PICKUP_LAT";
	public static final String PICKUP_LONG_COL = "PICKUP_LONG";
	private double[] pickupLocation;
	
	public static final String PICKUP_ADDRESS_COL = "PICKUP_ADDR";
	private String pickupAddress;

	public static final String DROPOFF_LAT_COL = "DROPOFF_LAT";
	public static final String DROPOFF_LONG_COL = "DROPOFF_LONG";
	private double[] dropoffLocation;
	
	public static final String DROPOFF_ADDRESS_COL = "DROPOFF_ADDR";
	private String dropoffAddress;
	
	public static final String STATUS_COL = "STATUS";
	private TaskStatus status;
	
	public static final String PARAM_RANGE_LOCATION_LAT = "PARAM_RANGE_LOCATION_LAT"; 
	public static final String PARAM_RANGE_LOCATION_LONG = "PARAM_RANGE_LOCATION_LONG";
	public static final String PARAM_RANGE_RADIUS = "PARAM_RANGE_RADIUS"; 
	public static final String PARAM_RANGE_UNIT = "PARAM_RANGE_UNIT"; 
	public static final String PARAM_NEAREST_TASKS = "PARAM_NEAREST_TASKS"; 


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the ownerId
	 */
	public Long getOwnerId() {
		return ownerId;
	}
	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	/**
	 * @return the workerId
	 */
	public Long getWorkerId() {
		return workerId;
	}
	/**
	 * @param workerId the workerId to set
	 */
	public void setWorkerId(Long workerId) {
		this.workerId = workerId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the deadline
	 */
	public Date getDeadline() {
		return deadline;
	}
	/**
	 * @param deadline the deadline to set
	 */
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	/**
	 * @return the payment
	 */
	public Double getPayment() {
		return payment;
	}
	/**
	 * @param payment the payment to set
	 */
	public void setPayment(Double payment) {
		this.payment = payment;
	}
	/**
	 * @return the pickupLoc
	 */
	public double[] getPickupLocation() {
		return pickupLocation;
	}
	/**
	 * @param pickupLocation the pickupLocation to set
	 */
	public void setPickupLocation(double[] pickupLocation) {
		this.pickupLocation = pickupLocation;
	}
	/**
	 * @return the dropoffLoc
	 */
	public double[] getDropoffLocation() {
		return dropoffLocation;
	}
	/**
	 * @param dropoffLocation the dropoffLocation to set
	 */
	public void setDropoffLocation(double[] dropoffLocation) {
		this.dropoffLocation = dropoffLocation;
	}
	/**
	 * @return the pickupLocAddress
	 */
	public String getPickupAddress() {
		return pickupAddress;
	}
	/**
	 * @param pickupAddress the pickupLocAddress to set
	 */
	public void setPickupAddress(String pickupAddress) {
		this.pickupAddress = pickupAddress;
	}
	/**
	 * @return the dropoffLocAddress
	 */
	public String getDropoffAddress() {
		return dropoffAddress;
	}
	/**
	 * @param dropoffAddress the dropoffLocAddress to set
	 */
	public void setDropoffAddress(String dropoffAddress) {
		this.dropoffAddress = dropoffAddress;
	}
	/**
	 * @return the status
	 */
	public TaskStatus getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	
	@Override
	public JSONObject toJSON() {
		try{
			JSONObject result = new JSONObject();
			SimpleDateFormat dateFormat = new SimpleDateFormat(DataProvider.DATE_FORMAT);
	
			result.put(ID_COL, id);
			result.put(OWNER_ID_COL, ownerId);
			result.put(WORKER_ID_COL, workerId);
			result.put(NAME_COL, name);
			result.put(DESCRIPTION_COL, description);
			
			if(deadline != null)
				result.put(DEADLINE_COL, dateFormat.format(deadline));
			else result.put(DEADLINE_COL,null);
	
			result.put(PAYMENT_COL, payment);
			
			if(pickupLocation != null){
				result.put(PICKUP_LAT_COL, pickupLocation[0]);
				result.put(PICKUP_LONG_COL, pickupLocation[1]);
			}
			
			if(dropoffLocation != null){
				result.put(DROPOFF_LAT_COL, dropoffLocation[0]);
				result.put(DROPOFF_LONG_COL, dropoffLocation[1]);
			}
			
			result.put(PICKUP_ADDRESS_COL, pickupAddress);
			result.put(DROPOFF_ADDRESS_COL, dropoffAddress);
			
			if(status != null)
				result.put(STATUS_COL, status.name());
			return result;
			
		} catch (JSONException e) {
			Log.e(TAG, "toJSON JSONException: " + e.getMessage());
			return null;
		}

	}
	
	@Override
	public boolean fromJSON(JSONObject jsonObject) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(DataProvider.DATE_FORMAT);

		try{
			if(!jsonObject.isNull(ID_COL))
				setId(jsonObject.getLong(ID_COL));
			
			if(!jsonObject.isNull(OWNER_ID_COL))
				setOwnerId(jsonObject.getLong(OWNER_ID_COL));
			if(!jsonObject.isNull(WORKER_ID_COL))
				setWorkerId(jsonObject.getLong(WORKER_ID_COL));
			
			setName(jsonObject.getString(NAME_COL));
			setDescription(jsonObject.getString(DESCRIPTION_COL));
			
			String dateStr = jsonObject.getString(DEADLINE_COL);
			if(dateStr != null && !TextUtils.isEmpty(dateStr))
				setDeadline(dateFormat.parse(dateStr));
			
			if(!jsonObject.isNull(PAYMENT_COL))
				setPayment(jsonObject.getDouble(PAYMENT_COL));
			
			if(!jsonObject.isNull(PICKUP_LAT_COL) && !jsonObject.isNull(PICKUP_LONG_COL))
				setPickupLocation(new double[]{jsonObject.getDouble(PICKUP_LAT_COL),
						jsonObject.getDouble(PICKUP_LONG_COL)});
			
			if(!jsonObject.isNull(DROPOFF_LAT_COL) && !jsonObject.isNull(DROPOFF_LONG_COL))
				setDropoffLocation(new double[]{jsonObject.getDouble(DROPOFF_LAT_COL),
						jsonObject.getDouble(DROPOFF_LONG_COL)});
			if(!jsonObject.isNull(PICKUP_ADDRESS_COL))
				setPickupAddress(jsonObject.getString(PICKUP_ADDRESS_COL));
			
			if(!jsonObject.isNull(DROPOFF_ADDRESS_COL))
				setDropoffAddress(jsonObject.getString(DROPOFF_ADDRESS_COL));
			
			if(!jsonObject.isNull(STATUS_COL))
				setStatus(TaskStatus.valueOf(jsonObject.getString(STATUS_COL)));
			
		} catch (JSONException e) {
			Log.e(TAG, "fromJSON JSONException: " + e.getMessage());
			return false;
		} catch (ParseException e) {
			Log.e(TAG, "fromJSON ParseException: " + e.getMessage());
			return false;
		}
		
		return true;
		
	}
	
	
	
	
}
