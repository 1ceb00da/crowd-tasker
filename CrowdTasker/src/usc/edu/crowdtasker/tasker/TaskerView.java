package usc.edu.crowdtasker.tasker;

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




/**
 * A placeholder fragment containing a simple view.
 */
public class TaskerView extends Fragment implements UpdatableFragment{

	private ListView taskListView;
	private TaskListAdapter taskListAdapter;
	private Button newTaskBtn;
	
	private User currentUser;

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
        taskListAdapter = new TaskListAdapter(getActivity(), currentUser, R.layout.task_list_item);
        taskListView.setAdapter(taskListAdapter);
        taskListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Task task = (Task)parent.getAdapter().getItem(position);
				if(task != null){
					Intent editTaskIntent = new Intent(getActivity(), NewTaskActivity.class);
					editTaskIntent.putExtra(Task.ID_COL, task.getId());
					startActivity(editTaskIntent); 
				}
			}
		});
        update();
        newTaskBtn = (Button)(rootView.findViewById(R.id.new_task_btn));
        newTaskBtn.setOnClickListener(new OnClickListener () {
			@Override
			public void onClick(View arg0) {
				Intent newTaskIntent = new Intent(getActivity(), NewTaskActivity.class);
				startActivity(newTaskIntent);
			}
		});
        
        return rootView;
    }

	@Override
	public void update() {
		taskListAdapter.updateTasks();
	}
}