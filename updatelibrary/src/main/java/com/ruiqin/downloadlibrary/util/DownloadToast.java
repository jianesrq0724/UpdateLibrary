package com.ruiqin.downloadlibrary.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public class DownloadToast {
    private static Toast mShortToast;
    private static Toast mLongToast;

    /**
     * Toast短
     *
     * @param context
     * @param text
     */
    public static void showShort(Context context, String text) {
        if (mShortToast == null) {
            mShortToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mShortToast.setText(text);
        }
        mShortToast.show();
    }

    /**
     * Toast长
     *
     * @param context
     * @param text
     */
    public static void showLong(Context context, String text) {
        if (mLongToast == null) {
            mLongToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            mLongToast.setText(text);
        }
        mLongToast.show();
    }
}
