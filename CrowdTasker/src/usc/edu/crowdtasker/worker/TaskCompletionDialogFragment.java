package usc.edu.crowdtasker.worker;

import usc.edu.crowdtasker.R;
import usc.edu.crowdtasker.data.model.Task;
import usc.edu.crowdtasker.data.provider.TaskProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class TaskCompletionDialogFragment extends DialogFragment {
	protected static final String TAG = "TaskComepletionDialogFragment";
	Button btnYes, btnNo;
	static String DialogBoxTitle;
	private Task currentTask;

	private Fragment frag;
	
	public interface OnFinishListener {
		void onFinish(boolean c);
	}
	
	// ---empty constructor required
	public TaskCompletionDialogFragment(Task currentTask, Fragment fragment ) {
		this.currentTask = currentTask;
		this.frag = fragment;
	}

	// ---set the title of the dialog window---
	public void setDialogTitle(String title) {
		DialogBoxTitle = title;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.task_completion_dialog_fragment,
				container);
		// ---get the Button views---
		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnNo = (Button) view.findViewById(R.id.btnNo);

		// Button listener
		btnYes.setOnClickListener(btnListener);
		btnNo.setOnClickListener(btnListener);

		// ---set the title for the dialog
		getDialog().setTitle(DialogBoxTitle);

		return view;
	}

	// ---create an anonymous class to act as a button click listener
	private OnClickListener btnListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, currentTask.getName() + "task completed ");
			
			boolean completed = ((Button) v).getText().toString().equals("Completed") ? true : false;
			((OnFinishListener) frag).onFinish(completed);
			currentTask = null;
			dismiss();
		}
	};
}