package com.yellowsoft.radioapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MyTextView extends android.widget.TextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        if (!isInEditMode()) {
            if(Settings.get_user_language(context).equals("ar")) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/GE_Flow_Regular.ttf");
                setTypeface(tf);
            }
            else
            {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto_Regular.ttf");
                setTypeface(tf);
            }

        }

    }


}