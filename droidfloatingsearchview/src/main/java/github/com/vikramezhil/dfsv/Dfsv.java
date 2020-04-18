package github.com.vikramezhil.dfsv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.vikramezhil.dfsv.R;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Droid Floating Search View
 *
 * @author Vikram Ezhil
 */

public class Dfsv extends LinearLayout {

    private Context context;
    private CardView cvDroidFSView;
    private DroidEditText etSearch;
    private ImageView ivCloseSearch, ivClearSearch, ivActionIcon;
    private LinearLayout llSuggestion;
    private RecyclerView rvSuggestionsList;
    private View overlayLayout, overlayView, divider;
    private OnDfsvListener onDfsvListener;
    private SuggestionsAdapter suggestionsAdapter;

    private String oldQuery;
    private int overlayBgColor = Color.BLACK;
    private boolean showActionIcon = true;
    private boolean continuousSearch = true;
    private boolean closeSearchViewOnOverlayTouch = true;
    private boolean customSuggestionAdapter = false;

    // MARK: Droid Floating Search View Methods

    public Dfsv(Context context) {
        super(context);

        init(context, null);
    }

    public Dfsv(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public Dfsv(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        overlayLayout = inflate(context, R.layout.overlay, null);
        overlayView = overlayLayout.findViewById(R.id.overlayView);
        overlayView.setBackgroundColor(overlayBgColor);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        overlayLayout.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY) , View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY));
        overlayLayout.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    /**
     * Initializes the views
     *
     * @param context The application context
     * @param attrs The view attributes
     */
    private void init(final Context context, AttributeSet attrs) {
        View rootView = inflate(context, R.layout.layout_dfsv, this);

        this.context = context;
        cvDroidFSView = rootView.findViewById(R.id.cvDroidFSView);
        etSearch = rootView.findViewById(R.id.etSearch);
        ivCloseSearch = rootView.findViewById(R.id.ivCloseSearch);
        ivClearSearch = rootView.findViewById(R.id.ivClearSearch);
        ivActionIcon = rootView.findViewById(R.id.ivActionIcon);
        llSuggestion = rootView.findViewById(R.id.llSuggestion);
        rvSuggestionsList = rootView.findViewById(R.id.rvSuggestionsList);
        divider = findViewById(R.id.divider);

        etSearch.setOnKeyboardListener(new OnKeyboardListener() {
            @Override
            public void onKeyboardClosed() {
                // Closing search view when keyboard closes
                ivCloseSearch.performClick();
            }
        });

        etSearch.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(onDfsvListener != null) {
                        // Sending an update with the search query text
                        onDfsvListener.onSearchText(WordUtils.capitalize(Objects.requireNonNull(etSearch.getText()).toString().toLowerCase()));
                    }

                    if(continuousSearch) {
                        // Clearing out the existing text
                        etSearch.setText("");
                    } else {
                        // Closing search view when "enter" is pressed in keyboard
                        ivCloseSearch.performClick();
                    }

                    return true;
                }

                return false;
            }
        });

        etSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // Activating search view when focused
                    etSearch.performClick();

                    if(onDfsvListener != null) {
                        // Sends an update that the search view has gained focus
                        onDfsvListener.onHasFocus();
                    }
                } else {
                    // Closing the search view
                    closeSearchView();

                    if(onDfsvListener != null) {
                        // Sends an update that the search view has lost focus
                        onDfsvListener.onLostFocus();
                    }
                }
            }
        });

        etSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ivCloseSearch.getVisibility() == View.GONE) {
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
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    etSearch.clearAnimation();
                    etSearch.setAnimation(slideInRightAnim);

                    // Adding the overlay to parent
                    addOverlayToParent();
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(onDfsvListener != null && oldQuery != null && !oldQuery.equals(Objects.requireNonNull(etSearch.getText()).toString())) {
                    // Sending an update with the old query and new query
                    onDfsvListener.onSearchTextChanged(oldQuery, etSearch.getText().toString());

                    if (!customSuggestionAdapter) {
                        // Filtering the suggestions adapter
                        suggestionsAdapter.filterSuggestions(etSearch.getText().toString());
                    }
                }

                if(etSearch.getText() == null || etSearch.getText().toString().isEmpty()) {
                    oldQuery = null;

                    clearSuggestions();
                } else {
                    oldQuery = etSearch.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Setting the clear search view visibility based on text length
                ivClearSearch.setVisibility(TextUtils.isEmpty(s.toString().trim()) ? View.GONE : View.VISIBLE);
            }
        });

        ivCloseSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Closing the keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputMethodManager != null;
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

        ivClearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clearing out the search view text
                etSearch.setText("");

                // Clearing out the suggestions list if applicable
                clearSuggestions();
            }
        });

        ivActionIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDfsvListener != null) {
                    // Sending an update that the action item was clicked
                    onDfsvListener.onActionItemClicked();
                }
            }
        });

        suggestionsAdapter = new SuggestionsAdapter(new OnSuggestionsListener() {
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

        // Initializing view attributes
        initViewAttributes(attrs);
    }

    /**
     * Initializes the view attributes
     * @param attrs The view attributes
     */
    private void initViewAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Dfsv, 0, 0);

            try {
                setSearchViewCloseIcon(typedArray.getInt(R.styleable.Dfsv_dfsvCloseIcon, R.drawable.ic_back));
                setSearchViewClearIcon(typedArray.getInt(R.styleable.Dfsv_dfsvCloseIcon, R.drawable.ic_close));
                setSearchViewActionIcon(typedArray.getInt(R.styleable.Dfsv_dfsvCloseIcon, R.drawable.ic_action));

                setSearchViewBgColor(typedArray.getInt(R.styleable.Dfsv_dfsvBgColor, Color.BLACK));
                setSearchViewOverlayBgColor(typedArray.getInt(R.styleable.Dfsv_dfsvOverlayBgColor, Color.BLACK));
                setSearchViewTextColor(typedArray.getInt(R.styleable.Dfsv_dfsvTextColor, Color.WHITE));
                setSearchViewIconsColor(typedArray.getInt(R.styleable.Dfsv_dfsvIconsColor, Color.WHITE));
                setSearchViewHintTextColor(typedArray.getInt(R.styleable.Dfsv_dfsvHintTextColor, Color.WHITE));
                setDividerColor(typedArray.getInt(R.styleable.Dfsv_dfsvIconsColor, Color.WHITE));

                setSearchViewHint(typedArray.getString(R.styleable.Dfsv_dfsvHintText));

                setContinuousSearch(typedArray.getBoolean(R.styleable.Dfsv_dfsvContinuousSearch, false));
                setCloseSearchViewOnOverlayTouch(typedArray.getBoolean(R.styleable.Dfsv_dfsvCloseSearchOnOverlayTouch, false));

                setCornerRadius(typedArray.getFloat(R.styleable.Dfsv_dfsvCornerRadius, 0));
                setElevation(typedArray.getFloat(R.styleable.Dfsv_dfsvElevation, 2));

                String[] suggestions = typedArray.getResources().getStringArray(typedArray.getResourceId(R.styleable.Dfsv_dfsvSuggestions, 0));
                if (suggestions.length > 0) { setSuggestions(new ArrayList<>(Arrays.asList(suggestions))); }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                typedArray.recycle();
            }
        }
    }
    /**
     * Adds the overlay view to the parent
     */
    private void addOverlayToParent() {
        if(overlayLayout != null) {
            ViewGroup parentLayout = ((ViewGroup) getParent());
            if(parentLayout != null) {
                parentLayout.getOverlay().add(overlayLayout);
                parentLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ivCloseSearch.getVisibility() == View.VISIBLE && closeSearchViewOnOverlayTouch) {
                            // Closing the search view when overlay is clicked
                            ivCloseSearch.performClick();
                        }
                    }
                });
            }
        }
    }

    /**
     * Clears out the suggestions
     */
    private void clearSuggestions() {
        if(suggestionsAdapter != null) {
            // Clearing out the filter suggestions list if applicable
            suggestionsAdapter.clearFilterSuggestions();
        }

        // Hiding the suggestion layout visibility if applicable
        llSuggestion.setVisibility(View.GONE);
    }

    /**
     * Removes the overlay view from parent
     */
    private void removeOverlayFromParent() {
        if(overlayLayout != null) {
            ViewGroup parentLayout = ((ViewGroup) getParent());
            if(parentLayout != null) {
                parentLayout.getOverlay().remove(overlayLayout);
            }
        }
    }

    // MARK: Listeners

    /**
     * Sets the floating search view listener interface
     *
     * @param onDfsvListener The class instance which implements the interface
     */
    public void setOnDfsvListener(OnDfsvListener onDfsvListener) {
        this.onDfsvListener = onDfsvListener;
    }

    // MARK: Public Methods

    /**
     * Sets the search view corner radius
     * @param radius The radius value
     */
    public void setCornerRadius(float radius) {
        cvDroidFSView.setRadius(radius);
    }

    /**
     * Sets the search view elevation
     * @param elevation The elevation value
     */
    public void setElevation(float elevation) {
        cvDroidFSView.setCardElevation(elevation);
    }

    /**
     * Shows the suggestions
     */
    public void showSuggestions() {
        llSuggestion.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the suggestions
     */
    public void hideSuggestions() {
        llSuggestion.setVisibility(View.GONE);
    }

    /**
     * Closes the search if found open
     */
    public void closeSearchView() {
        // Closing the search view
        if(ivCloseSearch.getVisibility() == View.VISIBLE) {
            ivCloseSearch.performClick();
        }
    }

    /**
     * Sets the custom suggestions adapter
     * @param adapter The custom suggestion adapter
     */
    public void setCustomSuggestionsAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            customSuggestionAdapter = true;
            rvSuggestionsList.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            rvSuggestionsList.setLayoutManager(linearLayoutManager);
        }
    }

    /**
     * Sets the search view suggestions
     *
     * @param suggestions The suggestions list
     */
    public void setSuggestions(ArrayList<String> suggestions) {
        customSuggestionAdapter = false;
        rvSuggestionsList.setAdapter(suggestionsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvSuggestionsList.setLayoutManager(linearLayoutManager);
        suggestionsAdapter.setSuggestions(suggestions);
    }

    /**
     * Sets the search view background color
     *
     * @param color The desired color
     */
    public void setSearchViewBgColor(int color) {
        cvDroidFSView.setCardBackgroundColor(color);
    }

    /**
     * Sets the search view text color
     *
     * @param color The desired color
     */
    public void setSearchViewTextColor(int color) {
        etSearch.setTextColor(color);
        suggestionsAdapter.setSuggestionTextColor(color);
    }

    /**
     * Sets the search view close icon
     *
     * @param closeIcon The desired right icon
     */
    public void setSearchViewCloseIcon(int closeIcon) {
        ivCloseSearch.setImageResource(closeIcon);
    }

    /**
     * Sets the search view clear icon
     *
     * @param clearIcon The desired clear icon
     */
    public void setSearchViewClearIcon(int clearIcon) {
        ivClearSearch.setImageResource(clearIcon);
    }


    /**
     * Sets the search view action icon
     *
     * @param rightIcon The desired right icon
     */
    public void setSearchViewActionIcon(int rightIcon) {
        ivActionIcon.setImageResource(rightIcon);
    }

    /**
     * Sets the search view icons color
     *
     * @param color The desired color
     */
    public void setSearchViewIconsColor(int color) {
        ivCloseSearch.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        ivClearSearch.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        ivActionIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    /**
     * Sets the search view hint text color
     *
     * @param color The desired color
     */
    public void setSearchViewHintTextColor(int color) {
        etSearch.setHintTextColor(color);
    }

    /**
     * Sets the search view overlay background color
     *
     * @param color The desired color
     */
    public void setSearchViewOverlayBgColor(int color) {
        this.overlayBgColor = color;
        if(overlayView != null) {
            overlayView.setBackgroundColor(color);
        }
    }

    /**
     * Sets the search view divider color
     *
     * @param color The desired color
     */
    public void setDividerColor(int color) {
        divider.setBackgroundColor(color);
        if(suggestionsAdapter != null) {
            suggestionsAdapter.setSuggestionDividerColor(color);
        }
    }

    /**
     * Sets the search view hint
     *
     * @param hint The desired hint
     */
    public void setSearchViewHint(String hint) {
        if(hint != null) {
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
    public void setShowSearchViewActionIcon(boolean showActionIcon) {
        this.showActionIcon = showActionIcon;
        if(ivCloseSearch.getVisibility() == View.GONE & showActionIcon) {
            ivActionIcon.setVisibility(View.VISIBLE);
        } else {
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
    public void setContinuousSearch(boolean continuousSearch) {
        this.continuousSearch = continuousSearch;
    }

    /**
     * Sets the close search view status when overlay is touched
     *
     * NOTE: Default is true
     *
     * @param closeSearchViewOnOverlayTouch True - search view will be closed when overlay is touched, False - if otherwise
     */
    public void setCloseSearchViewOnOverlayTouch(boolean closeSearchViewOnOverlayTouch) {
        this.closeSearchViewOnOverlayTouch = closeSearchViewOnOverlayTouch;
    }
}
