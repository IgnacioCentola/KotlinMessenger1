package com.nacho.kotlinmessenger;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;

public class KeyboardManager extends AppCompatActivity {

    private static InputMethodManager imm;

    public void hideKeyboard(){


        if (getCurrentFocus() != null) {
            imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(View view) {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        imm.showSoftInput(view, 0);
    }
}
