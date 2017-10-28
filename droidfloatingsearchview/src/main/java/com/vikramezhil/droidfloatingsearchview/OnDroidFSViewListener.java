package com.vikramezhil.droidfloatingsearchview;

/**
 * Droid Floating Search View Listener
 *
 * @author Vikram Ezhil
 */

public interface OnDroidFSViewListener
{
    /**
     * Sends an update when the search view has focus
     */
    void onHasFocus();

    /**
     * Sends an update when the search view has lost focus
     */
    void onLostFocus();

    /**
     * Sends an update whenever the search text has changed
     *
     * @param oldQuery The old query
     *
     * @param newQuery The new query
     */
    void onSearchTextChanged(String oldQuery, String newQuery);

    /**
     * Sends an update with the query when the search action is triggered
     *
     * @param searchQuery The query to be searched
     */
    void onSearchText(String searchQuery);

    /**
     * Sends an update that the action item was clicked
     */
    void onActionItemClicked();
}
