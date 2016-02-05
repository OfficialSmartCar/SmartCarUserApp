package com.moin.smartcar.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by macpro on 12/14/15.
 */
public class MoinUtils {
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    public String Package = "MoinIsAwesome";

    public static MoinUtils my_myUtil;

    private MoinUtils() {

    }

    public static MoinUtils getReference() {
        if (my_myUtil == null) {
            my_myUtil = new MoinUtils();
        }
        return my_myUtil;
    }

    public void showMessage(Context context, String message) {

        SnackbarManager.show(
                Snackbar.with(context)
                        .text(message));
    }

    public void setGlassyBitmap(final ImageView img){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap input = ((BitmapDrawable)img.getDrawable()).getBitmap();

                int w = input.getWidth();
                int h = input.getHeight();
                RectF rectF = new RectF(w, h, w, h);
                float blurRadius = 200.0f;
                Bitmap bitmapResult = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvasResult = new Canvas(bitmapResult);
                Paint blurPaintOuter = new Paint();
                blurPaintOuter.setColor(0xFFffffff);
                blurPaintOuter.setMaskFilter(new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.INNER));
                canvasResult.drawBitmap(input, 0, 0, blurPaintOuter);
                Paint blurPaintInner = new Paint();
                blurPaintInner.setColor(0xFFffffff);
                blurPaintInner.setMaskFilter(new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.OUTER));
                canvasResult.drawRect(rectF, blurPaintInner);
                img.setImageBitmap(bitmapResult);
            }
        }).run();


    }
}
