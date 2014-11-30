package usc.edu.crowdtasker;

import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.provider.UserProvider;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.RatingBar;

public class ProfileActivity extends Activity {
	
	public static final int SELECT_PICTURE = 1;
	public static final double PROFILE_PIC_DIM = 180.0;
	private User currentUser;
	
	private TextView username;
	private EditText firstName;
	private EditText lastName;
	private EditText email;
	private RatingBar ratingBar;
	private Button saveBtn;
	private ImageView profilePicture;
	
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
		profilePicture = (ImageView) findViewById(R.id.profile_picture);
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
			
			profilePicture.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");
					startActivityForResult(photoPickerIntent, SELECT_PICTURE);    
				}
			});
			
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
	
	
	protected void onActivityResult(int requestCode, int resultCode, 
		       Intent imageReturnedIntent) {
		    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

		    switch(requestCode) { 
		    case SELECT_PICTURE:
		        if(resultCode == RESULT_OK){  
		            Uri selectedImage = imageReturnedIntent.getData();
		            String[] filePathColumn = {MediaStore.Images.Media.DATA};

		            Cursor cursor = getContentResolver().query(
		                               selectedImage, filePathColumn, null, null, null);
		            cursor.moveToFirst();
		            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		            String filePath = cursor.getString(columnIndex);
		            cursor.close();
		            
		            Bitmap selectedBmp = BitmapFactory.decodeFile(filePath);
		            double max = Math.max(profilePicture.getHeight(), profilePicture.getWidth());
		            
		            double factor = PROFILE_PIC_DIM / max ;
		            
		            Bitmap scaledBmp = Bitmap.createScaledBitmap(selectedBmp, 
		            		(int)(factor * profilePicture.getWidth()), 
		            		(int)(factor * profilePicture.getHeight()), false);

		            profilePicture.setImageBitmap(scaledBmp);
		        }
		        
		    }
		}

}
