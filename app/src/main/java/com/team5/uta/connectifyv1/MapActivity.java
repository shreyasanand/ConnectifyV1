package com.team5.uta.connectifyv1;

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

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends ActionBarActivity implements LocationListener {

    private GoogleMap googleMap = null;
    private LatLng latLng;
    private LocationManager locMgr;
    private String provider;
    private Location location;
    private Geofence geofence;
    private Criteria criteria;
    private Marker user;
    private Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5793f")));
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
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
                googleMap.setMyLocationEnabled(true);//Makes the users current location visible by displaying a blue dot.
                criteria = new Criteria();

                locMgr =(LocationManager)getSystemService(LOCATION_SERVICE);//use of location services by firstly defining location manager.

                provider= locMgr.getBestProvider(criteria, true);
                Log.i("Provider","Provider: "+provider);
                locMgr.requestLocationUpdates(provider,100, 0, this);
                if(provider==null){
                    onProviderDisabled(provider);
                }

                location= locMgr.getLastKnownLocation(provider);
                Log.i("GPS","Location: "+location);
                if (location!=null){
                    onLocationChanged(location);
                }
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        removeGeoFence();
        Toast.makeText(MapActivity.this,"Location changed: "+location, Toast.LENGTH_SHORT);
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        addGeoFence(30f, latLng);
    }

    public void addGeoFence(float radius, LatLng latLng) {
        geofence = new Geofence.Builder().setRequestId("1")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(location.getLatitude(), location.getLongitude(), radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE).build();

        user = googleMap.addMarker(new MarkerOptions().position(latLng));

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)   //set center
                .radius(radius)   //set radius in meters
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);

        circle = googleMap.addCircle(circleOptions);
    }

    public void removeGeoFence(){
        if(user!=null && circle!=null){
            //LocationServices.GeofencingApi.removeGeofences()
            user.remove();
            circle.remove();
        }
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
}
