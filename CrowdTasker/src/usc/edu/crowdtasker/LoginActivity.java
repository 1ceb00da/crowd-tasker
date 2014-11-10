package usc.edu.crowdtasker;

import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.provider.UserProvider;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private Button loginBtn;
	private Button registerBtn;
	
	private EditText username;
	private EditText password;
	
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        loginBtn = (Button)findViewById(R.id.login_btn);
        registerBtn = (Button)findViewById(R.id.register_btn);
        
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        
        loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});
        
        registerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(intent);
				//finish();
			}
		});

	}
	
	private void login(){
		new AsyncTask<String, Void, User>() {
			
			@Override
			protected User doInBackground(String... params) {
				if(params.length < 2)
					return null;
				return UserProvider.login(params[0], params[1]);
			}
			
			@Override
			protected void onPostExecute(User result) {
				if(result != null){					
					Editor editor = prefs.edit();
					editor.putString(getString(R.string.pref_username), result.getLogin());
					editor.putLong(getString(R.string.pref_userid), result.getId());
					editor.commit();
					
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(intent);
					finish();
				}else{
					Toast.makeText(getApplicationContext(), 
							R.string.login_incorrect, Toast.LENGTH_LONG).show();
				}	
			}
		}.execute(username.getText().toString(), password.getText().toString());
	}
}
