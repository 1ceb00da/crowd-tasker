package usc.edu.crowdtasker.tasker;

import java.util.Timer;
import java.util.TimerTask;

import usc.edu.crowdtasker.R;
import usc.edu.crowdtasker.UpdatableFragment;
import usc.edu.crowdtasker.data.model.Task;
import usc.edu.crowdtasker.data.model.User;
import usc.edu.crowdtasker.data.provider.UserProvider;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaskerView extends Fragment implements UpdatableFragment{

	public static final int UPDATE_INTERVAL = 10000; // Update interval in milliseconds
	
	private ListView taskListView;
	private TextView taskListEmptyView;
	private TaskListAdapter taskListAdapter;
	private Button newTaskBtn;
	
	private User currentUser;
	private Timer updateTimer;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TaskerView newInstance() {
    	TaskerView fragment = new TaskerView();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TaskerView() {
    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tasker_view, container, false);
        currentUser = UserProvider.getCurrentUser(getActivity());
        
        taskListView = (ListView)(rootView.findViewById(R.id.task_list_view));
        taskListEmptyView = (TextView)(rootView.findViewById(R.id.task_list_empty_view));
        taskListView.setEmptyView(taskListEmptyView);
        
        taskListAdapter = new TaskListAdapter(getActivity(), currentUser, R.layout.task_list_item);
        taskListView.setAdapter(taskListAdapter);
        taskListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Task task = (Task)parent.getAdapter().getItem(position);
				if(task != null){
					Intent taskStatusIntent = new Intent(getActivity(), TaskStatusActivity.class);
					taskStatusIntent.putExtra(Task.ID_COL, task.getId());
					if(updateTimer != null)
						updateTimer.cancel();
					startActivity(taskStatusIntent); 
				}
			}
		});
        
    	taskListAdapter.updateTasks();	  
      
        newTaskBtn = (Button)(rootView.findViewById(R.id.new_task_btn));
        newTaskBtn.setOnClickListener(new OnClickListener () {
			@Override
			public void onClick(View v) {
				Intent newTaskIntent = new Intent(getActivity(), NewTaskActivity.class);
				if(updateTimer != null)
					updateTimer.cancel();
				startActivity(newTaskIntent);
			}
		});
        
        return rootView;
    }
    
    @Override
    public void onDestroyView() {
    	if(updateTimer != null){
    		updateTimer.cancel();
        	updateTimer.purge();
    	}
    	super.onDestroyView();
    }

	@Override
	public void update() {
		if(updateTimer != null){
        	updateTimer.cancel();
        	updateTimer.purge();
        }
        
        // Update tasks regularly
	    updateTimer = new Timer();
		  // Set the schedule function and rate
	    updateTimer.scheduleAtFixedRate(new TimerTask() {
		    @Override
		    public void run() {
		    	taskListAdapter.updateTasks();
		    }
	    },
	    0, // Set how long before to start calling the TimerTask (in milliseconds)
	    UPDATE_INTERVAL); // Set the amount of time between each execution (in milliseconds)		
	}

	@Override
	public void stopUpdate() {
		if(updateTimer != null){
        	updateTimer.cancel();
        	updateTimer.purge();
        }		
	}
	
	
}