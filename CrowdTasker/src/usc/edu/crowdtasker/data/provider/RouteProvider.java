package usc.edu.crowdtasker.data.provider;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class RouteProvider extends DataProvider{
	public static final String TAG = "RouteProvider";
	public static final String GMAP_URL = "https://maps.googleapis.com/maps/api/directions/json?";
	
	public static List<LatLng> getRoute(String startAddr, String endAddr){
		List<LatLng> route = new ArrayList<LatLng>();
		try {
			String url = GMAP_URL+ "origin=" + Uri.encode(startAddr) +"&"
					+"destination="+ Uri.encode(endAddr );
			String callResult = callWebService(url); 
			JSONObject json = new JSONObject(callResult);
			JSONArray routeArray = json.getJSONArray("routes");
			
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject polylines = routes.getJSONObject("overview_polyline");
			
	        String encodedString;
			
			encodedString = polylines.getString("points");
			route = decodePoly(encodedString);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
        
        return route;
	}
	
	private static List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
 
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
 
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
 
            LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
            poly.add(p);
        }
 
        return poly;
	}
			
}
