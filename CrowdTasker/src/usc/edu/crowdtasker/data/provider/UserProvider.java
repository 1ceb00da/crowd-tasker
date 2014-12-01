package usc.edu.crowdtasker.data.provider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import usc.edu.crowdtasker.R;
import usc.edu.crowdtasker.data.model.User;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

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
	
	@SuppressWarnings("deprecation")
	public static boolean uploadProfilePicture(User user, Bitmap bitmap){
		String url = SERVICE_URL + "/upload/profile";
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        bitmap.compress(CompressFormat.JPEG, 75, bos);
	        byte[] data = bos.toByteArray();
	        HttpClient httpClient = new DefaultHttpClient();
	        HttpPost postRequest = new HttpPost(url);
	        ByteArrayBody bab = new ByteArrayBody(data, user.getLogin()+".jpg");
	       
	        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
	        meb.addPart("picture", bab);
	        meb.addPart("user_id", new StringBody(user.getId().toString()));
	        postRequest.setEntity(meb.build());
	        HttpResponse response = httpClient.execute(postRequest);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                response.getEntity().getContent(), "UTF-8"));
	        String sResponse;
	        StringBuilder s = new StringBuilder();
	
	        while ((sResponse = reader.readLine()) != null) {
	            s = s.append(sResponse);
	        }
	        return s.toString().equals(RESULT_OK);
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
		}
        
        return false;
	}
	
	public static Bitmap getProfilePic(User user) {
		if(user == null || user.getProfilePic() == null)
			return null;
	    try {
	    	String uri = SERVICE_URL + "/" + user.getProfilePic();

	        URL url = new URL(uri);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	    	Log.e(TAG, e.getMessage());
	        return null;
	    }
	}
	
}
