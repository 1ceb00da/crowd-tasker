package usc.edu.crowdtasker.tasker;

import java.util.HashMap;
import java.util.List;

import usc.edu.crowdtasker.R;
import usc.edu.crowdtasker.data.model.Task;
import usc.edu.crowdtasker.data.model.Task.TaskStatus;
import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.provider.TaskProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TaskListAdapter extends ArrayAdapter<Task> {

	private User user;
	private List<Task> tasks;
	private Context context;
	private HashMap<Long, TaskStatus> oldStatuses;
	private ProgressDialog progressDialog;

	
	public TaskListAdapter(Context context, User user, int resource) {
		super(context, resource);
		this.user = user;
		this.context = context;
		
	}
	
	public void updateTasks(){

		new AsyncTask<User, Void, List<Task>>() {
			
			@Override
			protected void onPreExecute() {
				if(progressDialog == null){
					((Activity)context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog = new ProgressDialog(context);
					        progressDialog.setMessage(context.getString(R.string.loading_tasks));
					        progressDialog.setCanceledOnTouchOutside(false);
					        progressDialog.setCancelable(false);
							progressDialog.show();
						}
					});
				}
			}
			
			@Override
			protected List<Task> doInBackground(User... users) {
				return TaskProvider.getTasks(users[0]);
			}
			
			@Override
			protected void onPostExecute(List<Task> result) {
				if(progressDialog.isShowing())
					progressDialog.dismiss();

				if(tasks == null || oldStatuses == null){
					tasks = result;
					oldStatuses = new HashMap<Long, TaskStatus>();
				}else{
				  for(final Task newTask : result){
					if(oldStatuses.containsKey(newTask.getId()) 
						&& newTask.getStatus() != null &&
						!oldStatuses.get(newTask.getId()).equals(newTask.getStatus())){
						
						new AlertDialog.Builder(context)
					    	.setTitle(R.string.task_status_changed)
					    	.setMessage(context.getString(R.string.task_status_changed_msg,
					    			newTask.getName(),newTask.getStatus().name()))
					    	.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					    		public void onClick(DialogInterface dialog, int which) { 
					    			dialog.dismiss();
					    		}
					    	})
					    	.setNegativeButton(R.string.details, new DialogInterface.OnClickListener() {
					    		public void onClick(DialogInterface dialog, int which) { 
					    			Intent intent = new Intent(context, TaskStatusActivity.class);
					    			intent.putExtra(Task.ID_COL, newTask.getId());
					    			context.startActivity(intent);
					    			dialog.dismiss();
					    		}
					    	})
					    	.setIcon(android.R.drawable.ic_dialog_info)
					    	.setCancelable(false)
					    	.show();
					}
					oldStatuses.put(newTask.getId(), newTask.getStatus());
				  }
				  tasks = result;
				}
				
				notifyDataSetChanged();
			};
		}.execute(user);
	}
	
	public void clearTaskStatuses(){
		if(oldStatuses != null)
			oldStatuses.clear();
	}
	@Override
	public Task getItem(int position) {
		if(tasks.size() > position)
			return tasks.get(position);
		else return null;
	}
	@Override
	public int getCount() {
		if(tasks == null)
			return 0;
		return tasks.size();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		if (view == null) {
	        LayoutInflater vi;
	        vi = LayoutInflater.from(getContext());
	        view = vi.inflate(R.layout.task_list_item, null);
	    }
		
		if(tasks != null && tasks.size() > position){
			Task task = tasks.get(position);
			TextView taskNameView = (TextView) view.findViewById(R.id.task_name);
			taskNameView.setText(task.getName());
			showStatus(task.getStatus(), view);
		}
		
		return view;
	}
	
	private void showStatus(TaskStatus status, View view){
		TextView createdView = (TextView)view.findViewById(R.id.status_created);
		TextView acceptedView = (TextView)view.findViewById(R.id.status_accepted);
		TextView completedView = (TextView)view.findViewById(R.id.status_completed);
		TextView canceledView = (TextView)view.findViewById(R.id.status_canceled);
		
		createdView.setVisibility(View.GONE);
		acceptedView.setVisibility(View.GONE);
		completedView.setVisibility(View.GONE);
		canceledView.setVisibility(View.GONE);
		
		switch(status){
			case ACCEPTED:
				acceptedView.setVisibility(View.VISIBLE);
				break;
			case COMPLETED:
				completedView.setVisibility(View.VISIBLE);
				break;
			case CANCELED:
				canceledView.setVisibility(View.VISIBLE);
				break;
			case CREATED:
			default:
				createdView.setVisibility(View.VISIBLE);
		}
		
	}

}
