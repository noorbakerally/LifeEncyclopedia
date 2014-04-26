package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.activities;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;

import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.R;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.adapters.ArticleArrayAdapter;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.events.UpdateHeadlinesListEvent;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.Constants;

import de.greenrobot.event.EventBus;

/**
 * Created by noor on 26/04/14.
 */
public class HeadlinesFragment extends ListFragment {
    ArticleArrayAdapter headlinesAdapter;

    // Will monitor if a headline is clicked on

    OnHeadlineSelectedListener mCallback;

    // The container Activity must implement this interface so the
    // fragment can deliver messages


    boolean initial = true;
    public interface OnHeadlineSelectedListener {

        // This function is called when a list item is selected

        public void onArticleSelected(int position);

    }

    // Initializes the Fragment

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layout = R.layout.article_row;

        // A ListAdapter populates the ListView with data in ipsum arrays
        // An ArrayAdapter specifically deals with arrays
        // getActivity() gets an Intent to start a new activity
        // layout is the list items layout


        //registering to the event bus
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);

        headlinesAdapter = new ArticleArrayAdapter(getActivity(), layout, Constants.lifeObjects );
        setListAdapter(headlinesAdapter);





    }
    // Called when the Fragment is visible on the screen
    @Override
    public void onStart() {

        super.onStart();

        // If we have both the article names and the article Fragments on the screen
        // at the same time we highlight the selected article

        // The getFragmentManager() returns the FragmentManager which allows us
        // to interact with Fragments associated with the current activity

        // getListView() gets a ListView
        //  CHOICE_MODE_SINGLE allows up to one item to be in a chosen state in the list

        if (getFragmentManager().findFragmentById(R.id.article_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        String parentActivity = ((MainActivity)getActivity()).parentActivity;
        if (parentActivity != null && (parentActivity.equals("BOOKMARK") || parentActivity.equals("GO_RANDOM"))){
            System.out.println("Main Activity:Bookmarks");
            ((MainActivity)getActivity()).parentActivity = null;
            mCallback.onArticleSelected(0);
            // Set the item as checked to be highlighted when in two-pane layout
            //getListView().setItemChecked(0, true);
        }
        else if(Constants.currentLifeObject.getLabel().equals("Life") && Constants.lifeObjects.size()==1 && initial){
            mCallback.onArticleSelected(0);
            initial  = false;
        }
        getListView().setSelection(getListAdapter().getCount() - 1);
    }

    // Called when a Fragment is attached to an Activity

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.

        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        mCallback.onArticleSelected(position);

        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
    }

    public void onEvent(UpdateHeadlinesListEvent event){
        headlinesAdapter.notifyDataSetChanged();
        if (getActivity().findViewById(R.id.fragment_container) == null) {
            ((MainActivity)getActivity()).onArticleSelected(Constants.lifeObjects.size()-1);
            getListView().setSelection(getListAdapter().getCount() - 1);
        }

    }
    //MENU GOES HERE
    /***************************************************************/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        //only the headlines is being shown
        if (getActivity().findViewById(R.id.fragment_container) != null){
            menu.findItem(R.id.action_bookmark).setVisible(false);
        }
    }
    /*****************************************************************/


}
