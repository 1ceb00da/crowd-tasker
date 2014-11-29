package usc.edu.crowdtasker;

import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.provider.UserProvider;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.RatingBar;

public class ProfileActivity extends Activity {
	private User currentUser;
	
	private TextView username;
	private EditText firstName;
	private EditText lastName;
	private EditText email;
	private RatingBar ratingBar;
	private Button saveBtn;
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_activity);
		currentUser = UserProvider.getCurrentUser(getApplicationContext());
		
		username = (TextView)findViewById(R.id.username);
		firstName = (EditText)findViewById(R.id.first_name);
		lastName = (EditText)findViewById(R.id.last_name);
		email = (EditText)findViewById(R.id.email);
		saveBtn = (Button)findViewById(R.id.save_btn);
		ratingBar = (RatingBar)findViewById(R.id.rating_bar);
		ratingBar.setActivated(false);
		
		progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.saving_profile));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        
		if(currentUser != null){
			username.setText(currentUser.getLogin());
			new AsyncTask<Long, Void, User>() {

				@Override
				protected User doInBackground(Long... params) {
					return UserProvider.getUserById(params[0]);
				}
				
				@Override
				protected void onPostExecute(User result) {
					currentUser = result;
					if(result.getEmail() != null)
						email.setText(result.getEmail());
					if(result.getFirstName() != null)
						firstName.setText(result.getFirstName());
					if(result.getLastName() != null)
						lastName.setText(result.getLastName());
					if(result.getRating() != null)
						ratingBar.setRating(result.getRating().floatValue());
				}
			}.execute(currentUser.getId());
			
			saveBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currentUser.setFirstName(firstName.getText().toString());
					currentUser.setLastName(lastName.getText().toString());
					currentUser.setEmail(email.getText().toString());
					new AsyncTask<User, Void, Boolean>(){
						protected void onPreExecute() {
							progressDialog.show();
						}
						@Override
						protected Boolean doInBackground(User... params) {
							return UserProvider.updateUser(params[0]);
						}
						
						protected void onPostExecute(Boolean result) {
							if(result)
								Toast.makeText(getApplicationContext(),
										R.string.profile_update_success, Toast.LENGTH_LONG).show();
							else Toast.makeText(getApplicationContext(),
										R.string.profile_update_error, Toast.LENGTH_LONG).show();
							progressDialog.dismiss();
						}
						
					}.execute(currentUser);
				}
			});
		}
		
	}

}
