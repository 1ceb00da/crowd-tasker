package usc.edu.crowdtasker.data.model;

import org.json.JSONObject;

public abstract class JSONBase {
	public abstract JSONObject toJSON();
	public abstract boolean fromJSON(JSONObject jsonObject);
	
}
