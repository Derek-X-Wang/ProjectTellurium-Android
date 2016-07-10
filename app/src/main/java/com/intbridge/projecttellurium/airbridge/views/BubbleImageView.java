package com.intbridge.projecttellurium.airbridge.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.github.siyamed.shapeimageview.ShaderImageView;
import com.github.siyamed.shapeimageview.shader.ShaderHelper;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.utils.BubbleShader;

/**
 * Chat Bubble Image View
 * Created by Derek on 7/9/2016.
 */
public class BubbleImageView extends ShaderImageView {
    private BubbleShader shader;

    public BubbleImageView(Context context) {
        super(context);
    }

    public BubbleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        shader = new BubbleShader();
        return shader;
    }

    public int getTriangleHeightPx() {
        if(shader != null) {
            return shader.getTriangleHeightPx();
        }
        return 0;
    }

    public void setTriangleHeightPx(final int triangleHeightPx) {
        if(shader != null) {
            shader.setTriangleHeightPx(triangleHeightPx);
            invalidate();
        }
    }

    public BubbleShader.ArrowPosition getArrowPosition() {
        if(shader != null) {
            return shader.getArrowPosition();
        }

        return BubbleShader.ArrowPosition.LEFT;
    }

    public void setArrowPosition(final BubbleShader.ArrowPosition arrowPosition) {
        if(shader != null) {
            shader.setArrowPosition(arrowPosition);
            invalidate();
        }
    }
}
