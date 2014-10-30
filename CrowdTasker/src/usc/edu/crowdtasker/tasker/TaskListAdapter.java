package usc.edu.crowdtasker.tasker;

import java.util.List;

import usc.edu.crowdtasker.R;
import usc.edu.crowdtasker.data.model.Task;
import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.provider.TaskProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TaskListAdapter extends ArrayAdapter<Task> {

	private User user;
	private List<Task> tasks;
	
	public TaskListAdapter(Context context, User user, int resource) {
		super(context, resource);
		this.user = user;
	}
	
	public void updateTasks(){
		new AsyncTask<User, Void, List<Task>>() {
			@Override
			protected List<Task> doInBackground(User... users) {
				return TaskProvider.getTasks(users[0]);
			}
			
			@Override
			protected void onPostExecute(List<Task> result) {
				tasks = result;
				notifyDataSetChanged();
			};
		}.execute(user);
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
			TextView taskNameView = (TextView) view.findViewById(R.id.task_name);
			Task task = tasks.get(position);
			taskNameView.setText(task.getName());
		}
		
		
		return view;
	}

}
