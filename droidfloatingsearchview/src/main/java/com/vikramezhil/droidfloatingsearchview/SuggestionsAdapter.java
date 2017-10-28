package com.vikramezhil.droidfloatingsearchview;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

/**
 * Suggestions Adapter
 *
 * @author Vikram Ezhil
 */

class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.HolderView>
{
    private ArrayList<String> suggestionsList = new ArrayList<>();
    private ArrayList<String> filteredSuggestionsList = new ArrayList<>();
    private OnSuggestionsListener onSuggestionsListener;

    private int suggestionTextColor = Color.BLACK;
    private int suggestionDividerColor = Color.parseColor("#E0E0E0");

    class HolderView extends RecyclerView.ViewHolder
    {
        LinearLayout llSuggestionItem;
        TextView tvSuggestionItem;
        View suggestionDividerItem;

        HolderView(View itemView)
        {
            super(itemView);

            llSuggestionItem = itemView.findViewById(R.id.llSuggestionItem);
            tvSuggestionItem = itemView.findViewById(R.id.tvSuggestionItem);
            suggestionDividerItem = itemView.findViewById(R.id.suggestionDividerItem);
        }
    }

    /**
     * SuggestionsAdapter Constructor
     *
     * @param onSuggestionsListener The class instance which implements the interface
     */
    SuggestionsAdapter(OnSuggestionsListener onSuggestionsListener)
    {
        this.onSuggestionsListener = onSuggestionsListener;
    }

    /**
     * Sets the suggestions data list
     *
     * @param suggestionsList The suggestions data list
     */
    void setSuggestions(ArrayList<String> suggestionsList)
    {
        this.suggestionsList = suggestionsList;

        filteredSuggestionsList.clear();

        notifyDataSetChanged();
    }

    /**
     * Filters suggestion data list based on text
     *
     * @param filterText The filter text
     */
    void filterSuggestions(String filterText)
    {
        if(suggestionsList.size() > 0)
        {
            filteredSuggestionsList.clear();
            for(String suggestion: suggestionsList)
            {
                if(suggestion.toLowerCase().contains(filterText.toLowerCase()))
                {
                    filteredSuggestionsList.add(WordUtils.capitalize(suggestion.toLowerCase()));
                }
            }

            // Sending an update with the suggestions found status
            onSuggestionsListener.onSuggestionsFound(filteredSuggestionsList.size() > 0);

            notifyDataSetChanged();
        }
    }

    /**
     * Clears out the filter suggestions list
     */
    void clearFilterSuggestions()
    {
        filteredSuggestionsList.clear();

        notifyDataSetChanged();
    }

    /**
     * Sets the suggestion text color
     *
     * @param suggestionTextColor The suggestion text color
     */
    void setSuggestionTextColor(int suggestionTextColor)
    {
        this.suggestionTextColor = suggestionTextColor;

        notifyDataSetChanged();
    }

    /**
     * Sets the suggestion divider color
     *
     * @param suggestionDividerColor The suggestion divider color
     */
    void setSuggestionDividerColor(int suggestionDividerColor)
    {
        this.suggestionDividerColor = suggestionDividerColor;

        notifyDataSetChanged();
    }

    @Override
    public HolderView onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rvsuggestionslist, parent, false);

        return new SuggestionsAdapter.HolderView(view);
    }

    @Override
    public void onBindViewHolder(HolderView holder, int position)
    {
        holder.llSuggestionItem.setTag(position);
        holder.tvSuggestionItem.setText(filteredSuggestionsList.get(position));
        holder.tvSuggestionItem.setTextColor(suggestionTextColor);
        holder.llSuggestionItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                // Sending an update with the clicked suggestion
                onSuggestionsListener.onSuggestionClicked(filteredSuggestionsList.get(((int) v.getTag())));
            }
        });

        holder.suggestionDividerItem.setBackgroundColor(suggestionDividerColor);
        if((int) holder.llSuggestionItem.getTag() < filteredSuggestionsList.size() - 1)
        {
            holder.suggestionDividerItem.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.suggestionDividerItem.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount()
    {
        return filteredSuggestionsList.size();
    }
}
