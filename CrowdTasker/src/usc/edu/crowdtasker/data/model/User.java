package usc.edu.crowdtasker.data.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import usc.edu.crowdtasker.data.provider.DataProvider;

import android.text.TextUtils;
import android.util.Log;

public class User extends JSONBase{
	
	public static final String TAG = "User";
	
	public static final String ENTITY_NAME = "user";

	public static final String ID_COL = "ID";
	private Long id;
	
	public static final String LOGIN_COL = "LOGIN";
	private String login;
	
	public static final String EMAIL_COL = "EMAIL";
	private String email;
	
	public static final String PASS_COL = "PASS";
	private String password;
	
	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}
	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
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
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public JSONObject toJSON() {
		try{
			JSONObject result = new JSONObject();
	
			result.put(ID_COL, id);
			result.put(LOGIN_COL, login);
			result.put(PASS_COL, password);
			result.put(EMAIL_COL, email);
			
			return result;
			
		} catch (JSONException e) {
			Log.e(TAG, "toJSON JSONException: " + e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public boolean fromJSON(JSONObject jsonObject) {
		
		try{
			if(!jsonObject.isNull(ID_COL))
				setId(jsonObject.getLong(ID_COL));
			
			if(!jsonObject.isNull(LOGIN_COL))
				setLogin(jsonObject.getString(LOGIN_COL));
			
			if(!jsonObject.isNull(EMAIL_COL))
				setEmail(jsonObject.getString(EMAIL_COL));
			
		} catch (JSONException e) {
			Log.e(TAG, "fromJSON JSONException: " + e.getMessage());
			return false;
		} 
		
		return true;
	}
	
	
	
	
	
}
