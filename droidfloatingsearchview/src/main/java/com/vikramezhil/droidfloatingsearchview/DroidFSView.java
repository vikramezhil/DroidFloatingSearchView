package com.vikramezhil.droidfloatingsearchview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

/**
 * Droid Floating Search View
 *
 * @author Vikram Ezhil
 */

public class DroidFSView extends LinearLayout
{
    private Context context;
    private CardView cvDroidFSView;
    private DroidEditText etSearch;
    private ImageView ivCloseSearch, ivClearSearch, ivActionIcon;
    private LinearLayout llSuggestion;
    private RecyclerView rvSuggestionsList;
    private View overlayLayout, overlayView, divider;
    private OnDroidFSViewListener onDroidFSViewListener;
    private SuggestionsAdapter suggestionsAdapter;

    private String oldQuery;
    private int overlayBgColor = Color.BLACK;
    private boolean showActionIcon = true;
    private boolean continuousSearch = true;
    private boolean closeSearchViewOnOverlayTouch = true;

    // MARK: Droid Floating Search View Methods

    public DroidFSView(Context context)
    {
        super(context);

        init(context);
    }

    public DroidFSView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init(context);
    }

    public DroidFSView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        overlayLayout = inflate(context, R.layout.overlay, null);
        overlayView = overlayLayout.findViewById(R.id.overlayView);
        overlayView.setBackgroundColor(overlayBgColor);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        overlayLayout.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY) , View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY));
        overlayLayout.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    /**
     * Initializes the views
     *
     * @param context The application context
     */
    private void init(final Context context)
    {
        View rootView = inflate(context, R.layout.droid_fsv_layout, this);

        this.context = context;
        cvDroidFSView = rootView.findViewById(R.id.cvDroidFSView);
        etSearch = rootView.findViewById(R.id.etSearch);
        ivCloseSearch = rootView.findViewById(R.id.ivCloseSearch);
        ivClearSearch = rootView.findViewById(R.id.ivClearSearch);
        ivActionIcon = rootView.findViewById(R.id.ivActionIcon);
        llSuggestion = rootView.findViewById(R.id.llSuggestion);
        rvSuggestionsList = rootView.findViewById(R.id.rvSuggestionsList);
        divider = findViewById(R.id.divider);

        etSearch.setOnKeyboardListener(new OnKeyboardListener()
        {
            @Override
            public void onKeyboardClosed() {

                // Closing search view when keyboard closes
                ivCloseSearch.performClick();
            }
        });

        etSearch.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    if(onDroidFSViewListener != null)
                    {
                        // Sending an update with the search query text
                        onDroidFSViewListener.onSearchText(WordUtils.capitalize(etSearch.getText().toString().toLowerCase()));
                    }

                    if(continuousSearch)
                    {
                        // Clearing out the existing text
                        etSearch.setText("");
                    }
                    else
                    {
                        // Closing search view when "enter" is pressed in keyboard
                        ivCloseSearch.performClick();
                    }

                    return true;
                }

                return false;
            }
        });

        etSearch.setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus)
                {
                    // Activating search view when focused
                    etSearch.performClick();

                    if(onDroidFSViewListener != null)
                    {
                        // Sends an update that the search view has gained focus
                        onDroidFSViewListener.onHasFocus();
                    }
                }
                else
                {
                    // Closing the search view
                    closeSearchView();

                    if(onDroidFSViewListener != null)
                    {
                        // Sends an update that the search view has lost focus
                        onDroidFSViewListener.onLostFocus();
                    }
                }
            }
        });

        etSearch.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if(ivCloseSearch.getVisibility() == View.GONE)
                {
                    Animation slideInRightAnim = AnimationUtils.loadAnimation(context, R.anim.slide_right);
                    slideInRightAnim.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            // Making the close view invisible for it to occupy space
                            // during animation start
                            ivCloseSearch.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // Clearing out the animation
                            etSearch.clearAnimation();

                            // Showing close and clear views
                            ivCloseSearch.setVisibility(View.VISIBLE);

                            // Hiding the action icon
                            ivActionIcon.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    etSearch.clearAnimation();
                    etSearch.setAnimation(slideInRightAnim);

                    // Adding the overlay to parent
                    addOverlayToParent();
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(onDroidFSViewListener != null && oldQuery != null && !oldQuery.equals(etSearch.getText().toString()))
                {
                    // Sending an update with the old query and new query
                    onDroidFSViewListener.onSearchTextChanged(oldQuery, etSearch.getText().toString());

                    // Filtering the suggestions adapter
                    suggestionsAdapter.filterSuggestions(etSearch.getText().toString());
                }

                if(etSearch.getText() == null || etSearch.getText().toString().isEmpty())
                {
                    oldQuery = null;

                    clearSuggestions();
                }
                else
                {
                    oldQuery = etSearch.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Setting the clear search view visibility based on text length
                ivClearSearch.setVisibility(TextUtils.isEmpty(s.toString().trim()) ? View.GONE : View.VISIBLE);
            }
        });

        ivCloseSearch.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {

                // Closing the keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                // Hiding close and clear search views and showing the action icon if applicable
                ivCloseSearch.setVisibility(View.GONE);
                ivClearSearch.setVisibility(View.GONE);
                ivActionIcon.setVisibility(showActionIcon ? View.VISIBLE : View.GONE);

                // Removing the overlay from parent
                removeOverlayFromParent();

                // Clearing the search view text and clearing out the focus
                etSearch.setText("");
                etSearch.clearFocus();

                // Clearing out the suggestions list if applicable
                clearSuggestions();
            }
        });

        ivClearSearch.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {

                // Clearing out the search view text
                etSearch.setText("");

                // Clearing out the suggestions list if applicable
                clearSuggestions();
            }
        });

        ivActionIcon.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if(onDroidFSViewListener != null)
                {
                    // Sending an update that the action item was clicked
                    onDroidFSViewListener.onActionItemClicked();
                }
            }
        });

        suggestionsAdapter = new SuggestionsAdapter(new OnSuggestionsListener()
        {
            @Override
            public void onSuggestionClicked(String suggestion) {

                // Setting the suggestion text in the search view
                etSearch.setText("");
                etSearch.append(suggestion);

                // Clearing out the filter suggestions list
                suggestionsAdapter.clearFilterSuggestions();

                // Hiding the suggestion layout visibility if applicable
                llSuggestion.setVisibility(View.GONE);
            }

            @Override
            public void onSuggestionsFound(boolean suggestionsFound) {

                // Setting the suggestion layout visibility
                llSuggestion.setVisibility(suggestionsFound ? View.VISIBLE : View.GONE);
            }
        });
        rvSuggestionsList.setAdapter(suggestionsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvSuggestionsList.setLayoutManager(linearLayoutManager);
    }

    /**
     * Adds the overlay view to the parent
     */
    private void addOverlayToParent()
    {
        if(overlayLayout != null)
        {
            ViewGroup parentLayout = ((ViewGroup) getParent());
            if(parentLayout != null)
            {
                parentLayout.getOverlay().add(overlayLayout);
                parentLayout.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {

                        if(ivCloseSearch.getVisibility() == View.VISIBLE && closeSearchViewOnOverlayTouch)
                        {
                            // Closing the search view when overlay is clicked
                            ivCloseSearch.performClick();
                        }
                    }
                });
            }
        }
    }

    /**
     * Removes the overlay view from parent
     */
    private void removeOverlayFromParent()
    {
        if(overlayLayout != null)
        {
            ViewGroup parentLayout = ((ViewGroup) getParent());
            if(parentLayout != null)
            {
                parentLayout.getOverlay().remove(overlayLayout);
            }
        }
    }

    /**
     * Clears out the suggestions
     */
    private void clearSuggestions()
    {
        if(suggestionsAdapter != null)
        {
            // Clearing out the filter suggestions list if applicable
            suggestionsAdapter.clearFilterSuggestions();

            // Hiding the suggestion layout visibility if applicable
            llSuggestion.setVisibility(View.GONE);
        }
    }

    // MARK: Listeners

    /**
     * Sets the floating search view listener interface
     *
     * @param onDroidFSViewListener The class instance which implements the interface
     */
    public void setOnDroidFSViewListener(OnDroidFSViewListener onDroidFSViewListener)
    {
        this.onDroidFSViewListener = onDroidFSViewListener;
    }

    // MARK: Public Methods

    /**
     * Closes the search if found open
     */
    public void closeSearchView()
    {
        // Closing the search view
        if(ivCloseSearch.getVisibility() == View.VISIBLE)
        {
            ivCloseSearch.performClick();
        }
    }

    /**
     * Sets the search view suggestions
     *
     * @param suggestions The suggestions list
     */
    public void setSuggestions(ArrayList<String> suggestions)
    {
        suggestionsAdapter.setSuggestions(suggestions);
    }

    /**
     * Sets the search view background color
     *
     * @param color The desired color
     */
    public void setSearchViewBgColor(int color)
    {
        cvDroidFSView.setCardBackgroundColor(color);
    }

    /**
     * Sets the search view text color
     *
     * @param color The desired color
     */
    public void setSearchViewTextColor(int color)
    {
        etSearch.setTextColor(color);

        suggestionsAdapter.setSuggestionTextColor(color);
    }

    /**
     * Sets the search view close icon
     *
     * @param closeIcon The desired right icon
     */
    public void setSearchViewCloseIcon(int closeIcon)
    {
        ivCloseSearch.setImageResource(closeIcon);
    }

    /**
     * Sets the search view close icon color
     *
     * @param color The desired color
     */
    public void setSearchViewCloseIconColor(int color)
    {
        ivCloseSearch.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    /**
     * Sets the search view clear icon
     *
     * @param clearIcon The desired clear icon
     */
    public void setSearchViewClearIcon(int clearIcon)
    {
        ivClearSearch.setImageResource(clearIcon);
    }

    /**
     * Sets the search view clear icon color
     *
     * @param color The desired color
     */
    public void setSearchViewClearIconColor(int color)
    {
        ivClearSearch.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    /**
     * Sets the search view action icon
     *
     * @param rightIcon The desired right icon
     */
    public void setSearchViewActionIcon(int rightIcon)
    {
        ivActionIcon.setImageResource(rightIcon);
    }

    /**
     * Sets the search view action icon color
     *
     * @param color The desired color
     */
    public void setSearchViewActionIconColor(int color)
    {
        ivActionIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    /**
     * Sets the search view hint text color
     *
     * @param color The desired color
     */
    public void setSearchViewHintTextColor(int color)
    {
        etSearch.setHintTextColor(color);
    }

    /**
     * Sets the search view overlay background color
     *
     * @param color The desired color
     */
    public void setSearchViewOverlayBgColor(int color)
    {
        this.overlayBgColor = color;

        if(overlayView != null)
        {
            overlayView.setBackgroundColor(color);
        }
    }

    /**
     * Sets the search view divider color
     *
     * @param color The desired color
     */
    public void setDividerColor(int color)
    {
        divider.setBackgroundColor(color);

        if(suggestionsAdapter != null)
        {
            suggestionsAdapter.setSuggestionDividerColor(color);
        }
    }

    /**
     * Sets the search view hint
     *
     * @param hint The desired hint
     */
    public void setSearchViewHint(String hint)
    {
        if(hint != null)
        {
            etSearch.setHint(hint);
        }
    }

    /**
     * Sets the search view show action icon status
     *
     * NOTE: Default is true
     *
     * @param showActionIcon True - shows action icon, False - if otherwise
     */
    public void setShowSearchViewActionIcon(boolean showActionIcon)
    {
        this.showActionIcon = showActionIcon;

        if(ivCloseSearch.getVisibility() == View.GONE & showActionIcon)
        {
            ivActionIcon.setVisibility(View.VISIBLE);
        }
        else
        {
            ivActionIcon.setVisibility(View.GONE);
        }
    }

    /**
     * Sets the continuous search status
     *
     * NOTE: Default is true
     *
     * @param continuousSearch True - search view will be focused after each search action, False - if otherwise
     */
    public void setContinuousSearch(boolean continuousSearch)
    {
        this.continuousSearch = continuousSearch;
    }

    /**
     * Sets the close search view status when overlay is touched
     *
     * NOTE: Default is true
     *
     * @param closeSearchViewOnOverlayTouch True - search view will be closed when overlay is touched, False - if otherwise
     */
    public void setCloseSearchViewOnOverlayTouch(boolean closeSearchViewOnOverlayTouch)
    {
        this.closeSearchViewOnOverlayTouch = closeSearchViewOnOverlayTouch;
    }
}
