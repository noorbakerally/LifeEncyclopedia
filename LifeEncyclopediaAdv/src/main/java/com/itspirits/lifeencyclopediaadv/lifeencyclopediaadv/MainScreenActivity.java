package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.SharedPreferences.Editor;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import java.text.ParseException;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities.AboutActivity;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities.BookmarkListActivity;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities.MainActivity;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities.SearchActivity;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.Constants;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.DataBaseHelper;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.LifeObject;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.Msg;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainScreenActivity extends ActionBarActivity {

    Button btnAbout;
    Button btnSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        //check for message
        String url = "http://apioflife-itspirits.rhcloud.com/msg";
        Constants.queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                String result  = response.toString();

                Gson gson = new Gson();
                Msg objMSG = gson.fromJson(result, Msg.class);

                SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date convertedCurrentDate = date.parse(objMSG.date);
                    Date today = new Date();

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String msgKey = prefs.getString(objMSG.date, "No Val");
                    if (convertedCurrentDate.toString().equals(date.format(today)) && !msgKey.equals("No Val")){
                        Editor editor = prefs.edit();
                        //editor.putString(objMSG.date,objMSG.date);
                        editor.commit();
                    }

                    if (objMSG.type.equals("msg")){
                        //showMessage(objMSG.val);
                    } else if (objMSG.type.equals("link")){
                        //showMessage(objMSG.val,objMSG.link);
                    }else if (objMSG.type.equals("sql")){
                        //exeSQL(objMSG.val);
                    }


                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        });
        Constants.queue.add(jsObjRequest);


		/*
		 // Look up the AdView as a resource and load a request.
		AdView adView = (AdView)this.findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	    */


        /*
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS){

            System.out.println("SUCCESS");
        }else{

        }


        String android_id = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);

        Log.d("Android","Android ID : "+android_id);

        System.out.println(android_id);
         */
        DataBaseHelper myDbHelper = new DataBaseHelper(this);


        try {

            myDbHelper.createDataBase();

        } catch (Exception ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDataBase();

        }catch(Exception sqle){

            try {
                throw sqle;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        SQLiteDatabase db = myDbHelper.getReadableDatabase();


        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                System.out.println(c.getString(0));
                c.moveToNext();
            }
        }


        String[] COLUMNS = {"entity","entity_type","label","direct_subclasses","description","common_names","scientific_names","kingdomname","phylumname","classname","ordername","familyname","genusname","image","thumbnail","sound"};

        Cursor cursor =
                db.query("life", // a. table
                        COLUMNS, // b. column names
                        " entity = ?", // c. selections
                        new String[] { String.valueOf("Animal") }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
        db.close();



        LifeObject n = new LifeObject(cursor.getString(0), cursor.getString(1));
        n.setLabel(cursor.getString(2));

        String directSubclasses [] = cursor.getString(3).split("<directSubclass>");
        for (String currentDirectSubclass:directSubclasses){
            n.addDirectSubclass(currentDirectSubclass);
        }

        n.setDescription(cursor.getString(4));

        String common_names [] = cursor.getString(5).split("<common_names>");
        for (String currentCommonName:common_names){
            n.addCommonNames(currentCommonName);
        }

        String scientific_names [] = cursor.getString(6).split("<scientific_names>");
        for (String currentScientificName:scientific_names){
            n.addScientificNames(currentScientificName);
        }

        n.setKingdomName(cursor.getString(7));
        n.setPhylumName(cursor.getString(8));
        n.setClassName(cursor.getString(9));
        n.setOrderName(cursor.getString(10));
        n.setFamilyName(cursor.getString(11));
        n.setGenusName(cursor.getString(12));

        n.setImage(cursor.getString(13));
        n.setThumbnail(cursor.getString(14));
        n.setSound(cursor.getString(15));








        Constants.queue = Volley.newRequestQueue(this);

        Button btnBrowse = (Button)findViewById(R.id.btnBrowse);
        btnBrowse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainActivity);
            }
        });

        Button btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent searchActivity = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(searchActivity);
            }
        });


        Button btnBookmark = (Button)findViewById(R.id.btnBookmark);
        btnBookmark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent bookmarkActivity = new Intent(getApplicationContext(),BookmarkListActivity.class);
                startActivity(bookmarkActivity);
            }
        });

        Button goRandom = (Button)findViewById(R.id.btnGoRandom);
        goRandom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                mainActivity.putExtra("goRandom", true);
                startActivity(mainActivity);
            }
        });

        Button btnAbout = (Button)findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent aboutActivity = new Intent(getApplicationContext(),AboutActivity.class);
                startActivity(aboutActivity);
            }
        });

    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //MENU GOES HERE
    /***************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_bookmark).setVisible(false);
        menu.findItem(R.id.action_show_menu).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Constants.SHARE_STRING);
                startActivity(Intent.createChooser(intent, "Share"));
                return true;
        }
        return false;
    }
    /*****************************************************************/

}
