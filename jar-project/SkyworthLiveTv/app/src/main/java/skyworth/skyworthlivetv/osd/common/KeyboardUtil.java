package skyworth.skyworthlivetv.osd.common;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;

import skyworth.skyworthlivetv.R;

public class KeyboardUtil {
    private Context ctx;
    private Activity act;
    private KeyboardView keyboardView;
    private Keyboard keyboard;// 数字键盘
    public boolean isnun = false;// 是否数据键盘
    public boolean isupper = false;// 是否大写
    private int curRow = 0;//软键盘焦点所在的行,只有两行,0,1
    private int curFocus = 0;//当前焦点的KEY
    private EditText ed;

    public KeyboardUtil(Activity act, Context ctx, EditText edit) {
        this.act = act;
        this.ctx = ctx;
        this.ed = edit;
        keyboard = new Keyboard(ctx, R.xml.number);
        keyboardView = (KeyboardView) act.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);
    }

    public int getKeyRow() {
        return curRow;
    }

    public void setKeyRow(int keyRow) {
        this.curRow = keyRow;
    }

    public int getCurFocus() {
        return curFocus;
    }

    public void setCurFocus(int curFocus) {
        this.curFocus = curFocus;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public KeyboardView getKeyboardView() {
        return keyboardView;
    }

    public void setKeyboardView(KeyboardView keyboardView) {
        this.keyboardView = keyboardView;
    }

    public EditText getEd() {
        return ed;
    }

    public void setEd(EditText ed) {
        this.ed = ed;
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Log.i("yangjianjun", "Keyboard onKey........."+primaryCode);
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
                changeKey();
                keyboardView.setKeyboard(keyboard);
            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {// 数字键盘切换
                if (isnun) {
                    isnun = false;
                    keyboardView.setKeyboard(keyboard);
                } else {
                    isnun = true;
                    keyboardView.setKeyboard(keyboard);
                }
            } else if (primaryCode == 57419) { // go left
                if (start > 0) {
                    ed.setSelection(start - 1);
                }
            } else if (primaryCode == 57421) { // go right
                if (start < ed.length()) {
                    ed.setSelection(start + 1);
                }
            } else {
                Log.i("yangjianjun", "Keyboard insert.........");
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    };

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Key> keylist = keyboard.getKeys();
        if (isupper) {//大写切换小写
            isupper = false;
            for(Key key:keylist){
                if (key.label!=null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0]+32;
                }
            }
        } else {//小写切换大写
            isupper = true;
            for(Key key:keylist){
                if (key.label!=null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0]-32;
                }
            }
        }
    }

    /** Returns whether the CustomKeyboard is visible. */
    public boolean isKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    public void showKeyboard(View v) {
        int visibility = keyboardView.getVisibility();
        if (visibility != View.VISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
            keyboardView.setFocusable(true);
            //keyboardView.requestFocus();
            //Animation animation = AnimationUtils.loadAnimation(ctx,R.anim.keyboard_in);
            //this.ed.setAnimation(animation);
            keyboard.getKeys().get(0).onPressed();
            curFocus = 0;
            curRow = 0;
            if( v!=null ){
                ((InputMethodManager)act.getSystemService(Activity.INPUT_METHOD_SERVICE))
                                            .hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            //keyboard.getKeys().get(11).onPressed();
            //keyboard.getKeys().get(12).onPressed();
            //listener.onKey(keyboard.getKeys().get(0).codes[0], null);
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.GONE);
            keyboardView.setEnabled(false);
        }
    }

    private boolean isword(String str){
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase())>-1) {
            return true;
        }
        return false;
    }

    public void registerEditText(EditText ed) {
        // Find the EditText 'resid'
        EditText edittext= ed;
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showKeyboard(v);
                } else {
                    hideKeyboard();
                }
            }
        });
        edittext.setOnClickListener(new View.OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override public void onClick(View v) {
                showKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }
}