package com.team5.uta.connectifyv1;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *  Wrapper class to send Http requests to the server
 */
public class HttpWrapper extends AsyncTask<HttpPost, Void, InputStream> {

    private BufferedReader reader;
    private StringBuilder sb;
    private String line, result1;
    private ArrayList<NameValuePair> postParameters = null;
    private InputStream inputStream = null;
    private LoginActivity loginActivity;
    private RegisterActivity registerActivity;
    private AddInterestActivity addInterestActivity;
    private UserProfile userProfileActivity;
    private MapActivity mapActivity;
    private String TAG = "http_wrapper";

    @Override
    protected InputStream doInBackground(HttpPost... httppost) {
        InputStream is = null;
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = httppost[0];
            Log.i(TAG,httpPost.getURI().toString());
            if(postParameters!=null) {
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
            }
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        }
        catch(Exception e){
            Log.e(TAG, "Error in http connection " + e.toString());
        }
        this.inputStream = is;
        return is;
    }

    @Override
    protected void onPostExecute(InputStream is) {
        String result1 = responseToString(is);
        String status;

        Log.i(TAG,result1.toString());

        if(result1.contains("Register")) {
            if(result1.contains("Success")) {
                status = "Success";
                this.registerActivity.registerResult(status);
            } else {
                status = "Fail";
                this.registerActivity.registerResult(status);
            }
        } else if(result1.contains("Login")) {
            if(result1.contains("Success")) {
                status = "Success";
                this.loginActivity.loginResult(result1);
            } else {
                status = "Fail";
                this.loginActivity.loginResult(result1);
            }
        } else if(result1.contains("Get interest")) {
            //this.mapActivity.openUserProfile(result1);
            this.loginActivity.getInterestFromLoginResult(result1);
        } else if(result1.contains("Get Location Success")) {
            this.mapActivity.getUsersLocationResult(result1);
        } else if(result1.contains("Get other user interest")) {
            this.mapActivity.getOtherUserInterestResult(result1);
        }
    }

    public String responseToString(InputStream ins)
    {
        //convert response to string
        try{
            reader = new BufferedReader(new InputStreamReader(ins,"iso-8859-1"),8);
            sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            ins.close();

            result1 = sb.toString();
            return result1;

        }

        catch(Exception e)
        {
            Log.e("log_tag", "Error converting result "+e.toString());
            return "Error converting result "+e.toString();
        }
    }



    public ArrayList<NameValuePair> getPostParameters() {
        return postParameters;
    }

    public void setPostParameters(ArrayList<NameValuePair> postParameters) {
        this.postParameters = postParameters;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public LoginActivity getLoginActivity() {
        return loginActivity;
    }

    public void setLoginActivity(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    public RegisterActivity getRegisterActivity() {
        return registerActivity;
    }

    public void setRegisterActivity(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }

    public AddInterestActivity getAddInterestActivity() {
        return addInterestActivity;
    }

    public void setAddInterestActivity(AddInterestActivity addInterestActivity) {
        this.addInterestActivity = addInterestActivity;
    }

    public UserProfile getUserProfileActivity() {
        return userProfileActivity;
    }

    public void setUserProfileActivity(UserProfile userProfileActivity) {
        this.userProfileActivity = userProfileActivity;
    }

    public MapActivity getMapActivity() {
        return mapActivity;
    }

    public void setMapActivity(MapActivity mapActivity) {
        this.mapActivity = mapActivity;
    }

}
