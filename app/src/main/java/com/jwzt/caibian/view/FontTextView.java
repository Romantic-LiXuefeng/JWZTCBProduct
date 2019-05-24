package com.jwzt.caibian.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jwzt.caibian.util.FontUtils;

/**
 * Created by Administrator on 2015/10/27.
 */
public class FontTextView  extends TextView {
    /** The file name of the font data in the assets directory*/
    private static String mFontPath = "fonts/fzsongti.ttf";

    public FontTextView(Context context) {
        super(context);
        init(context, null, 0);
    }
    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }
    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public String getFontPath() {
        return mFontPath;
    }

    /**
     * <p>Set font file fontPath</p>
     * @param fontPath The file name of the font data in the assets directory
     */
    public void setFontPath(String fontPath) {
        mFontPath = fontPath;

        if (!TextUtils.isEmpty(mFontPath)) {
            FontUtils.getInstance().replaceFontFromAsset(this, mFontPath);
        }
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FontTextView, defStyleAttr, 0);
//        mFontPath = typedArray.getString(R.styleable.FontTextView_font_path);
//        mFontPath=;
//        typedArray.recycle();
//
        if (!TextUtils.isEmpty(mFontPath)) {
            FontUtils.getInstance().replaceFontFromAsset(this, mFontPath);
        }
    }
}
