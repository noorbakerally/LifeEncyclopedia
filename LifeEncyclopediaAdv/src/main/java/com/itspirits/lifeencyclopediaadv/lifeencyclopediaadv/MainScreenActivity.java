package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities.AboutActivity;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities.SearchActivity;

public class MainScreenActivity extends ActionBarActivity {

    Button btnAbout;
    Button btnSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        btnAbout = (Button)findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent aboutActivity = new Intent(getApplicationContext(),AboutActivity.class);
                startActivity(aboutActivity);
            }
        });

        Button btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent searchActivity = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(searchActivity);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}
