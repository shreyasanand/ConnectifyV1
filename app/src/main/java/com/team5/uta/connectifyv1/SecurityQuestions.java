package com.team5.uta.connectifyv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SecurityQuestions extends Activity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner01;
    private Spinner spinner02;
    private Spinner spinner03;
    private static final String[]paths1 = {"First Crush", "Pet name", "Favourite Cuisine"};
    private static final String[]paths2 = {"Favourite sports", "Mothers Maiden Name", "High School"};
    private static final String[]paths3 = {"First Car Model", "Favourite City", "First Company"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_questions);

        spinner01 = (Spinner)findViewById(R.id.spinner5);
        spinner02 = (Spinner)findViewById(R.id.spinner6);
        spinner03 = (Spinner)findViewById(R.id.spinner7);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(SecurityQuestions.this,
                android.R.layout.simple_spinner_item,paths1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(SecurityQuestions.this,
                android.R.layout.simple_spinner_item,paths2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(SecurityQuestions.this,
                android.R.layout.simple_spinner_item,paths3);


        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner01.setAdapter(adapter1);
        spinner01.setOnItemSelectedListener(this);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner02.setAdapter(adapter2);
        spinner02.setOnItemSelectedListener(this);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner03.setAdapter(adapter3);
        spinner03.setOnItemSelectedListener(this);

        Button next = (Button)findViewById(R.id.button3);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interestActivity = new Intent(SecurityQuestions.this, AddInterestActivity.class);
                User user = (User)getIntent().getSerializableExtra("user");
                interestActivity.putExtra("user", user);
                startActivity(interestActivity);
            }
        });

        next.setFocusable(true);
    }


    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_security_questions, menu);
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
