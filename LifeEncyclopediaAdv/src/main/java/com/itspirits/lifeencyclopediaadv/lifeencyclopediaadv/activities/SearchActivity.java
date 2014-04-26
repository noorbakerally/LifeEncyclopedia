package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.toolbox.Volley;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.MainScreenActivity;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.R;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.adapters.DirectSubclassArrayAdapter;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.Constants;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.DataBaseHelper;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.LifeObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ActionBarActivity {


    private AutoCompleteTextView edtSearch;
    private Button btnSearch;
    private ListView lstSearchResults;
    public static String SELECTED_ENTITY = "SELECTED_ENTITY";
    public static String SELECTED_LABEL = "SELECTED_LABEL";
    public static String SELECTED_ENTITY_TYPE = "SELECTED_ENTITY_TYPE";
    public static String PARENT = "PARENT";
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        Constants.queue = Volley.newRequestQueue(this);


        edtSearch = (AutoCompleteTextView)findViewById(R.id.edtSearch);
        edtSearch.setThreshold(1);

        edtSearch.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                searchLifeObjects(edtSearch.getText().toString());
            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchLifeObjects(edtSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });


        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                List<LifeObject> matchedList = getListLifeObjects(arg0.toString(),true);

                List <String> strMatchedList = new ArrayList<String>();
                for (LifeObject currentObj:matchedList){
                    strMatchedList.add(currentObj.getLabel());
                }
                updateAutocomplete(strMatchedList);
            }});


        btnSearch = (Button)findViewById(R.id.btnSearch);
        Log.d("button",btnSearch+"");
        btnSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                searchLifeObjects(edtSearch.getText().toString());
            }
        });


        lstSearchResults = (ListView)findViewById(R.id.lstSearchResults);
        lstSearchResults.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {

                /*
                LifeObject lifeObject = (LifeObject) arg0.getAdapter().getItem(position);
                Constants.lifeObjects.removeAll(Constants.lifeObjects);

                Intent launchBrowser = new Intent(getApplicationContext(), MainActivity.class);

                launchBrowser.putExtra(SELECTED_ENTITY, lifeObject.getEntity());
                launchBrowser.putExtra(SELECTED_ENTITY_TYPE, lifeObject.getEntity_type());
                launchBrowser.putExtra(SELECTED_LABEL, lifeObject.getLabel());
                startActivity(launchBrowser);
                */
            }
        });

    }
    public void updateAutocomplete(List <String> strMatchedList){
        if (strMatchedList.size() > 0){
            int maxR = (strMatchedList.size() == 5)? 5:strMatchedList.size();
            ArrayAdapter<String> autocompleteCountriesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, strMatchedList.subList(0, maxR));
            edtSearch.setAdapter(autocompleteCountriesAdapter);
        }
        progress.dismiss();

    }
    //killing the activity on pressing the back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    public List <LifeObject> getListLifeObjects(final String searchText,boolean auto){

        progress = ProgressDialog.show(this, "Connecting to Server",
                "Searching "+searchText+" .....", true);


        DataBaseHelper myDbHelper = new DataBaseHelper(this);


        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDataBase();

        }catch(SQLException sqle){

            throw sqle;

        }

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        String[] COLUMNS = {"entity","entity_type","label","direct_subclasses","description","common_names","scientific_names","kingdomname","phylumname","classname","ordername","familyname","genusname","image","thumbnail","sound"};
        String sql;
        if (auto){
            sql = "select entity,label from life where label like \"searchText%\" order by label asc";
        }else{
            sql = "select entity,label from life where entity like \"%searchText%\" or entity_type like \"%searchText%\" or " +
                    "label like \"%searchText%\" or common_names like \"%searchText%\" or scientific_names like \"%searchText%\" order by label asc";
        }




        // 2. build query

        sql = sql.replace("searchText", searchText);
        ;
        Cursor cursor = db.rawQuery(sql, null); // h. limit

        // 3. if we got results get the first one
        List <LifeObject> searchObjs = new ArrayList<LifeObject>();
        if (cursor != null){
            int cursorCount = cursor.getCount()-1;
            while (cursorCount >=0) {
                cursor.moveToPosition(cursorCount);
                cursorCount--;
                if (cursor.getString(1).length() ==0){
                    continue;
                }
                LifeObject n = new LifeObject();
                n.setEntity(cursor.getString(0));
                n.setLabel(cursor.getString(1));
                searchObjs.add(n);

            }
        }
        db.close();



        cursor.close();
        return searchObjs;

    }
    public void searchLifeObjects (final String searchText){

        List <LifeObject> searchObjs = new ArrayList<LifeObject>();
        searchObjs = getListLifeObjects(searchText,false);
        if (searchObjs.size() ==0){
            showNoResultAlert(searchText);
        }
        edtSearch.dismissDropDown();
        updateList(searchObjs);
        progress.dismiss();

    }

    public void updateList(List <LifeObject> lifeObjects){
        lstSearchResults.setAdapter(new DirectSubclassArrayAdapter(this, R.layout.direct_subclass_row,lifeObjects));
    }






    void showNoResultAlert(String searchText){
        new AlertDialog.Builder(this)
                .setTitle("Search Status")
                .setPositiveButton("OK",null)
                .setMessage("No results was found for "+searchText)
                .show();
    }
    //MENU GOES HERE
    /***************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_share:
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Constants.SHARE_STRING);
                startActivity(Intent.createChooser(intent, "Share"));
                return true;

            case R.id.action_show_menu:
                //intent = new Intent(this, MainScreenActivity.class);
                //startActivity(intent);
                return true;

            case R.id.action_search:
                Intent searchActivity = new Intent(this,SearchActivity.class);
                startActivity(searchActivity);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent mainActivity = new Intent(this,MainScreenActivity.class);
        startActivity(mainActivity);
        super.onBackPressed();
    }

    /*****************************************************************/

}
