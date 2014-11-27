package usc.edu.crowdtasker.data.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import usc.edu.crowdtasker.R;
import usc.edu.crowdtasker.data.model.Task;
import usc.edu.crowdtasker.data.model.User;

public class UserProvider extends DataProvider {
	
	public static final String TAG = "UserProvider";
	
	
	public static List<User> getUsers(HashMap<String, Object> params){
		List<User> result = new ArrayList<User>();
		JSONObject jsonParams = createJSONParams(params);
		
		try {
			JSONArray usersJson = performRequest(User.ENTITY_NAME, GET, jsonParams);
			if(usersJson == null)
				return result;
			for(int i = 0; i < usersJson.length(); i++){
				JSONObject userJson = usersJson.getJSONObject(i);
				User user = new User();
				if(user.fromJSON(userJson))
					result.add(user);
			}
		} catch (JSONException e) {
			Log.e(TAG, "JSONException: " + e.getMessage());
		}
		return result;
	}
	

	public static List<User> getUsers(){
		return getUsers(new HashMap<String, Object>());
	}
	
	
	public static User login(String username, String password){
		if(username == null || password == null ||
				username.isEmpty() || password.isEmpty())
			return null;
			
		String hashedPassword 
			= new String(Hex.encodeHex(DigestUtils.sha1(password)));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(User.LOGIN_COL, username);
		params.put(User.PASS_COL, hashedPassword);
		
		List<User> users = getUsers(params);
		return users.size() > 0 ? users.get(0) : null;
	}
	
	
	public static boolean createUser(User user){
		try {
			String hashedPassword 
				= new String(Hex.encodeHex(DigestUtils.sha1(user.getPassword())));
			user.setPassword(hashedPassword);
			
			JSONObject params = user.toJSON();
			if(params == null)
				return false;
			
			JSONArray usersJson = performRequest(User.ENTITY_NAME, CREATE, params);
			if(usersJson == null)
				return false;
			
			for(int i = 0; i < usersJson.length(); i++){
				JSONObject resultJson = usersJson.getJSONObject(i);
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
	
	
	public static boolean updateUser(User user){
		try {
			JSONObject params = user.toJSON();
			if(params == null)
				return false;
			
			JSONArray usersJson = performRequest(User.ENTITY_NAME, UPDATE, params);
			if(usersJson == null)
				return false;
			
			for(int i = 0; i < usersJson.length(); i++){
				JSONObject resultJson = usersJson.getJSONObject(i);
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
	
	
	
	public static User getUserById(Long id){
		if(id == null)
			return null;
		HashMap<String,Object> params = new HashMap<String, Object>();
		params.put(User.ID_COL, id);
		List<User> result = getUsers(params);
		if(result != null && result.size() > 0)
			return result.get(0);
		return null;
	}
	
	public static User getCurrentUser(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		User user = new User();
		user.setId(prefs.getLong(context.getString(R.string.pref_userid), -1));
		user.setLogin(prefs.getString(context.getString(R.string.pref_username), null));
		
		if(user.getId() > 0 && user.getLogin() != null)
			return user;
		else return null;
	}
	
	public static void logoutCurrentUser(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.remove(context.getString(R.string.pref_username));
		editor.remove(context.getString(R.string.pref_userid));
		editor.commit();
	}
	
}
