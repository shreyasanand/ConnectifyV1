package com.team5.uta.connectifyv1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.team5.uta.connectifyv1.adapter.Interest;
import com.team5.uta.connectifyv1.adapter.InterestDataAdapter;

import java.util.ArrayList;


public class UserProfile extends ActionBarActivity {

    GridView grid;
    TextView username;
    ArrayList<Interest> selectedInterests =  new ArrayList<Interest>();
    User user = null;
    ImageView userImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f5793f")));

        this.user = (User)getIntent().getSerializableExtra("user");
        this.selectedInterests = this.user.getInterests();


        final Interest[] interestPool = new Interest[selectedInterests.size()];
        for(int i=0;i<selectedInterests.size();i++) {
            Interest interest = selectedInterests.get(i);
            interestPool[i] = interest;
        }


        final InterestDataAdapter adapter = new InterestDataAdapter(UserProfile.this, interestPool);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);

        username = (TextView)findViewById(R.id.username);
        username.setText(user.getFname()+" "+user.getLname());

        userImage = (ImageView)findViewById(R.id.imageView);
        userImage.setImageResource(R.drawable.photo);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.interests:
                //Toast.makeText(getBaseContext(), "You selected Interests", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, AddInterestActivity.class);
                startActivity(intent2);
                break;
        }
        return true;
    }
}
