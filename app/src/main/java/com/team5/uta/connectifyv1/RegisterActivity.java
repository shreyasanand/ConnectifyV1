package com.team5.uta.connectifyv1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends ActionBarActivity {

    public String TAG = "register_activity";
    public static final String PREFS_NAME = "UserData";
    public Object output = null;
    private HttpPost httppost;
    private HttpResponse response;
    private InputStream is;
    private HttpWrapper httpWrapper;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5793f")));

        final EditText first_name = (EditText) findViewById(R.id.first_name);
        final EditText last_name= (EditText) findViewById(R.id.last_name);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText confirm_password = (EditText) findViewById(R.id.confirm_password);
        final EditText email= (EditText) findViewById(R.id.email);
        final Button sign_up=(Button) findViewById(R.id.signup_button);
        final int duration=Toast.LENGTH_SHORT;
        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_APPEND);
        httpWrapper = new HttpWrapper();


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                if ((first_name.getText().toString().trim().equals("")) || (last_name.getText().toString().trim().equals("")) ||
                        (password.getText().toString().trim().equals("")) || (confirm_password.getText().toString().trim().equals("")) ||
                        (email.getText().toString().trim().equals(""))) {
                    Toast.makeText(getApplicationContext(), "All Fields are Required.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!(password.getText().toString().equals(confirm_password.getText().toString()))) {
                        Toast.makeText(getApplicationContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    } else {

                        if (checkEmail(email.getText().toString())) {
                            //Toast.makeText(getApplicationContext(),"Succesfully logged in",Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("FirstName", first_name.getText().toString());
                            editor.putString("LastName", last_name.getText().toString());
                            editor.putString("Password", password.getText().toString());
                            editor.putString("Email", email.getText().toString());
                            editor.commit();

                            String uid = UUID.randomUUID().toString();
                            String fname = first_name.getText().toString();
                            String lname = last_name.getText().toString();
                            String pwd = password.getText().toString();
                            byte[] hashedPwd = getHash(pwd);
                            String uemail = email.getText().toString();

                            user = new User(uid, fname, lname, pwd, uemail, null, null);

                            // declare parameters that are passed to PHP script
                            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

                            // define the parameter
                            postParameters.add(new BasicNameValuePair("id",user.getUid()));
                            postParameters.add(new BasicNameValuePair("firstname",fname));
                            postParameters.add(new BasicNameValuePair("lastname",lname));
                            postParameters.add(new BasicNameValuePair("emailid",uemail));
                            postParameters.add(new BasicNameValuePair("password",pwd));

                            httpWrapper.setPostParameters(postParameters);

                            //http post
                            try{
                                httppost = new HttpPost("http://omega.uta.edu/~sxa1001/register.php");
                                httpWrapper.setRegisterActivity(RegisterActivity.this);
                                httpWrapper.execute(httppost);
                            }
                            catch(Exception e){
                                Log.e("register_activity", "Error in http connection "+e.toString());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid email id.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch(Exception e) {
                    Log.i(TAG,e.getMessage());
                }
            }

            public boolean checkEmail(String email) {
                Pattern pattern = Pattern.compile("[A-Za-z]+\\.[A-Za-z]+@mavs\\.uta\\.edu$");
                Matcher matcher = pattern.matcher(email);
                return matcher.matches();
            }
        });
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

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void registerResult(String status) {
        if(status.equals("Success")) {
            Log.i(TAG,"Inside registerResult : Success");
            Intent securityQuestionsActivity = new Intent(RegisterActivity.this, SecurityQuestions.class);
            securityQuestionsActivity.putExtra("user", user);
            startActivity(securityQuestionsActivity);
        } else {
            Toast.makeText(getApplicationContext(),"Registration failed",Toast.LENGTH_SHORT).show();
        }
    }

    public byte[] getHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        return input;
    }
}