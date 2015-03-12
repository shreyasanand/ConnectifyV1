package com.team5.uta.connectifyv1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.ResultSet;
import java.util.Random;

public class LoginActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "UserData";
    public DBConnection db = null;
    public ResultSet output = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5793f")));

        final Button go = (Button) findViewById(R.id.btn_login);

        SharedPreferences userData = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        final String uname = (String)userData.getString("Email", "");
        final String password = (String)userData.getString("Password", "");

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

                if(uname.isEmpty() ||
                        password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No stored username or password", Toast.LENGTH_LONG).show();
                    return;
                }

                if(email.getText().toString().equalsIgnoreCase(uname) &&
                        pwd.getText().toString().equalsIgnoreCase(password)) {
                    Toast.makeText(getApplicationContext(), "Awesome ! You are IN !", Toast.LENGTH_LONG).show();

                    Intent mapActivity = new Intent(LoginActivity.this, MapActivity.class);
                    startActivity(mapActivity);
                } else {

                    try {

                        db = new DBConnection();
                        db.execute("select * from connectifydb.user where user_email='"+ email.getText().toString() +"'", true);

                        output = (ResultSet)db.getResult();
                        while (output.next()) {
                            String email = output.getString("user_email");
                            if(email.equalsIgnoreCase(uname)) {
                                Toast.makeText(getApplicationContext(), "Awesome ! You are IN !", Toast.LENGTH_LONG).show();

                                Intent mapActivity = new Intent(LoginActivity.this, MapActivity.class);
                                startActivity(mapActivity);
                            } else {
                                Toast.makeText(getApplicationContext(), "Oops ! Username or Password is wrong !", Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Oops ! Username or Password is wrong !", Toast.LENGTH_LONG).show();
                    }


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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}