package usc.edu.crowdtasker.data.model;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	public static final String FIRST_NAME_COL = "FIRST_NAME";
	private String firstName;
	
	public static final String LAST_NAME_COL = "LAST_NAME";
	private String lastName;
	
	public static final String RATING_COL = "RATING";
	private Double rating;
	
	public static final String PROFILE_PIC_COL = "PROFILE_PIC";
	private String profilePic;
	
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
	
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the rating
	 */
	public Double getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(Double rating) {
		this.rating = rating;
	}
	/**
	 * @return the profilePic
	 */
	public String getProfilePic() {
		return profilePic;
	}
	/**
	 * @param profilePic the profilePic to set
	 */
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	@Override
	public JSONObject toJSON() {
		try{
			JSONObject result = new JSONObject();
	
			result.put(ID_COL, id);
			result.put(LOGIN_COL, login);
			result.put(PASS_COL, password);
			result.put(EMAIL_COL, email);
			result.put(FIRST_NAME_COL, firstName);
			result.put(LAST_NAME_COL, lastName);
			
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
			
			if(!jsonObject.isNull(PASS_COL))
				setPassword(jsonObject.getString(PASS_COL));
			
			if(!jsonObject.isNull(EMAIL_COL))
				setEmail(jsonObject.getString(EMAIL_COL));
			
			if(!jsonObject.isNull(FIRST_NAME_COL))
				setFirstName(jsonObject.getString(FIRST_NAME_COL));
			
			if(!jsonObject.isNull(LAST_NAME_COL))
				setLastName(jsonObject.getString(LAST_NAME_COL));
			
			if(!jsonObject.isNull(RATING_COL))
				setRating(jsonObject.getDouble(RATING_COL));
			
			if(!jsonObject.isNull(PROFILE_PIC_COL))
				setProfilePic(jsonObject.getString(PROFILE_PIC_COL));
		} catch (JSONException e) {
			Log.e(TAG, "fromJSON JSONException: " + e.getMessage());
			return false;
		} 
		
		return true;
	}
	
	
	
	
	
}
