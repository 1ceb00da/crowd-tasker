package usc.edu.crowdtasker.data.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DataProvider {

	public static final String TAG = "DataProvider";
	
	public static final String RESULT = "result";
	public static final String GET = "get";
	public static final String CREATE = "create";
	public static final String UPDATE = "update";
	
	public static final String RESULT_OK = "ok";
	public static final String RESULT_FAIL = "fail";
	
	
	protected final static String SERVICE_URL = "http://csci587team12.cloudapp.net";
	
	public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm";

	public static JSONArray performRequest(String entityName, String funcName, 
			JSONObject jsonParams){
		
		String url = SERVICE_URL + "/" + entityName + "/"+funcName;
		
		HttpClient httpClient = new DefaultHttpClient();
	    HttpResponse response;
        String responseString = null;
        try {
        	HttpPost httpPost = new HttpPost(url);
        	httpPost.setHeader("Content-type", "application/json");
        	if(jsonParams != null){
        		String jsonString = jsonParams.toString();
        		httpPost.setEntity(new StringEntity(jsonString));
        		Log.d(TAG, funcName + " " + entityName + " JSON params: " + jsonString);
        	}else        
        		Log.d(TAG, funcName + " " + entityName + " No params");

        	
            response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
                Log.d(TAG, funcName + " " + entityName + " JSON Response: "+responseString);
            } else{
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
            
            JSONArray reader = new JSONArray(responseString);
            return reader;
            
        } catch (ClientProtocolException e) {
        	Log.e(TAG, "ClientProtocolException: " + e.getMessage());  	
        } catch (IOException e) {
        	Log.e(TAG, "IOException: " + e.getMessage());
        } catch (JSONException e) {
        	Log.e(TAG, "JSONException: " + e.getMessage());
		}
    	return null;		
	}
	
	
	public static JSONObject createJSONParams(HashMap<String, Object> params){
		if(params == null || params.isEmpty())
			return null;
		try {
			JSONObject result = new JSONObject();
			for(String key : params.keySet()){
				result.put(key, params.get(key));
			}
			return result;

		} catch (JSONException e) {
			Log.e(TAG, "Create JSON Params: " + e.getMessage());
		}
		return null;
	}
	
	public static String callWebService(String url){
		String result = "";
        HttpClient httpclient = new DefaultHttpClient();  
        HttpGet request = new HttpGet(url);  
        ResponseHandler<String> handler = new BasicResponseHandler();  
        try {  
            result = httpclient.execute(request, handler);  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        httpclient.getConnectionManager().shutdown();   
        Log.i("RESTCALL", result);  
        return result;
	 } // end callWebService()  
}
