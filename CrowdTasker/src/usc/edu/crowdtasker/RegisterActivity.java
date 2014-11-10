package usc.edu.crowdtasker;

import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.provider.UserProvider;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	private Button registerBtn;
	private EditText username;
	private EditText password;
	private EditText passwordRepeat;
	private EditText email;
	
	private SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		registerBtn = (Button)findViewById(R.id.register_btn);
		username = (EditText)findViewById(R.id.username);
		password = (EditText)findViewById(R.id.password);
		passwordRepeat = (EditText)findViewById(R.id.password_repeat);
		email = (EditText)findViewById(R.id.email);
		
		registerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});
	}
	
	private void register(){
		String usernameStr = username.getText().toString().trim();
		String emailStr = email.getText().toString().trim();
		String passwordStr = password.getText().toString();
		String passwordRepeatStr = passwordRepeat.getText().toString();
		
		
		if(TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(emailStr)
			|| TextUtils.isEmpty(passwordStr) || TextUtils.isEmpty(passwordRepeatStr)){
			Toast.makeText(this, R.string.fill_required, Toast.LENGTH_LONG).show();
			return;
		}
		if(!passwordStr.equals(passwordRepeatStr)){
			Toast.makeText(this, R.string.pass_dont_match, Toast.LENGTH_LONG).show();
			return;
		}
		
		User user = new User();
		user.setLogin(usernameStr);
		user.setEmail(emailStr);
		user.setPassword(passwordStr);
		
		new AsyncTask<User, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(User... params) {
				return UserProvider.createUser(params[0]);
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if(result){
					Toast.makeText(getApplicationContext(), R.string.register_success, 
							Toast.LENGTH_LONG).show();
					finish();
				}else{
					Toast.makeText(getApplicationContext(), R.string.register_failed,
							Toast.LENGTH_LONG).show();
				}
			}
			
		}.execute(user);
	}
	
	
}
