package usc.edu.crowdtasker.data.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import usc.edu.crowdtasker.data.model.Task;
import usc.edu.crowdtasker.data.model.User;
import android.util.Log;

public class TaskProvider extends DataProvider {
	public static final String TAG = "TaskProvider";
	
	public static boolean createTask(Task task){
		try {
			JSONObject params = task.toJSON();
			if(params == null)
				return false;
			
			JSONArray tasksJson = performRequest(Task.ENTITY_NAME, CREATE, params);
			if(tasksJson == null)
				return false;
			
			for(int i = 0; i < tasksJson.length(); i++){
				JSONObject resultJson = tasksJson.getJSONObject(i);
				String result = resultJson.getString(RESULT);
				if(!result.equals(RESULT_OK))
					return false;
			}
		} catch (JSONException e) {
			Log.e(TAG, "JSONException: " + e.getMessage());
			return false;
		} 
		
		return true;
	}
	
	public static boolean updateTask(Task task){
		try {
			JSONObject params = task.toJSON();
			if(params == null)
				return false;
			
			JSONArray tasksJson = performRequest(Task.ENTITY_NAME, UPDATE, params);
			if(tasksJson == null)
				return false;
			
			for(int i = 0; i < tasksJson.length(); i++){
				JSONObject resultJson = tasksJson.getJSONObject(i);
				String result = resultJson.getString(RESULT);
				if(!result.equals(RESULT_OK))
					return false;
			}
		} catch (JSONException e) {
			Log.e(TAG, "JSONException: " + e.getMessage());
			return false;
		} 
		
		return true;
	}
	
	public static List<Task> getTasks(HashMap<String, Object> params){
		List<Task> result = new ArrayList<Task>();
		JSONObject jsonParams = createJSONParams(params);
		
		try {
			JSONArray tasksJson = performRequest(Task.ENTITY_NAME, GET, jsonParams);
			if(tasksJson == null)
				return result; 
			for(int i = 0; i < tasksJson.length(); i++){
				JSONObject taskJson = tasksJson.getJSONObject(i);
				Task task = new Task();
				if(task.fromJSON(taskJson))
					result.add(task);
			}
		} catch (JSONException e) {
			Log.e(TAG, "JSONException: " + e.getMessage());
		}
		return result;
	}
	
	public static List<Task> getTasks(User user){
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(Task.OWNER_ID_COL, user.getId());
		return getTasks(params);
	}
	
	public static List<Task> getTasksRange(double location[], Double radius, 
			String unit, Integer numNearestTasks){
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(Task.PARAM_RANGE_LOCATION_LAT, location[0]);
		params.put(Task.PARAM_RANGE_LOCATION_LONG, location[1]);
		if(radius != null)
			params.put(Task.PARAM_RANGE_RADIUS, radius);
		if(unit != null)
			params.put(Task.PARAM_RANGE_UNIT, unit);
		if(numNearestTasks != null)
			params.put(Task.PARAM_NEAREST_TASKS, numNearestTasks);

		return getTasks(params);
	}
	public static List<Task> getTasks(){
		return getTasks(new HashMap<String, Object>());
	}

	public static Task getTaskById(long taskId) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(Task.ID_COL, taskId);
		List<Task> result = getTasks(params);
		
		if(result.size() > 0)
			return result.get(0);
		
		return null;
	}
	
	public static boolean deleteTask(long taskId){
		try {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(Task.ID_COL, taskId);
			
			JSONObject jsonParams = createJSONParams(params);
			
			JSONArray tasksJson = performRequest(Task.ENTITY_NAME, DELETE, jsonParams);
			if(tasksJson == null)
				return false;
			
			for(int i = 0; i < tasksJson.length(); i++){
				JSONObject resultJson = tasksJson.getJSONObject(i);
				String result = resultJson.getString(RESULT);
				if(!result.equals(RESULT_OK))
					return false;
			}
		} catch (JSONException e) {
			Log.e(TAG, "JSONException: " + e.getMessage());
			return false;
		} 
		return true;
	}

}
