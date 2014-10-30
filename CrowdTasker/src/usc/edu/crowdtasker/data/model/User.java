package usc.edu.crowdtasker.data.model;

public class User {
	
	public static final String ENTITY_NAME = "user";

	public static final String ID_COL = "ID";
	private Long id;
	
	public static final String LOGIN_COL = "LOGIN";
	private String login;
	
	public static final String EMAIL_COL = "EMAIL";
	private String email;
	
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
	
	
}
