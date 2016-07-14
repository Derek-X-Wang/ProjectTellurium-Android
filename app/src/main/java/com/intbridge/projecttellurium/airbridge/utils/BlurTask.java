package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * utils for Blur
 * Created by Derek on 7/7/2016.
 */
public class BlurTask {

    public interface Callback {
        void done(BitmapDrawable drawable);
    }

    private Resources res;
    private WeakReference<Context> contextWeakRef;
    private BlurHelper.BlurFactor factor;
    private Callback callback;
    private static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    public BlurTask(View target, BlurHelper.BlurFactor factor, Callback callback) {
        this.res = target.getResources();
        this.factor = factor;
        this.callback = callback;

        contextWeakRef = new WeakReference<>(target.getContext());
    }

    public void execute() {
        THREAD_POOL.execute(new Runnable() {
            @Override public void run() {
                Context context = contextWeakRef.get();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = factor.sampling;
                Bitmap bitmap;
                if(factor.path == null) {
                    bitmap = BitmapFactory.decodeResource(res, factor.imgId, options);
                } else {
                    bitmap = BitmapFactory.decodeFile(factor.path,options);
                }

                Bitmap newImg = Blur.fastblur(context, bitmap, factor.sampling);
                final BitmapDrawable bitmapDrawable = new BitmapDrawable(res, newImg);
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override public void run() {
                            callback.done(bitmapDrawable);
                        }
                    });
                }
            }
        });
    }
}
