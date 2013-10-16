package com.on.puz.quicktron;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import java.util.Hashtable;

public class Button2 extends Button {
    private static final String TAG = "Button";
    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public Button2(Context context) {
        super(context);
    }

    public Button2(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public Button2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Button2);
        String customFont = a.getString(R.styleable.Button2_customFontButton);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public Typeface getType(Context c, String assetPath) {
        Typeface tf;
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(),
                            assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    Log.e(TAG, "Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            tf = cache.get(assetPath);
        }
        return tf;
    }

    public boolean setCustomFont(Context c, String assetPath) {
        try {
            setTypeface(getType(c, assetPath));
        } catch (Exception e) {
            Log.wtf(TAG, "Unable to setTypeface");
            return false;
        }
        return true;
    }
}

