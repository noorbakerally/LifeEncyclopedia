package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities;

import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.MainScreenActivity;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.Constants;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.LifeObject;
import android.media.MediaPlayer.OnPreparedListener;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.R;
import java.io.IOException;
import java.util.List;

/**
 * Created by noor on 26/04/14.
 */
public class ArticleFragment extends Fragment implements OnPreparedListener {
    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private Handler handler = new Handler();
    ArticleFragment currentFragment;
    ProgressDialog progress;
    SoundController soundController;
    Rect b;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = new Rect();


        // TODO Auto-generated method stub
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }
        currentFragment = this;
        soundController = new SoundController();

        if (getActivity().findViewById(R.id.fragment_container) == null) {
            setHasOptionsMenu(false);
        }
        else
        {
            setHasOptionsMenu(true);
        }

        View articleFragmentView = inflater.inflate(R.layout.article_view, container, false);

		 /*
		 AdView adView = (AdView)articleFragmentView.findViewById(R.id.adView1);
		 AdRequest adRequest = new AdRequest.Builder().build();
		 adView.loadAd(adRequest);
		   */
        return articleFragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }

    @Override
    public void onStart() 	{
        super.onStart();

        // Check if an article had been selected
        Bundle args = getArguments();
        if (args != null) {

            // Set article based on argument passed in
            updateArticleView(args.getInt(ARG_POSITION));

        } else if (mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView

            updateArticleView(mCurrentPosition);
        }
    }



    public void updateArticleView(int position) {
        if (Constants.lifeObjects!= null && Constants.lifeObjects.size() < position-1){
            return;
        }

        if (position >= Constants.lifeObjects.size()){
            System.out.println(position+" "+Constants.lifeObjects.size());
            Intent mainActivity = new Intent(getActivity(),MainScreenActivity.class);
            startActivity(mainActivity);
            return;
        }


        Constants.currentLifeObject = Constants.lifeObjects.get(position);

        getActivity().supportInvalidateOptionsMenu();


        //setting the title
        if (Constants.currentLifeObject.getLabel() != null) {
            TextView tvTitle = (TextView)getActivity().findViewById(R.id.title);
            tvTitle.setText(Constants.capitalize(Constants.currentLifeObject.getLabel()));
        }
        //set title to "" if life
        if (Constants.currentLifeObject.getLabel().equals("Life")) {
            TextView tvTitle = (TextView)getActivity().findViewById(R.id.title);
            tvTitle.setText("");
        }

        //setting the image of the lifeobject
        final ImageView imgLifeOject = (ImageView)getActivity().findViewById(R.id.imageLifeObject);
        if (Constants.currentLifeObject.getEntity().equals("Life")){
            imgLifeOject.setImageResource(R.drawable.life);
        }
        else{
            ImageRequest ir = new ImageRequest(Constants.currentLifeObject.getImage(), new Response.Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap response) {
                    imgLifeOject.setImageBitmap(response);
                    imgLifeOject.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(Constants.currentLifeObject.getImage()), "image/*");
                            PackageManager manager = getActivity().getPackageManager();
                            List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
                            if (infos.size() > 0) {
                                startActivity(intent);
                            }else{
                                //No Application can handle your intent
                            }



                        }
                    });

                }
            }, 0, 0, null, null);
            Constants.queue.add(ir);
        }

        //setting the sound
        LinearLayout soundContainer = (LinearLayout)getActivity().findViewById(R.id.soundContainer);
        if (Constants.currentLifeObject.getSound() == null || Constants.currentLifeObject.getSound().length() ==0){
            soundContainer.setVisibility(View.GONE);
        }

        else{
            //load the sound

            soundContainer.setVisibility(View.VISIBLE);
            ImageButton soundLoader = (ImageButton)getActivity().findViewById(R.id.lifeObjectSound);


            soundLoader.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo ni = cm.getActiveNetworkInfo();
                    if (ni == null) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("IT Spirits")
                                .setMessage("There is currently no internet connection. While you will be able to go through the encyclopedia, you will not be able to view images or hear sound. Consider connecting your device to the Internet. Thank You :)")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;

                    }

                    progress = ProgressDialog.show(currentFragment.getActivity(), "Please wait",
                            "Loading Sound...", true);


                    progress.setCancelable(true);
                    progress.setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface arg0) {
                            progress.dismiss();
                            mediaPlayer.reset();
                            mediaController.hide();
                        }
                    });

                    if (mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaController.hide();
                    }

                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnPreparedListener(currentFragment);

                    mediaController = new MediaController(getActivity()){
                        @Override
                        public void hide()
                        {

                            if(mediaPlayer.isPlaying()){
                                mediaPlayer.stop();
                            }
                            super.hide();
                        }
                    };


                    try {
                        mediaPlayer.setDataSource(Constants.currentLifeObject.getSound());
                        mediaPlayer.prepareAsync();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();


                }
            });


        }


        //setting the taxnomic rank
        LinearLayout taxonomicRank = (LinearLayout) getActivity().findViewById(R.id.taxonomicRank);

        if (Constants.currentLifeObject.getEntity_type() != null && Constants.currentLifeObject.getEntity_type().length() > 0){
            taxonomicRank.setVisibility(View.VISIBLE);

            TextView taxonomicRankValue = (TextView)getActivity().findViewById(R.id.taxonomicRankValue);
            taxonomicRankValue.setText(" "+Constants.capitalize(Constants.currentLifeObject.getEntity_type()));
        }
        else{

            taxonomicRank.setVisibility(View.GONE);
        }


        //setting the other taxonomic details
        LinearLayout taxonomicDetailsContainer = (LinearLayout) getActivity().findViewById(R.id.taxonomicNameContainer);
        if (Constants.currentLifeObject.getKingdomName() != null && Constants.currentLifeObject.getKingdomName().length() > 0){
            //setting the container on
            taxonomicDetailsContainer.setVisibility(View.VISIBLE);


            LinearLayout taxonomicNamesContainer = (LinearLayout)getActivity().findViewById(R.id.taxonomicNamesContainer);
            taxonomicNamesContainer.removeAllViewsInLayout();


            //adding the kingdom
            TextView kingdomName = new TextView(getActivity());
            kingdomName.setText("Kingdom:"+Constants.capitalize(Constants.currentLifeObject.getKingdomName()));
            kingdomName.setTextSize(16);
            taxonomicNamesContainer.addView(kingdomName);


            //adding the phylum
            if (Constants.currentLifeObject.getPhylumName() != null && Constants.currentLifeObject.getPhylumName().length() > 0){
                TextView phylumName = new TextView(getActivity());
                phylumName.setText("Phylum:"+Constants.capitalize(Constants.currentLifeObject.getPhylumName()));
                phylumName.setTextSize(16);
                taxonomicNamesContainer.addView(phylumName);
            }

            //adding the class
            if (Constants.currentLifeObject.getClassName() != null && Constants.currentLifeObject.getClassName().length() > 0){
                TextView className = new TextView(getActivity());
                className.setText("Class:"+Constants.capitalize(Constants.currentLifeObject.getClassName()));
                className.setTextSize(16);
                taxonomicNamesContainer.addView(className);
            }

            //adding the order
            if (Constants.currentLifeObject.getOrderName() != null && Constants.currentLifeObject.getOrderName().length() > 0){
                TextView orderName = new TextView(getActivity());
                orderName.setText("Order:"+Constants.capitalize(Constants.currentLifeObject.getOrderName()));
                orderName.setTextSize(16);
                taxonomicNamesContainer.addView(orderName);
            }

            //adding the family
            if (Constants.currentLifeObject.getFamilyName() != null && Constants.currentLifeObject.getFamilyName().length() > 0){
                TextView familyName = new TextView(getActivity());
                familyName.setText("Family:"+Constants.capitalize(Constants.currentLifeObject.getFamilyName()));
                familyName.setTextSize(16);
                taxonomicNamesContainer.addView(familyName);
            }

            //adding the genus
            if (Constants.currentLifeObject.getGenusName() != null && Constants.currentLifeObject.getGenusName().length() > 0){
                TextView genusName = new TextView(getActivity());
                genusName.setText("Genus:"+Constants.capitalize(Constants.currentLifeObject.getGenusName()));
                genusName.setTextSize(16);
                taxonomicNamesContainer.addView(genusName);
            }

        }
        else{

            taxonomicDetailsContainer.setVisibility(View.GONE);
        }



        //setting the description
        LinearLayout descriptionContainer = (LinearLayout) getActivity().findViewById(R.id.descriptionContainer);
        if (Constants.currentLifeObject.getDescription() != null && Constants.currentLifeObject.getDescription().length() > 0){

            //setting the description container on
            descriptionContainer.setVisibility(View.VISIBLE);

            TextView descriptionLabel = (TextView)getActivity().findViewById(R.id.descriptionLabel);
            descriptionLabel.setTypeface(null, Typeface.BOLD);
            descriptionLabel.setText(Constants.currentLifeObject.getLabel()+" Description");

            TextView descriptionLabelValue = (TextView)getActivity().findViewById(R.id.descriptionLabelValue);
            descriptionLabel.setTypeface(null, Typeface.BOLD);
            descriptionLabelValue.setText(Constants.currentLifeObject.getDescription());
        }
        else{

            descriptionContainer.setVisibility(View.GONE);
        }




        //setting the scientific names
        LinearLayout scientificContainer = (LinearLayout) getActivity().findViewById(R.id.scientificContainer);
        if (Constants.currentLifeObject.getScientific_names() != null && Constants.currentLifeObject.getScientific_names().size() > 0){

            //setting the container on
            scientificContainer.setVisibility(View.VISIBLE);


            TextView scientificNameLabel = (TextView)getActivity().findViewById(R.id.scientificNamesLabel);
            scientificNameLabel.setTypeface(null, Typeface.BOLD);
            scientificNameLabel.setText("Scientific names of "+Constants.currentLifeObject.getLabel()+" are:");
            LinearLayout scientificNamesContainer = (LinearLayout)getActivity().findViewById(R.id.scientificNamesContainer);
            scientificNamesContainer.removeAllViewsInLayout();

            for (String currentScientificName:Constants.currentLifeObject.getScientific_names()){
                TextView tvScientific = new TextView(getActivity());
                currentScientificName = currentScientificName.substring(0, 1).toUpperCase() + currentScientificName.substring(1);
                tvScientific.setText(currentScientificName);
                tvScientific.setTextSize(16);
                scientificNamesContainer.addView(tvScientific);

            }
        }
        else{

            scientificContainer.setVisibility(View.GONE);
        }





        //setting the common names
        LinearLayout commonContainer = (LinearLayout) getActivity().findViewById(R.id.commonContainer);
        if (Constants.currentLifeObject.getCommon_names() != null && Constants.currentLifeObject.getCommon_names().size() > 0){

            //setting the container on
            commonContainer.setVisibility(View.VISIBLE);


            TextView commonNameLabel = (TextView)getActivity().findViewById(R.id.commonNamesLabel);
            commonNameLabel.setTypeface(null, Typeface.BOLD);
            commonNameLabel.setText("Common names of "+Constants.currentLifeObject.getLabel()+" are:");
            LinearLayout commonNamesContainer = (LinearLayout)getActivity().findViewById(R.id.commonNamesContainer);
            commonNamesContainer.removeAllViewsInLayout();


            for (String currentCommonName:Constants.currentLifeObject.getCommon_names()){
                TextView tvCommonName = new TextView(getActivity());
                currentCommonName = currentCommonName.substring(0, 1).toUpperCase() + currentCommonName.substring(1);
                tvCommonName.setText(currentCommonName);
                tvCommonName.setTextSize(16);
                commonNamesContainer.addView(tvCommonName);

            }

        }
        else{

            commonContainer.setVisibility(View.GONE);
        }



        //setting the direct subclass
        LinearLayout subclassContainer = (LinearLayout) getActivity().findViewById(R.id.subclassContainer);
        if (Constants.currentLifeObject.getDirect_subclasses() != null && Constants.currentLifeObject.getDirect_subclasses().size() > 0){

            //showing the direct subclass whole container
            subclassContainer.setVisibility(View.VISIBLE);

            TextView directSubclassLabel = (TextView)getActivity().findViewById(R.id.directSuclassLabel);
            directSubclassLabel.setTypeface(null, Typeface.BOLD);

            if (Constants.currentLifeObject.getLabel().toLowerCase().equals("life")){
                directSubclassLabel.setText("Life consist of "+Constants.currentLifeObject.getDirect_subclasses().size()+" Kingdoms(Choose anyone of them to continue exploring):");
            }
            else{
                directSubclassLabel.setText("The "+Constants.currentLifeObject.getLabel()+" "+Constants.currentLifeObject.getEntity_type()+" consist of the following(Press anyone to continue exploring):");
            }

            LinearLayout directSubclassesContainer = (LinearLayout)getActivity().findViewById(R.id.directSubclassesContainer);
            directSubclassesContainer.removeAllViewsInLayout();
            for (LifeObject directSubclass:Constants.currentLifeObject.getDirect_subclasses()){


                final View rowView = getActivity().getLayoutInflater().inflate(R.layout.text_view_directclass, null);


                TextView tvDirectSubclass = (TextView) rowView.findViewById(R.id.txt_text_view_directclass);
                tvDirectSubclass.setText(directSubclass.getLabel());
                tvDirectSubclass.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (LifeObject currentSubclass:Constants.currentLifeObject.getDirect_subclasses()){
                            if (currentSubclass.getLabel().toLowerCase().equals(((TextView)v).getText().toString().toLowerCase())){
                                //changing the currently selected lifeobject based on the new subclass selection
                                Constants.currentLifeObject = currentSubclass;
                                try {
                                    addNew(currentSubclass);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                });

                directSubclassesContainer.addView(rowView);
            }
        }
        else{

            subclassContainer.setVisibility(View.GONE);
        }
        final ScrollView scrollView = (ScrollView)getActivity().findViewById(R.id.scrollView);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 100);

        //updating the position
        mCurrentPosition = position;
        Constants.currentLifeObjectPosition = position;
    }



    void addNew(LifeObject newLifeObject) throws Exception {
        MainActivity activity = (MainActivity) getActivity();
        activity.add(newLifeObject);
    }





    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("media status", "onPrepared");



        handler.post(new Runnable() {
            public void run() {

                launchMediaController();
                progress.dismiss();
            }
        });

    }
    void launchMediaController(){
        mediaController.setEnabled(true);

        mediaPlayer.start();
        mediaController.setMediaPlayer(soundController);
        mediaController.setAnchorView(getView());
        mediaController.show(0);
    }

    private class SoundController implements MediaController.MediaPlayerControl{

        public void start() {
            mediaPlayer.start();
        }

        public void pause() {
            mediaPlayer.pause();
        }

        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        public void seekTo(int i) {
            mediaPlayer.seekTo(i);
        }

        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        public int getBufferPercentage() {
            return 0;
        }

        public boolean canPause() {
            return true;
        }

        public boolean canSeekBackward() {
            return true;
        }

        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            // TODO Auto-generated method stub
            return 0;
        }
    }

    //MENU GOES HERE
    /***************************************************************/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String lifeObjStr = prefs.getString("LO:"+ Constants.currentLifeObject.getEntity(), "No Val");


        if (lifeObjStr.equals("No Val")){
            menu.findItem(R.id.action_bookmark).setIcon(R.drawable.startw);
        } else {
            menu.findItem(R.id.action_bookmark).setIcon(R.drawable.starto);
        }

    }
    /*****************************************************************/


}
