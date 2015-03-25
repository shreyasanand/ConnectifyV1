package com.team5.uta.connectifyv1;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends ActionBarActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    final int RQS_GooglePlayServices = 1;
    private GoogleMap googleMap = null;
    private LatLng latLng;
    private LocationManager locMgr;
    private String provider;
    private Location location;
    private Geofence geofence = null;
    private Criteria criteria;
    private Marker userMarker;
    private Circle circle;
    protected GoogleApiClient mGoogleApiClient = null;
    private PendingIntent mGeofencePendingIntent;
    List<Geofence> mGeofenceList;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5793f")));
        setUpMapIfNeeded();
        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;
        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(),"Inside onStart!!", Toast.LENGTH_SHORT).show();
        this.user = (User)getIntent().getSerializableExtra("userMarker");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS){
            Toast.makeText(getApplicationContext(),
                    "isGooglePlayServicesAvailable SUCCESS",
                    Toast.LENGTH_LONG).show();
        }else{
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the userMarker to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A userMarker can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        if (googleMap == null){
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();//invoke of map fragment by id from main xml file
            //googleMap.getUiSettings().setScrollGesturesEnabled(false);
            //googleMap.getUiSettings().setAllGesturesEnabled(false);
            if (googleMap != null) {
                //googleMap.setMyLocationEnabled(true);//Makes the users current location visible by displaying a blue dot.
                criteria = new Criteria();

                locMgr =(LocationManager)getSystemService(LOCATION_SERVICE);//use of location services by firstly defining location manager.

                provider= locMgr.getBestProvider(criteria, true);
                Log.i("Provider","Provider: "+provider);
                locMgr.requestLocationUpdates(provider,100, 5, this);
                if(provider==null){
                    onProviderDisabled(provider);
                }

                location= locMgr.getLastKnownLocation(provider);
                Log.i("GPS","Location: "+location);
//                if (location!=null){
//                    onLocationChanged(location);
//                }
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if(!mGeofenceList.isEmpty()) {
            removeGeoFence();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        addGeoFence(30f, latLng);
        this.user.setCurrentLocation(location);
    }

    public void addGeoFence(float radius, LatLng latLng) {
        geofence = new Geofence.Builder().setRequestId("1")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(location.getLatitude(), location.getLongitude(), radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE).build();

        userMarker = googleMap.addMarker(new MarkerOptions().position(latLng));

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)   //set center
                .radius(radius)   //set radius in meters
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);

        circle = googleMap.addCircle(circleOptions);

        mGeofenceList.add(geofence);

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Toast.makeText(getApplicationContext(),"Adding geofence",Toast.LENGTH_SHORT).show();
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    public void removeGeoFence(){
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        if(userMarker !=null && circle!=null && geofence!=null){
            try {
                // Remove geofences.
                Toast.makeText(getApplicationContext(),"Removing geofence",Toast.LENGTH_SHORT).show();
                LocationServices.GeofencingApi.removeGeofences(
                        mGoogleApiClient,
                        getGeofencePendingIntent()
                ).setResultCallback(this);
            } catch (SecurityException securityException) {
                logSecurityException(securityException);
            }
            userMarker.remove();
            circle.remove();
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e("Exception", "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MapActivity.this,"Provider disabled", Toast.LENGTH_SHORT);
        Log.i("msg","Provider disabled");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {

    }
}
