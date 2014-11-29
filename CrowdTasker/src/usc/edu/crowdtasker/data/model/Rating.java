package usc.edu.crowdtasker.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Rating extends JSONBase{
	
	public static final String TAG = "Rating";
	
	public static final String ENTITY_NAME = "rating";
	
	public static final String FROM_ID_COL = "FROM_ID";
	private long fromId;
	
	public static final String TO_ID_COL = "TO_ID";
	private long toId;
	
	public static final String TASK_ID_COL = "TASK_ID";
	private long taskId;
	
	public static final String RATING_COL = "RATING";
	private double rating;
	/**
	 * @return the fromId
	 */
	public long getFromId() {
		return fromId;
	}
	/**
	 * @param fromId the fromId to set
	 */
	public void setFromId(long fromId) {
		this.fromId = fromId;
	}
	/**
	 * @return the toId
	 */
	public long getToId() {
		return toId;
	}
	/**
	 * @param toId the toId to set
	 */
	public void setToId(long toId) {
		this.toId = toId;
	}
	/**
	 * @return the taskId
	 */
	public long getTaskId() {
		return taskId;
	}
	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	/**
	 * @return the rating
	 */
	public double getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}
	
	
	@Override
	public JSONObject toJSON() {
		try{
			JSONObject result = new JSONObject();
	
			result.put(FROM_ID_COL, fromId);
			result.put(TO_ID_COL, toId);
			result.put(TASK_ID_COL, taskId);
			result.put(RATING_COL, rating);
			
			return result;
			
		} catch (JSONException e) {
			Log.e(TAG, "toJSON JSONException: " + e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public boolean fromJSON(JSONObject jsonObject) {
		
		try{
			if(!jsonObject.isNull(FROM_ID_COL))
				setFromId(jsonObject.getLong(FROM_ID_COL));
			
			if(!jsonObject.isNull(TO_ID_COL))
				setToId(jsonObject.getLong(TO_ID_COL));
			
			if(!jsonObject.isNull(TASK_ID_COL))
				setTaskId(jsonObject.getLong(TASK_ID_COL));
			
			if(!jsonObject.isNull(RATING_COL))
				setRating((float)jsonObject.getDouble(RATING_COL));
			
		} catch (JSONException e) {
			Log.e(TAG, "fromJSON JSONException: " + e.getMessage());
			return false;
		} 
		
		return true;
	}
	
	
	
	
}
