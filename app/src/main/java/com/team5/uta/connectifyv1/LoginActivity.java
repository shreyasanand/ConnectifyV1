package com.team5.uta.connectifyv1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.ArrayList;

public class LoginActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "UserData";
    public ResultSet output = null;
    private HttpWrapper httpWrapper;
    private HttpPost httppost;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5793f")));

        httpWrapper = new HttpWrapper();
        final Button go = (Button) findViewById(R.id.btn_login);

//        SharedPreferences userData = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        final String uname = (String)userData.getString("Email", "");
//        final String password = (String)userData.getString("Password", "");

        final EditText email = (EditText) findViewById(R.id.txt_login_email);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if (email.getText().toString().equalsIgnoreCase("UTA Email ID")) {email.setText("");}
                }else {
                    if (email.getText().toString().isEmpty()) {email.setText(R.string.login_email_id);}
                }
            }
        });

        final EditText pwd = (EditText) findViewById(R.id.txt_password);
        pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if (pwd.getText().toString().equalsIgnoreCase("password")) {pwd.setText("");}
                }else {
                    if (pwd.getText().toString().isEmpty()) {pwd.setText(R.string.login_pwd);}
                }
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(email == null || pwd == null) {
                    Toast.makeText(getApplicationContext(), "Please enter email id and password", Toast.LENGTH_LONG).show();
                    return;
                }

                // declare parameters that are passed to PHP script
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

                // define the parameter
                postParameters.add(new BasicNameValuePair("username",email.getText().toString()));
                postParameters.add(new BasicNameValuePair("password",pwd.getText().toString()));

                httpWrapper.setPostParameters(postParameters);

                //http post
                try{
                    httppost = new HttpPost("http://omega.uta.edu/~sxa1001/login.php");
                    httpWrapper.setLoginActivity(LoginActivity.this);
                    httpWrapper.execute(httppost);
                }
                catch(Exception e){
                    Log.e("register_activity", "Error in http connection " + e.toString());
                }

            }
        });

        go.setFocusable(true);

        Button register = (Button)findViewById(R.id.btn_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void loginResult(String result) {
        if(result.contains("Success")) {
            try {
                JSONObject jObject  = new JSONObject(result);
                String userid = jObject.getString("userid");
                String fname = jObject.getString("user_Fname");
                String lname = jObject.getString("user_Lname");
                String email = jObject.getString("user_email");
                user = new User(userid,fname,lname,null,email,null,null);
                Intent mapActivity = new Intent(LoginActivity.this, MapActivity.class);
                mapActivity.putExtra("user", user);
                startActivity(mapActivity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
        }
    }
}