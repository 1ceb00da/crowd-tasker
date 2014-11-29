package usc.edu.crowdtasker.data.provider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import usc.edu.crowdtasker.data.model.Rating;
import usc.edu.crowdtasker.data.model.Task;
import android.util.Log;

public class RatingProvider extends DataProvider {
	
	public static boolean createRating(Rating rating){
		try {
			JSONObject params = rating.toJSON();
			if(params == null)
				return false;
			
			JSONArray ratingsJson = performRequest(Rating.ENTITY_NAME, CREATE, params);
			if(ratingsJson == null)
				return false;
			
			for(int i = 0; i < ratingsJson.length(); i++){
				JSONObject resultJson = ratingsJson.getJSONObject(i);
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
	
	public static boolean updateRating(Rating rating){
		try {
			JSONObject params = rating.toJSON();
			if(params == null)
				return false;
			
			JSONArray ratingsJson = performRequest(Rating.ENTITY_NAME, UPDATE, params);
			if(ratingsJson == null)
				return false;
			
			for(int i = 0; i < ratingsJson.length(); i++){
				JSONObject resultJson = ratingsJson.getJSONObject(i);
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
