package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import com.intbridge.projecttellurium.airbridge.R;

/**
 * utils for Blur
 * Created by Derek on 7/7/2016.
 */
public class BlurHelper {
    public static Composer with(Context context) {
        return new Composer(context);
    }

    public static class Composer {

        private Context context;
        private BlurFactor factor;
        private boolean async = false;


        public Composer(Context context) {
            factor = new BlurFactor();
            this.context = context;
        }

        public Composer radius(int radius) {
            factor.radius = radius;
            return this;
        }

        public Composer sampling(int sampling) {
            factor.sampling = sampling;
            return this;
        }

        public Composer async() {
            async = true;
            return this;
        }

        public Composer image(int id) {
            factor.imgId = id;
            return this;
        }

        public void setBackground(final View target) {
            factor.width = target.getMeasuredWidth();
            factor.height = target.getMeasuredHeight();

            if (async) {
                BlurTask task = new BlurTask(target, factor, new BlurTask.Callback() {
                    @Override
                    public void done(BitmapDrawable drawable) {
                        setBackground(target, drawable);
                    }
                });
                task.execute();
            } else {

                Resources res = context.getResources();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = factor.sampling;
                Bitmap bitmap = BitmapFactory.decodeResource(res, factor.imgId, options);
                Bitmap newImg = Blur.fastblur(context, bitmap, factor.radius);
                Drawable d = new BitmapDrawable(res, newImg);
                setBackground(target, d);
            }
        }

        private void setBackground(View v, Drawable d) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                v.setBackground(d);
            } else {
                v.setBackgroundDrawable(d);
            }
        }
    }
}

class BlurFactor {

    public static final int DEFAULT_RADIUS = 25;
    public static final int DEFAULT_SAMPLING = 1;

    public int width;
    public int height;
    public int radius = DEFAULT_RADIUS;
    public int sampling = DEFAULT_SAMPLING;
    public int imgId;
}
