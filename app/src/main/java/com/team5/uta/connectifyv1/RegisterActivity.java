package com.team5.uta.connectifyv1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText first_name = (EditText) findViewById(R.id.first_name);
        final EditText last_name= (EditText) findViewById(R.id.last_name);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText confirm_password = (EditText) findViewById(R.id.confirm_password);
        final EditText email= (EditText) findViewById(R.id.email);
        final Button sign_up=(Button) findViewById(R.id.signup_button);
        final int duration=Toast.LENGTH_SHORT;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((first_name.getText().toString().trim().equals(""))||(last_name.getText().toString().trim().equals(""))||
                        (password.getText().toString().trim().equals(""))||(confirm_password.getText().toString().trim().equals(""))||
                        (email.getText().toString().trim().equals("")))
                {
                   Toast.makeText(getApplicationContext(),"All Fields are Required.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!(password.getText().toString().equals(confirm_password.getText().toString()))) {
                        Toast.makeText(getApplicationContext(),"Passwords do not match.",Toast.LENGTH_SHORT).show();
                    }
                    else {

                        if(checkEmail(email.getText().toString())) {
                            //Toast.makeText(getApplicationContext(),"Succesfully logged in",Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("FirstName",first_name.getText().toString());
                            editor.putString("LastName",last_name.getText().toString());
                            editor.putString("Password",password.getText().toString());
                            editor.putString("Email",email.getText().toString());
                            editor.commit();
                            //Toast ex= Toast.makeText(getApplicationContext(),prefs.getString("FirstName",null),duration);
                            //ex.show();

                            Intent addInterestsActivity = new Intent(RegisterActivity.this, AddInterestActivity.class);
                            startActivity(addInterestsActivity);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Invalid email id.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            public boolean checkEmail(String email) {
                //Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}