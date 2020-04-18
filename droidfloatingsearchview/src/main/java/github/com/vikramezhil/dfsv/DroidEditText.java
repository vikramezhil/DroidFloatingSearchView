package github.com.vikramezhil.dfsv;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.KeyEvent;

/**
 * Droid Edit Text View
 *
 * @author Vikram Ezhil
 */

class DroidEditText extends AppCompatEditText {
    private OnKeyboardListener onKeyboardListener;

    public DroidEditText(Context context) {
        super(context);
    }

    public DroidEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DroidEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(onKeyboardListener != null) {
                // Sending an update that the keyboard was closed
                onKeyboardListener.onKeyboardClosed();
            }

            return true;
        }

        return false;
    }

    /**
     * Sets the edit text listener
     *
     * @param onKeyboardListener The class instance which implements the listener
     */
    public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
        this.onKeyboardListener = onKeyboardListener;
    }
}
