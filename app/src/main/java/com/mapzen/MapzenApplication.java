package com.mapzen;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;

import static android.provider.BaseColumns._ID;

public class MapzenApplication extends Application {
    public static final int HTTP_REQUEST_TIMEOUT_MS = 500;
    public static final String PELIAS_TEXT = "text";
    private final String[] columns = {
            _ID, PELIAS_TEXT
    };
    public static final double DEFAULT_LATITUDE = 64.133333;
    public static final double DEFAULT_LONGITUDE = -21.933333;
    public static final int DEFAULT_ZOOMLEVEL = 15;
    private static MapPosition mapPosition =
            new MapPosition(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, Math.pow(2, DEFAULT_ZOOMLEVEL));
    public static final String LOG_TAG = "Mapzen: ";
    private String currentSearchTerm = "";
    private RequestQueue queue;
    private Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
    }

    public String[] getColumns() {
        return columns;
    }


    public void storeMapPosition(MapPosition pos) {
        mapPosition = pos;
    }

    public double getStoredZoomLevel() {
        return mapPosition.zoomLevel;
    }

    public GeoPoint getLocationPoint() {
        return getLocationPosition().getGeoPoint();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public MapPosition getLocationPosition() {
        if (location != null) {
            mapPosition = new MapPosition(location.getLatitude(), location.getLongitude(),
                            Math.pow(2, getStoredZoomLevel()));
        } else {
            mapPosition = new MapPosition(DEFAULT_LATITUDE, DEFAULT_LONGITUDE,
                            Math.pow(2, getStoredZoomLevel()));
        }
        return mapPosition;
    }

    public String getCurrentSearchTerm() {
        return currentSearchTerm;
    }

    public void setCurrentSearchTerm(String currentSearchTerm) {
        this.currentSearchTerm = currentSearchTerm;
    }

    public void enqueueApiRequest(Request<?> request) {
        Log.d(LOG_TAG, "request: adding " + request.getUrl());
        request.setRetryPolicy(new DefaultRetryPolicy(HTTP_REQUEST_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void cancelAllApiRequests() {
        if (queue != null) {
            queue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }
}
