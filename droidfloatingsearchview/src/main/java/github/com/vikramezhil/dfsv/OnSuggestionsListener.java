package github.com.vikramezhil.dfsv;

/**
 * Suggestions Listener
 *
 * @author Vikram Ezhil
 */

public interface OnSuggestionsListener {
    /**
     * Sends an update with the clicked suggestion
     *
     * @param suggestion The clicked suggestion
     */
    void onSuggestionClicked(String suggestion);

    /**
     * Sends an update if suggestions are found
     *
     * @param suggestionsFound True - suggestions found, False - if otherwise
     */
    void onSuggestionsFound(boolean suggestionsFound);
}
