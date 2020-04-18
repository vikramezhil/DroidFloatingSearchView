package github.com.vikramezhil.dfsvexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import github.com.vikramezhil.dfsv.Dfsv;
import github.com.vikramezhil.dfsv.OnDfsvListener;

/**
 * Droid Floating Search View Activity
 *
 * @author Vikram Ezhil
 */

public class ActivityDfsv extends Activity {
    private final String TAG = "ActivityDFSV";

    private Dfsv dfsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dfsv);

        dfsView = findViewById(R.id.dfsView);
        dfsView.setOnDfsvListener(new OnDfsvListener() {
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
    protected void onPause() {
        super.onPause();

        dfsView.closeSearchView();
    }
}
