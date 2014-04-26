package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.MainScreenActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.R;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.events.NewRequestEvent;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.events.UpdateHeadlinesListEvent;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.Constants;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.DataBaseHelper;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.LifeObject;
import android.content.SharedPreferences.Editor;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends FragmentActivity implements  HeadlinesFragment.OnHeadlineSelectedListener {
    private ProgressDialog progress;
    SharedPreferences prefs;
    public Menu menu;
    String parentActivity;
    boolean initial = true;
    DataBaseHelper myDbHelper;
    public int currentLifeObj = 0;

    boolean goRandom = false;

    // Called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //checking the go random
        System.out.println("HasRandom:"+getIntent().hasExtra("goRandom"));
        if (getIntent().hasExtra("goRandom")){
            parentActivity = "GO_RANDOM";
            goRandom = true;
        }


        //define the database
        myDbHelper = new DataBaseHelper(this);
        try {

            myDbHelper.createDataBase();

        } catch (Exception ioe) {

            throw new Error("Unable to create database");

        }

        myDbHelper.openDataBase();



        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        EventBus.getDefault().register(this);

        setContentView(R.layout.news_articles);
        Constants.queue = Volley.newRequestQueue(this);

        Intent intent = getIntent();

        if (intent.hasExtra(SearchActivity.SELECTED_ENTITY)){
            LifeObject seachedObject =  new LifeObject(intent.getExtras().getString(SearchActivity.SELECTED_ENTITY),intent.getExtras().getString(SearchActivity.SELECTED_ENTITY_TYPE));
            seachedObject.setLabel(intent.getExtras().getString(SearchActivity.SELECTED_LABEL));
            parentActivity = intent.getExtras().getString(SearchActivity.PARENT);
            try {
                add(seachedObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            //ensure that there is no lifeobjects currently loaded
            Constants.lifeObjects.clear();
            try {
                initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.

            if (savedInstanceState != null) {
                return;
            }



            HeadlinesFragment firstFragment = new HeadlinesFragment();
            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments

            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            // beginTransaction() is used to begin any edits of Fragments

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();

        }
    }



    //MENU GOES HERE
    /***************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
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
                intent = new Intent(this, MainScreenActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_search:
                Intent searchActivity = new Intent(this,SearchActivity.class);
                startActivity(searchActivity);
                return true;

            case R.id.action_bookmark:
                if (item.getIcon().getConstantState().equals(
                        getResources().getDrawable(R.drawable.starto).getConstantState())){

                    item.setIcon(R.drawable.startw);
                    //ubbookmarking the item
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    Editor editor = prefs.edit();
                    editor.remove("LO:"+Constants.currentLifeObject.getEntity());
                    editor.commit();

                } else {



                    item.setIcon(R.drawable.starto);
                    //bookmarking the item

                    Editor editor = prefs.edit();
                    Gson gson = new Gson();
                    editor.putString("LO:"+Constants.currentLifeObject.getEntity(), gson.toJson(Constants.currentLifeObject));
                    //editor.putString("key", "value");
                    editor.commit();
                }


                return true;
        }
        return false;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Constants.currentLifeObject !=null){
            String lifeObjStr = PreferenceManager.getDefaultSharedPreferences(this).getString("LO:"+Constants.currentLifeObject.getEntity(), "No Val");

            if (findViewById(R.id.fragment_container) == null) {
                if (lifeObjStr.equals("No Val")){
                    menu.findItem(R.id.action_bookmark).setIcon(R.drawable.startw);
                } else {
                    menu.findItem(R.id.action_bookmark).setIcon(R.drawable.starto);
                }
            }
        }
        return true;
    }

    /*****************************************************************/





    // Required if the OnHeadlineSelectedListener interface is implemented
    // This method is called when a headline is clicked on

    public void showHeadlineFragment() {
        // Create an instance of the Fragment that holds the titles

        if (findViewById(R.id.fragment_container) == null)
            return;

        if (initial){
            initial = false;
            return;
        }
        HeadlinesFragment headlinesFragment = new HeadlinesFragment();
        // In case this activity was started with special instructions from an Intent,
        // pass the Intent's extras to the fragment as arguments

        headlinesFragment.setArguments(getIntent().getExtras());
        // Add the fragment to the 'fragment_container' FrameLayout
        // beginTransaction() is used to begin any edits of Fragments

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, headlinesFragment);

        // addToBackStack() causes the transaction to be remembered.
        // It will reverse this operation when it is later popped off
        // the stack.

        transaction.addToBackStack(null);

        // Schedules for the addition of the Fragment to occur

        transaction.commit();
    }

    public void onArticleSelected(int position) {
        int i = position +1;
        int size = Constants.lifeObjects.size()-1;
        while (size >= i){
            Constants.lifeObjects.remove(size);
            size--;
        }

        Constants.currentLifeObject = Constants.lifeObjects.get(position);
        supportInvalidateOptionsMenu();


        ArticleFragment articleFrag = (ArticleFragment)
                getSupportFragmentManager().findFragmentById(R.id.article_fragment);




        // If the article fragment is here we're in the two pane layout

        if (articleFrag != null) {

            // Get the ArticleFragment to update itself

            articleFrag.updateArticleView(position);

        } else {

            // If the fragment is not available, use the one pane layout and
            // swap between the article and headline fragments

            // Create fragment and give it an argument for the selected article

            ArticleFragment newFragment = new ArticleFragment();

            // The Bundle contains information passed between activities

            Bundle args = new Bundle();


            // Save the current article value

            args.putInt(ArticleFragment.ARG_POSITION, position);

            // Add the article value to the new Fragment

            newFragment.setArguments(args);

            // The FragmentTransaction adds, removes, replaces and
            // defines animations for Fragments
            // The FragmentManager provides methods for interacting
            // beginTransaction() is used to begin any edits of Fragments

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back

            transaction.replace(R.id.fragment_container, newFragment);

            // addToBackStack() causes the transaction to be remembered.
            // It will reverse this operation when it is later popped off
            // the stack.

            transaction.addToBackStack(null);

            // Schedules for the addition of the Fragment to occur

            transaction.commit();
        }

    }

    public void onEvent(UpdateHeadlinesListEvent event){
        showHeadlineFragment();
    }

    public void onEvent(NewRequestEvent event){
        Constants.queue.add(event.getRequest());
    }
    void initialize() throws Exception {

        LifeObject firstObject = new LifeObject("Life","Life");
        //LifeObject firstObject = new LifeObject("Animal","kingdom");
        //LifeObject firstObject = new LifeObject("Bird","class");
        firstObject.setLabel("Life");

        add(firstObject);
    }

    public void add(final LifeObject lifeObject) throws Exception {

        System.out.println("Requesting:"+lifeObject.getLabel());

        if (lifeObject.getLabel()==null){
            return;
        }
        progress = ProgressDialog.show(this, "Connecting to Server",
                "Fetching "+lifeObject.getLabel()+" .....", true);

        final List<LifeObject> tempObjs = new ArrayList<LifeObject>();


        //creating event to show main progress car, using MainProgressBarEvent
        //EventBus.getDefault().post(new MainProgressBarEvent("Connecting to Server","Fetching "+lifeObject.getEntity()+"......"));


        //to prevent two similar object from being loaded
    	/*
 	   for (LifeObject currentLifeObject:tempObjs){
    		if (currentLifeObject.getEntity().equals(lifeObject.getEntity())){
    			progress.dismiss();
    			return;
    		}
    	}
    	*/

    	/*
    	for (LifeObject currentLifeObject:Constants.lifeObjects){
    		if (currentLifeObject.getEntity().equals(lifeObject.getEntity())){
    			progress.dismiss();
    			return;
    		}
    	}
    	*/

        tempObjs.add(lifeObject);

        //creating the url
        //an exception for the life object
        String url;
        if (lifeObject.getEntity_type()!=null && lifeObject.getEntity_type().toLowerCase().equals("life")){
            lifeObject.setEntity("Life");
        }



        LifeObject animal = getDatabaseObject(lifeObject.getEntity());

        int FinalRm = Constants.lifeObjects.size();
        System.out.println(Constants.currentLifeObjectPosition+" "+ FinalRm);
        for (int i=Constants.currentLifeObjectPosition+1;i<FinalRm;i++){
            Constants.lifeObjects.remove(Constants.lifeObjects.size()-1);
        }

        Constants.lifeObjects.add(animal);


        //setting the current life object
        Constants.currentLifeObject = animal;


        EventBus.getDefault().post(new UpdateHeadlinesListEvent());

        tempObjs.remove(lifeObject);

        progress.dismiss();


        //use the support version of InvalidateOptionsMenu
        supportInvalidateOptionsMenu();
        //comming from bookmark




    }
    void showAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setPositiveButton("OK",new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mainActivity = new Intent(getApplicationContext(),MainScreenActivity.class);
                        startActivity(mainActivity);

                    }
                })
                .setMessage("There is an error while accessing the server. Check your internet connection or report this error. Thank You :)")
                .show();
    }

    LifeObject getDatabaseObject(String entity) throws Exception {


        DataBaseHelper myDbHelper = new DataBaseHelper(this);


        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDataBase();

        }catch(Exception sqle){

            throw sqle;

        }
        SQLiteDatabase db = myDbHelper.getReadableDatabase();

        Cursor cursor = null;
        if (!goRandom){



            String[] COLUMNS = {"entity","entity_type","label","direct_subclasses","description","common_names","scientific_names","kingdomname","phylumname","classname","ordername","familyname","genusname","image","thumbnail","sound"};


            // 2. build query
            cursor =
                    db.query("life", // a. table
                            COLUMNS, // b. column names
                            " entity = ?", // c. selections
                            new String[] { String.valueOf(entity) }, // d. selections args
                            null, // e. group by
                            null, // f. having
                            null, // g. order by
                            null); // h. limit
            // 3. if we got results get the first one
            if (cursor != null)
                cursor.moveToFirst();
        }
        else{
            goRandom = false;


            String ranLabel = "";

            while (ranLabel.length()==0){
                cursor  = db.rawQuery("SELECT * FROM life ORDER BY RANDOM() LIMIT 1;", null);
                //cursor  = db.rawQuery("SELECT * FROM life where entity='Ceratopsidae';", null);

                // 3. if we got results get the first one
                if (cursor != null){
                    cursor.moveToFirst();
                    ranLabel = cursor.getString(2);
                }
            }
        }




        db.close();
        myDbHelper.close();


        LifeObject n = new LifeObject(cursor.getString(0), cursor.getString(1));
        n.setLabel(cursor.getString(2));
        if (cursor.getString(3) != null && cursor.getString(3).length() > 0){
            String directSubclasses [] = cursor.getString(3).split("<directSubclass>");
            List <String> dSub = new ArrayList <String> ();
            for (String currentDirectSubclass:directSubclasses){
                if (dSub.contains(currentDirectSubclass.split("<label>")[1])){
                    continue;
                } else {
                    dSub.add(currentDirectSubclass.split("<label>")[1]);
                }

                LifeObject currentDirectClass = new LifeObject();
                currentDirectClass.setEntity(currentDirectSubclass.split("<label>")[1]);
                currentDirectClass.setLabel(currentDirectSubclass.split("<label>")[0]);
                n.addDirectSubclass(currentDirectClass);
            }
        }
        n.setDescription(cursor.getString(4));

        if (cursor.getString(5) != null && cursor.getString(5).length() > 0){
            String common_names [] = cursor.getString(5).split("<common_names>");
            for (String currentCommonName:common_names){
                n.addCommonNames(currentCommonName);
            }
        }

        if (cursor.getString(6) != null && cursor.getString(6).length() > 0){
            String scientific_names [] = cursor.getString(6).split("<scientific_names>");
            for (String currentScientificName:scientific_names){
                n.addScientificNames(currentScientificName);
            }
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



        String baseURL = "http://ichef.bbci.co.uk/naturelibrary/images/ic/";
        String variable = "/entityName/entityName_1.jpg";
        variable = variable.replace("entityName", n.getEntity().toLowerCase());
        variable = "/"+n.getEntity().substring(0, 1).toLowerCase()+"/"+n.getEntity().substring(0, 2).toLowerCase() +variable;

        n.setImage(baseURL+"credit/640x395"+variable);
        n.setThumbnail(baseURL+"149x84"+variable);
        return n;
    }
}
