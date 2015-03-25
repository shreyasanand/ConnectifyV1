package com.team5.uta.connectifyv1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.team5.uta.connectifyv1.adapter.Interest;
import com.team5.uta.connectifyv1.adapter.InterestDataAdapter;

import java.util.ArrayList;
import java.util.Arrays;


public class AddInterestActivity extends ActionBarActivity {

    GridView grid;
    ArrayList<Interest> selectedInterests =  new ArrayList<Interest>();
    String[] interestText = {
            "Anime",
            "Big Data",
            "Blogging",
            "Cats",
            "Music",
            "Cross-fit",
            "Cricket",
            "Dogs",
            "Drones",
            "GOT",
            "Hacking",
            "Motocross",
            "Paranormal",
            "Photography",
            "Seafood",
            "Tex-Mex",
            "UI Design",
            "Wine",
            "Yoga"
    };

    int[] interestImage = {
            R.drawable.anime,
            R.drawable.bigdata,
            R.drawable.blogging,
            R.drawable.cats,
            R.drawable.countrymusic,
            R.drawable.crossfit,
            R.drawable.cricket,
            R.drawable.dogs,
            R.drawable.drones,
            R.drawable.got,
            R.drawable.hacking,
            R.drawable.motocross,
            R.drawable.paranormal,
            R.drawable.photography,
            R.drawable.seafood,
            R.drawable.texmex,
            R.drawable.uidesign,
            R.drawable.wine,
            R.drawable.yoga
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interest);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5793f")));

        final Interest[] interestPool = new Interest[interestText.length];
        for(int i=0;i<interestText.length;i++) {
            Interest interest = new Interest(interestText[i], interestImage[i]);
            interestPool[i] = interest;
        }

        final InterestDataAdapter adapter = new InterestDataAdapter(AddInterestActivity.this, interestPool);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setBackgroundResource(R.drawable.grid_color_selector);
                if(grid.isItemChecked(position)) {
                    selectedInterests.add(interestPool[+position]);
                    //Toast.makeText(AddInterestActivity.this, interestText[+position]+ " : SELECTED", Toast.LENGTH_SHORT).show();
                    Log.i("myTag",Arrays.toString(selectedInterests.toArray()));
                } else {
                    selectedInterests.remove(interestPool[+position]);
                    //Toast.makeText(AddInterestActivity.this, interestText[+position]+ " : UNSELECTED", Toast.LENGTH_SHORT).show();
                    Log.i("myTag",Arrays.toString(selectedInterests.toArray()));
                }
            }
        });

        Button nextButton = (Button)findViewById(R.id.nextbutton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedInterests.size() > 0) {
                    Intent home = new Intent(AddInterestActivity.this, MapActivity.class);
                    User user = (User)getIntent().getSerializableExtra("user");
                    user.setInterests(selectedInterests);
                    home.putExtra("user", user);
                    startActivity(home);
                } else {
                    Toast.makeText(AddInterestActivity.this, "Please select at least one", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_interest, menu);
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