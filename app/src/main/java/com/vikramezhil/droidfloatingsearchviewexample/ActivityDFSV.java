package com.vikramezhil.droidfloatingsearchviewexample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.vikramezhil.droidfloatingsearchview.DroidFSView;
import com.vikramezhil.droidfloatingsearchview.OnDroidFSViewListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Droid Floating Search View Activity
 *
 * @author Vikram Ezhil
 */

public class ActivityDFSV extends AppCompatActivity
{
    private final String TAG = "ActivityDFSV";

    private DroidFSView dfsView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dfsv);

        dfsView = findViewById(R.id.dfsView);
        dfsView.setSearchViewBgColor(Color.parseColor("#263238"));
        dfsView.setSearchViewTextColor(Color.WHITE);
        dfsView.setSearchViewCloseIconColor(Color.WHITE);
        dfsView.setSearchViewClearIconColor(Color.WHITE);
        dfsView.setSearchViewActionIconColor(Color.WHITE);
        dfsView.setSearchViewHintTextColor(Color.WHITE);
        dfsView.setDividerColor(Color.WHITE);
        dfsView.setContinuousSearch(false);
        dfsView.setCloseSearchViewOnOverlayTouch(false);
        dfsView.setSuggestions(new ArrayList<>(Arrays.asList("The Dark Knight", "The Avengers", "Deadpool", "Superman", "Logan", "Batman Vs. Superman", "Batman Begins")));
        dfsView.setOnDroidFSViewListener(new OnDroidFSViewListener()
        {
            @Override
            public void onHasFocus() {

                Log.i(TAG, "Has focus");
            }

            @Override
            public void onLostFocus() {

                Log.i(TAG, "Lost focus");
            }

            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {

                Log.i(TAG, "Old query = " + oldQuery + ", New Query = " + newQuery);
            }

            @Override
            public void onSearchText(String searchQuery) {

                Log.i(TAG, "Query to search = " + searchQuery);
            }

            @Override
            public void onActionItemClicked() {

                Log.i(TAG, "Action item clicked");
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        dfsView.closeSearchView();
    }
}
