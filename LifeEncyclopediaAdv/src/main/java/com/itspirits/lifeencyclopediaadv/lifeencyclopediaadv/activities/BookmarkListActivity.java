package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.MainScreenActivity;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.internal.widget.ActionBarOverlayLayout;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.adapters.ArticleArrayAdapter;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.Constants;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.LifeObject;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities.HeadlinesFragment.OnHeadlineSelectedListener;
import android.widget.AdapterView.OnItemClickListener;
public class BookmarkListActivity extends ListActivity {

    List <LifeObject> lifeObjects = new ArrayList<LifeObject>();
    OnHeadlineSelectedListener mCallback;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = R.layout.article_row;

        //getting all bookmarks from the shared preferences
        Map<String,?> keys = PreferenceManager.getDefaultSharedPreferences(this).getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            System.out.println(entry.toString());
            if (entry.getKey().toString().contains("LO:")){
                String objStr = entry.getValue().toString();
                Gson gson = new Gson();
                LifeObject lifeObject = gson.fromJson(objStr, LifeObject.class);
                lifeObjects.add(lifeObject);
            }
        }
        ArticleArrayAdapter bookmarkAdapter = new ArticleArrayAdapter(this, layout,lifeObjects );
        setListAdapter(bookmarkAdapter);

        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                LifeObject lifeObject = (LifeObject) parent.getAdapter().getItem(position);
                Constants.lifeObjects.removeAll(Constants.lifeObjects);

                Intent launchBrowser = new Intent(getApplicationContext(), MainActivity.class);

                launchBrowser.putExtra(SearchActivity.SELECTED_ENTITY, lifeObject.getEntity());
                launchBrowser.putExtra(SearchActivity.SELECTED_ENTITY_TYPE, lifeObject.getEntity_type());
                launchBrowser.putExtra(SearchActivity.SELECTED_LABEL, lifeObject.getLabel());
                launchBrowser.putExtra(SearchActivity.PARENT, "BOOKMARK");
                startActivity(launchBrowser);

            }
        });
    }

    //MENU GOES HERE
    /***************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        menu.findItem(R.id.action_bookmark).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        Intent intent;

        return false;
    }


    /*****************************************************************/
}
