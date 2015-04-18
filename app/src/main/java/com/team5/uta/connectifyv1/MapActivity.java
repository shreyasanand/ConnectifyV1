package com.team5.uta.connectifyv1;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.team5.uta.connectifyv1.adapter.Interest;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapActivity extends ActionBarActivity implements LocationListener,
                                                        GoogleApiClient.ConnectionCallbacks,
                                                        GoogleApiClient.OnConnectionFailedListener,
                                                        ResultCallback<Status> {

    final int RQS_GooglePlayServices = 1;
    private GoogleMap googleMap = null;
    private LatLng latLng;
    private LocationManager locMgr;
    private String provider;
    private Location location;
    private Geofence geofence = null;
    private Criteria criteria;
    private Marker userMarker;
    private Marker otherUserMarker;
    private Circle circle;
    protected GoogleApiClient mGoogleApiClient = null;
    private PendingIntent mGeofencePendingIntent;
    List<Geofence> mGeofenceList;
    private User user = null;
    private Button mAddGeofencesButton;
    private boolean mGeofencesAdded;
    private int commonInterestsCount = 0;
    private HttpWrapper httpWrapper1;
    private HttpWrapper httpWrapper2;
    private HttpPost httppost1;
    private HttpPost httppost2;
    private String TAG = "map_activity";
    private LatLng otherUserLocation = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        this.user = (User)getIntent().getSerializableExtra("user");
        mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5793f")));
        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;
        httpWrapper1 = new HttpWrapper();
        httpWrapper2 = new HttpWrapper();
        setButtonsEnabledState();
        buildGoogleApiClient();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.user = (User)getIntent().getSerializableExtra("user");
        if(this.user==null) {
            Toast.makeText(getApplicationContext(),
                    "User is NULL",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Welcome : "+this.user.getFname()+" "+this.user.getLname(),
                    Toast.LENGTH_SHORT).show();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS){
//            Toast.makeText(getApplicationContext(),
//                    "isGooglePlayServicesAvailable SUCCESS",
//                    Toast.LENGTH_SHORT).show();
            Log.i("Google play services","isGooglePlayServicesAvailable SUCCESS");
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
            //invoke of map fragment by id from main xml file
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (googleMap != null) {
                //Makes the users current location visible by displaying a blue dot.
                googleMap.setMyLocationEnabled(true);
                criteria = new Criteria();

                //use of location services by firstly defining location manager.
                locMgr =(LocationManager)getSystemService(LOCATION_SERVICE);

                provider= locMgr.getBestProvider(criteria, true);
                Log.i(TAG,"Provider: "+provider);
                locMgr.requestLocationUpdates(provider,100, 20, this);
                if(provider==null){
                    onProviderDisabled(provider);
                }

                location= locMgr.getLastKnownLocation(provider);
                Log.i(TAG,"Location: "+location);
                latLng = new LatLng(location.getLatitude(),location.getLongitude());

                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

                // define the parameter
                postParameters.add(new BasicNameValuePair("user_id",user.getUid()));
                postParameters.add(new BasicNameValuePair("lat",String.valueOf(location.getLatitude())));
                postParameters.add(new BasicNameValuePair("lng",String.valueOf(location.getLongitude())));

                httpWrapper2.setPostParameters(postParameters);

                //http post
                try{
                    httppost2 = new HttpPost("http://omega.uta.edu/~sxa1001/save_location.php");
                    httpWrapper2.setMapActivity(this);
                    httpWrapper2.execute(httppost2);
                }
                catch(Exception e){
                    Log.e(TAG, "Error in http connection " + e.toString());
                }
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // Remove previous geofence if any
        if(!mGeofenceList.isEmpty()) {
            removeGeoFence();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        // define the parameter
        postParameters.add(new BasicNameValuePair("user_id",user.getUid()));
        postParameters.add(new BasicNameValuePair("lat",String.valueOf(location.getLatitude())));
        postParameters.add(new BasicNameValuePair("lng",String.valueOf(location.getLongitude())));

        HttpWrapper httpWrapper1 = new HttpWrapper();
        HttpWrapper httpWrapper2 = new HttpWrapper();
        httpWrapper1.setPostParameters(postParameters);

        //http post
        try{
            HttpPost httppost1 = new HttpPost("http://omega.uta.edu/~sxa1001/save_location.php");
            HttpPost httppost2 = new HttpPost("http://omega.uta.edu/~sxa1001/get_users_location.php");
            httpWrapper1.setMapActivity(this);
            httpWrapper2.setMapActivity(this);
            //httpWrapper1.execute(httppost1);
            httpWrapper2.execute(httppost2);
        }
        catch(Exception e){
            Log.e(TAG, "Error in http connection " + e.toString());
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        addGeoFence(Constants.GEOFENCE_RADIUS, latLng);
        //this.user.setCurrentLocation(location);
    }

    public void addGeoFence(float radius, LatLng latLng) {
        Toast.makeText(getApplicationContext(),"Adding geofence",Toast.LENGTH_SHORT).show();
        geofence = new Geofence.Builder().setRequestId("1")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(location.getLatitude(), location.getLongitude(), radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE).build();

        userMarker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.currentuser_icon))
        );

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)   //set center
                .radius(30)   //set radius in meters
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);

        circle = googleMap.addCircle(circleOptions);

        mGeofenceList.add(geofence);

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "mGoogleApiClient "+getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
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

    public void addGeofencesButtonHandler(View view) {
        //Toast.makeText(getApplicationContext(), "Inside addGeofence : Adding geofence", Toast.LENGTH_SHORT).show();
        Log.i("Geofence","Adding geofence");
        geofence = new Geofence.Builder().setRequestId("1")
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(location.getLatitude(), location.getLongitude(), 30)
                .setExpirationDuration(Geofence.NEVER_EXPIRE).build();

        userMarker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.currentuser_icon))
        );

//        Log.i(TAG,"Adding other user marker");
//        otherUserMarker = googleMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(32.73201728,-97.11315209))
//        );
//        Log.i(TAG,"Other user marker lat: "+otherUserMarker.getPosition().latitude);

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)   //set center
                .radius(30)   //set radius in meters
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2);

        circle = googleMap.addCircle(circleOptions);

        mGeofenceList.add(geofence);

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "mGoogleApiClient "+getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
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
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.home:
                //Toast.makeText(getBaseContext(), "You selected Home", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;

            case R.id.profile:
                //Toast.makeText(getBaseContext(), "You selected Profile", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, UserProfile.class);
                intent1.putExtra("user", user);
                startActivity(intent1);
                break;

            case R.id.interests:
                //Toast.makeText(getBaseContext(), "You selected Interests", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, AddInterestActivity.class);
                startActivity(intent2);
                break;

        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("GoogleApiClient", "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {
        if(status.isSuccess()) {
            mGeofencesAdded = !mGeofencesAdded;
            Log.i("Result","Geofence added successfully");
            setButtonsEnabledState();
        } else {
            Log.i("Result","Geofence not added successfully");
        }
    }

    private void setButtonsEnabledState() {
        if (mGeofencesAdded) {
            mAddGeofencesButton.setEnabled(false);
        } else {
            mAddGeofencesButton.setEnabled(true);
        }
    }

    private int getCommonInterestsCount(ArrayList<Interest> otherUserInterests) {
        ArrayList<Interest> myInterests = this.user.getInterests();

        Toast.makeText(MapActivity.this,"My interests: "+myInterests.size(),Toast.LENGTH_SHORT).show();
        Toast.makeText(MapActivity.this,"Other user interests: "+otherUserInterests.size(),Toast.LENGTH_SHORT).show();

        ArrayList<String> myInterestText = new ArrayList<String>(myInterests.size());
        ArrayList<String> otherUserInterestText = new ArrayList<String>(otherUserInterests.size());


        for(Interest i: myInterests) {
            myInterestText.add(i.getInterestText());
        }

        for(Interest i: otherUserInterests) {
            otherUserInterestText.add(i.getInterestText());
        }

        Set<String> common = new HashSet<>(myInterestText);
        common.retainAll(new HashSet<String>(otherUserInterestText));
        this.commonInterestsCount = common.size();

        Toast.makeText(MapActivity.this,"Common interest count: "+common.size(),Toast.LENGTH_SHORT).show();
        if(otherUserMarker!=null) {
            otherUserMarker.remove();
        }

        Log.i(TAG,"Adding marker");
        Toast.makeText(MapActivity.this,"Adding other user marker",Toast.LENGTH_SHORT).show();
        otherUserMarker = googleMap.addMarker(new MarkerOptions()
                        .position(otherUserLocation)
                        .title(String.valueOf(this.commonInterestsCount))
                        .icon(BitmapDescriptorFactory.fromBitmap(drawTextToBitmap(MapActivity.this,R.drawable.marker,String.valueOf(this.commonInterestsCount))))
        );
        Toast.makeText(MapActivity.this,"Other user marker lat: "+otherUserLocation.latitude,Toast.LENGTH_SHORT).show();

        return commonInterestsCount;
    }

    public int commonTwo(String[] a, String[] b) {
        Set<String> common = new HashSet<>(Arrays.asList(a));
        common.retainAll(new HashSet<>(Arrays.asList(b)));
        return common.size();
    }

    public void getOtherUserInterestResult(String result) {
        try {
            JSONObject jObject  = new JSONObject(result);
            String interests = jObject.getString("interests");
            Log.i(TAG,"Final interests: "+interests);
            ArrayList<Interest> selectedInterest = new ArrayList<Interest>(5);

            String[] rawInterests = interests.split(",");
            for(int i = 0;i<rawInterests.length;i++) {
                String[] interestItem = rawInterests[i].split(":");
                String interestText = interestItem[0];
                int interestImageId = Integer.valueOf(interestItem[1].trim());
                Interest interest = new Interest(interestText,interestImageId);
                selectedInterest.add(interest);
            }

            this.getCommonInterestsCount(selectedInterest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getUsersLocationResult(String result) {
        if(result.contains("Success")) {
            try {
                JSONObject jObject  = new JSONObject(result);
                JSONObject loc1 = jObject.getJSONObject("0");
                JSONObject loc2 = jObject.getJSONObject("4");

                double lat1 = loc1.getDouble("lat");
                double lng1 = loc1.getDouble("lng");

                String user_id = loc2.getString("user_id");
                double lat2 = loc2.getDouble("lat");
                double lng2 = loc2.getDouble("lng");

                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

                // define the parameter
                postParameters.add(new BasicNameValuePair("user_id",user_id));

                HttpWrapper httpWrapper = new HttpWrapper();
                httpWrapper.setPostParameters(postParameters);

                //http post
                try{
                    HttpPost httppost = new HttpPost("http://omega.uta.edu/~sxa1001/get_other_user_interest.php");
                    httpWrapper.setMapActivity(this);
                    httpWrapper.execute(httppost);
                }
                catch(Exception e){
                    Log.e(TAG, "Error in http connection " + e.toString());
                }

                Log.i(TAG,"Other user Lat: "+lat2);
                Log.i(TAG,"Other user Lng: "+lng2);

                LatLng latLng1 = new LatLng(lat2,lng2);
                this.otherUserLocation = latLng1;

//                if(otherUserMarker!=null) {
//                    otherUserMarker.remove();
//                }
//
//                Log.i(TAG,"Adding marker");
//                Toast.makeText(MapActivity.this,"Adding other user marker",Toast.LENGTH_SHORT).show();
//                otherUserMarker = googleMap.addMarker(new MarkerOptions()
//                        .position(latLng1)
//                        .title(String.valueOf(this.commonInterestsCount))
//                );
//                Toast.makeText(MapActivity.this,"Other user marker lat: "+latLng1.latitude,Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap drawTextToBitmap(Context gContext,int gResId,String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, gResId);

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize((int) (15 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2;

        canvas.drawText(gText, x * scale, y * scale, paint);

        return bitmap;
    }

}
