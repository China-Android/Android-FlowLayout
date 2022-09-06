package com.example.androidflowlayoutlibrary;

import android.content.Context;

/**
 * @author HL
 * @Date 2022/9/6
 */
public class dptopx {
    public static int dip2px(Context context,float dpValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
}
