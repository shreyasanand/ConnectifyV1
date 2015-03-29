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
    private ArrayList<NameValuePair> postParameters;
    private InputStream inputStream = null;
    private LoginActivity loginActivity;
    private RegisterActivity registerActivity;

    @Override
    protected InputStream doInBackground(HttpPost... httppost) {
        Log.i("http_wrapper","Inside doInBackground");
        InputStream is = null;
        try{
            HttpClient httpclient = new DefaultHttpClient();
            //HttpPost httpPost = new HttpPost("http://omega.uta.edu/~sxa1001/register.php");
            HttpPost httpPost = httppost[0];
            Log.i("http_wrapper",httpPost.toString());
            httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        }
        catch(Exception e){
            Log.e("http_wrapper", "Error in http connection " + e.toString());
        }
        this.inputStream = is;
        return is;
    }

    @Override
    protected void onPostExecute(InputStream is) {
        Log.i("http_wrapper","Inside onPostExecute");
        String result1 = responseToString(is);
        String status;

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
}
