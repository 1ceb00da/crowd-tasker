package usc.edu.crowdtasker.data.provider;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import usc.edu.crowdtasker.data.model.User;

public class UserProvider extends DataProvider {
	
	public static final String TAG = "UserProvider";
	
	public static User getCurrentUser(Context context){
		User user = new User();
		user.setId(1L);
		user.setLogin("geotasker");
		return user;
	}
	
	public static List<User> getUsers(){
		List<User> result = new ArrayList<User>();
		try {
			JSONArray usersJson = performRequest(User.ENTITY_NAME, GET, null);
			if(usersJson == null)
				return result;
			for(int i = 0; i < usersJson.length(); i++){
				JSONObject userJson = usersJson.getJSONObject(i);
				User user = new User();
				user.setId(userJson.getLong(User.ID_COL));
				user.setLogin(userJson.getString(User.LOGIN_COL));
				user.setEmail(userJson.getString(User.EMAIL_COL));
				result.add(user);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		return result;
	}
}
